"""
VPP电力交易策略算法  -  重构版本
消除硬编码，提高代码质量和可维护性

@author: VPP Team
@version: 2.1.0
@date: 2024  - 01  - 01
"""

import datetime
from dataclasses import dataclass
from enum import Enum
from typing import Any, Dict, List, Optional, Tuple

import cvxpy as cp
import numpy as np
import pandas as pd
import psycopg2
from flask import Flask, jsonify, request
from flask_executor import Executor

from common.exceptions import (
    DatabaseConnectionException,
    OptimizationException,
)
from common.log_util import get_logger

logger = get_logger(__name__)


class StrategyType(Enum):
    """策略类型枚举"""

    ENERGY_STORAGE = "energy_storage"
    POWER_TRADING = "power_trading"


@dataclass
class DatabaseConfig:
    """数据库配置"""

    host: str
    port: int
    database: str
    user: str
    password: str

    @classmethod
    def from_env(cls) -> 'DatabaseConfig':
        """从环境变量创建配置"""
        import os

        return cls(
            host=os.getenv('DB_HOST', 'localhost'),
            port=int(os.getenv('DB_PORT', '5432')),
            database=os.getenv('DB_NAME', 'vpp_algorithm'),
            user=os.getenv('DB_USER', 'postgres'),
            password=os.getenv('DB_PASSWORD', ''),
        )


@dataclass
class OptimizationConfig:
    """优化算法配置"""

    max_charge_power: float = 200.0  # kW
    min_charge_power: float = -200.0  # kW
    max_soc: float = 430.0  # kWh
    min_soc: float = 0.0  # kWh
    efficiency: float = 1.0  # 充放电效率
    energy_loss_rate: float = 15.0 / 96  # 每个时段的能量损耗率

    # 价格矫正系数
    valley_correction: float = 0.0001
    flat_correction: float = 0.0001
    peak_correction: float = -0.0015
    top_correction: float = -0.0015


@dataclass
class NodeConfig:
    """节点配置"""

    node_id: str
    system_id: str
    node_name: str
    capacity_kwh: float
    max_power_kw: float


class DatabaseManager:
    """数据库管理器"""

    def __init__(self, config: DatabaseConfig):
        self.config = config
        self._connection = None

    def __enter__(self):
        """上下文管理器入口"""
        try:
            self._connection = psycopg2.connect(
                host=self.config.host,
                port=self.config.port,
                database=self.config.database,
                user=self.config.user,
                password=self.config.password,
            )
            return self._connection
        except Exception as e:
            raise DatabaseConnectionException(
                database_url=f"{self.config.host}:{self.config.port}/{self.config.database}",
                cause=e,
            )

    def __exit__(self, exc_type, exc_val, exc_tb):
        """上下文管理器退出"""
        if self._connection:
            self._connection.close()

    def fetch_weather_data(
        self, start_time: datetime.datetime, end_time: datetime.datetime
    ) -> pd.DataFrame:
        """获取天气数据"""
        query = """
            SELECT  *  FROM weather_forecast
            WHERE timestamp BETWEEN %s AND %s
            ORDER BY timestamp
        """

        with self as conn:
            cursor = conn.cursor()
            cursor.execute(query, (start_time, end_time))
            rows = cursor.fetchall()
            columns = [desc[0] for desc in cursor.description]

        return pd.DataFrame(rows, columns=columns)

    def fetch_load_forecast(
        self, node_id: str, start_time: datetime.datetime, end_time: datetime.datetime
    ) -> pd.DataFrame:
        """获取负荷预测数据"""
        query = """
            SELECT timestamp,  predicted_load,  actual_load
            FROM load_forecast
            WHERE node_id  =  %s AND timestamp BETWEEN %s AND %s
            ORDER BY timestamp
        """

        with self as conn:
            cursor = conn.cursor()
            cursor.execute(query, (node_id, start_time, end_time))
            rows = cursor.fetchall()
            columns = [desc[0] for desc in cursor.description]

        return pd.DataFrame(rows, columns=columns)

    def fetch_price_data(self, date: datetime.date) -> pd.DataFrame:
        """获取电价数据"""
        query = """
            SELECT hour,  price_hour,  property
            FROM electricity_price
            WHERE date  =  %s
            ORDER BY hour
        """

        with self as conn:
            cursor = conn.cursor()
            cursor.execute(query, (date,))
            rows = cursor.fetchall()
            columns = [desc[0] for desc in cursor.description]

        return pd.DataFrame(rows, columns=columns)


class DataProcessor:
    """数据处理器"""

    @staticmethod
    def preprocess_weather_data(df: pd.DataFrame) -> Dict[str, float]:
        """预处理天气数据"""
        if df.empty:
            logger.warning("Weather data is empty")
            return {"temperature": 25.0, "humidity": 60.0}

        # 计算平均温度
        avg_temp = df['temperature'].mean() if 'temperature' in df.columns else 25.0
        avg_humidity = df['humidity'].mean() if 'humidity' in df.columns else 60.0

        return {"temperature": float(avg_temp), "humidity": float(avg_humidity)}

    @staticmethod
    def prepare_price_array(df_price: pd.DataFrame) -> Tuple[np.ndarray, np.ndarray]:
        """准备价格数组"""
        if len(df_price) != 24:
            raise ValueError(f"Price data should have 24 hours,  got {len(df_price)}")

        # 生成每15分钟的价格数组
        price_15min = np.array(
            [
                price
                for price in df_price['price_hour']
                for _ in range(4)  # 每小时4个15分钟
            ]
        )

        # 生成峰谷平标识数组
        property_15min = np.array(
            [prop for prop in df_price['property'] for _ in range(4)]
        )

        return price_15min, property_15min

    @staticmethod
    def validate_load_data(df: pd.DataFrame) -> pd.DataFrame:
        """验证和清洗负荷数据"""
        # 删除空值
        df_clean = df.dropna()

        # 删除负值
        df_clean = df_clean[df_clean['predicted_load'] >= 0]

        # 检查数据量
        if len(df_clean) < len(df) * 0.8:
            logger.warning(f"Removed {len(df)  -  len(df_clean)} invalid load records")

        return df_clean


class OptimizationEngine:
    """优化引擎"""

    def __init__(self, config: OptimizationConfig):
        self.config = config

    def optimize_energy_storage(
        self,
        demand_load: np.ndarray,
        electricity_price: np.ndarray,
        property_array: np.ndarray,
        initial_soc: float = 0.0,
    ) -> Tuple[np.ndarray, np.ndarray]:
        """
        储能优化算法

        Args:
            demand_load: 需求负荷数组 (96个点)
            electricity_price: 电价数组 (96个点)
            property_array: 峰谷平标识数组 (96个点)
            initial_soc: 初始SOC

        Returns:
            (充放电功率数组,  SOC数组)
        """
        try:
            # 定义优化变量
            charge_discharge_power = cp.Variable(96)  # 充放电功率 (正为放电，负为充电)
            soc = cp.Variable(96)  # 储能SOC

            # 目标函数：最大化收益
            profit = charge_discharge_power @ electricity_price * self.config.efficiency

            # 添加价格矫正项
            profit += self._add_price_corrections(soc, property_array)

            objective = cp.Maximize(profit)

            # 约束条件
            constraints = self._build_constraints(
                charge_discharge_power, soc, demand_load, initial_soc
            )

            # 求解优化问题
            problem = cp.Problem(objective, constraints)
            result = problem.solve(verbose=False, solver=cp.CLARABEL)

            if problem.status not in ["infeasible", "unbounded"]:
                return charge_discharge_power.value, soc.value
            else:
                raise OptimizationException(
                    node_id="unknown",
                    algorithm_name="energy_storage_optimization",
                    message=f"Optimization failed with status: {problem.status}",
                )

        except Exception as e:
            raise OptimizationException(
                node_id="unknown", algorithm_name="energy_storage_optimization", cause=e
            )

    def _add_price_corrections(
        self, soc: cp.Variable, property_array: np.ndarray
    ) -> cp.Expression:
        """添加价格矫正项"""
        correction = 0

        # 根据峰谷平时段添加不同的矫正系数
        for i in range(96):
            if property_array[i] == 'valley':
                correction += self.config.valley_correction * soc[i]
            elif property_array[i] == 'flat':
                correction += self.config.flat_correction * soc[i]
            elif property_array[i] == 'peak':
                correction += self.config.peak_correction * soc[i]
            elif property_array[i] == 'top':
                correction += self.config.top_correction * soc[i]

        return correction

    def _build_constraints(
        self,
        charge_discharge_power: cp.Variable,
        soc: cp.Variable,
        demand_load: np.ndarray,
        initial_soc: float,
    ) -> List[cp.Constraint]:
        """构建约束条件"""
        constraints = []

        # 充放电功率限制
        constraints.append(charge_discharge_power <= self.config.max_charge_power)
        constraints.append(charge_discharge_power >= self.config.min_charge_power)

        # 放电功率不能超过需求负荷
        constraints.append(
            charge_discharge_power * self.config.efficiency <= demand_load
        )

        # SOC约束
        constraints.append(soc <= self.config.max_soc)
        constraints.append(soc >= self.config.min_soc)

        # SOC动态平衡约束
        for i in range(96):
            if i == 0:
                constraints.append(
                    soc[i]
                    == initial_soc
                    - charge_discharge_power[i] * 0.25 * self.config.efficiency
                )
            else:
                constraints.append(
                    soc[i]
                    == soc[i - 1]
                    - charge_discharge_power[i] * 0.25 * self.config.efficiency
                )

            # 考虑能量损耗的最低电量约束
            constraints.append(soc[i] >= self.config.energy_loss_rate * (i + 1))

        return constraints


class TradingStrategyService:
    """交易策略服务"""

    def __init__(
        self, db_config: DatabaseConfig, optimization_config: OptimizationConfig
    ):
        self.db_manager = DatabaseManager(db_config)
        self.optimizer = OptimizationEngine(optimization_config)
        self.data_processor = DataProcessor()

    def generate_daily_strategy(
        self, target_date: datetime.date, nodes: List[NodeConfig]
    ) -> Dict[str, Any]:
        """生成日策略"""
        try:
            logger.info(f"Generating strategy for date: {target_date}")

            # 时间范围
            start_time = datetime.datetime.combine(target_date, datetime.time(0, 0))
            end_time = datetime.datetime.combine(target_date, datetime.time(23, 45))

            # 获取基础数据
            weather_data = self.db_manager.fetch_weather_data(start_time, end_time)
            price_data = self.db_manager.fetch_price_data(target_date)

            # 处理天气数据
            weather_features = self.data_processor.preprocess_weather_data(weather_data)

            # 处理价格数据
            price_15min, property_15min = self.data_processor.prepare_price_array(
                price_data
            )

            strategies = []

            # 为每个节点生成策略
            for node in nodes:
                # 获取负荷预测
                load_data = self.db_manager.fetch_load_forecast(
                    node.node_id, start_time, end_time
                )

                # 数据验证和清洗
                load_data_clean = self.data_processor.validate_load_data(load_data)

                if load_data_clean.empty:
                    logger.warning(f"No valid load data for node {node.node_id}")
                    continue

                # 插值到15分钟间隔
                demand_load = self._interpolate_load_data(
                    load_data_clean, start_time, end_time
                )

                # 执行优化
                charge_discharge_power, soc = self.optimizer.optimize_energy_storage(
                    demand_load, price_15min, property_15min
                )

                # 格式化输出
                strategy = self._format_strategy_output(
                    node, target_date, charge_discharge_power, soc
                )
                strategies.append(strategy)

            return {
                "date": target_date.strftime("%Y-%m-%d"),
                "weather": weather_features,
                "strategies": strategies,
                "metadata": {
                    "generated_at": datetime.datetime.now().isoformat(),
                    "version": "2.1.0",
                },
            }

        except Exception as e:
            logger.error(f"Failed to generate strategy: {e}")
            raise

    def _interpolate_load_data(
        self,
        load_data: pd.DataFrame,
        start_time: datetime.datetime,
        end_time: datetime.datetime,
    ) -> np.ndarray:
        """插值负荷数据到15分钟间隔"""
        # 生成15分钟时间序列
        time_series = pd.date_range(start_time, end_time, freq='15min')

        # 创建完整的时间序列DataFrame
        df_complete = pd.DataFrame({'timestamp': time_series})

        # 合并数据
        df_merged = pd.merge(df_complete, load_data, on='timestamp', how='left')

        # 插值填充缺失值
        df_merged['predicted_load'] = df_merged['predicted_load'].interpolate()

        # 返回numpy数组
        return df_merged['predicted_load'].values

    def _format_strategy_output(
        self, node: NodeConfig, date: datetime.date, power: np.ndarray, soc: np.ndarray
    ) -> Dict[str, Any]:
        """格式化策略输出"""
        # 生成时间戳序列
        start_time = datetime.datetime.combine(date, datetime.time(0, 0))
        timestamps = [
            (start_time + datetime.timedelta(minutes=i * 15)).strftime("%H:%M")
            for i in range(96)
        ]

        return {
            "node_id": node.node_id,
            "node_name": node.node_name,
            "date": date.strftime("%Y-%m-%d"),
            "strategy_points": [
                {
                    "time": timestamps[i],
                    "power_kw": float(power[i]),
                    "soc_kwh": float(soc[i]),
                    "soc_percentage": float(soc[i] / node.capacity_kwh * 100),
                }
                for i in range(96)
            ],
            "summary": {
                "total_charge_kwh": float(np.sum(power[power < 0]) * -0.25),
                "total_discharge_kwh": float(np.sum(power[power > 0]) * 0.25),
                "max_soc": float(np.max(soc)),
                "min_soc": float(np.min(soc)),
                "final_soc": float(soc[-1]),
            },
        }


class PowerTradingAPI:
    """电力交易API"""

    def __init__(self, config_path: Optional[str] = None):
        self.app = Flask(__name__)
        self.executor = Executor(self.app)

        # 加载配置
        self.db_config = DatabaseConfig.from_env()
        self.optimization_config = OptimizationConfig()

        # 初始化服务
        self.trading_service = TradingStrategyService(
            self.db_config, self.optimization_config
        )

        # 注册路由
        self._register_routes()

    def _register_routes(self):
        """注册API路由"""

        @self.app.route('/api  /  v1  /  strategy  /  generate', methods=['POST'])
        def generate_strategy():
            """生成交易策略API"""
            try:
                data = request.get_json()

                # 验证输入
                if not data or 'date' not in data:
                    return jsonify({"error": "Missing required field: date"}), 400

                # 解析日期
                target_date = datetime.datetime.strptime(
                    data['date'], '%Y-%m-%d'
                ).date()

                # 获取节点配置
                nodes = self._get_node_configs(data.get('nodes', []))

                # 生成策略
                result = self.trading_service.generate_daily_strategy(
                    target_date, nodes
                )

                return jsonify({"success": True, "data": result})

            except Exception as e:
                logger.error(f"API error: {e}")
                return jsonify({"success": False, "error": str(e)}), 500

        @self.app.route('/api  /  v1  /  health', methods=['GET'])
        def health_check():
            """健康检查API"""
            return jsonify(
                {
                    "status": "healthy",
                    "timestamp": datetime.datetime.now().isoformat(),
                    "version": "2.1.0",
                }
            )

    def _get_node_configs(self, node_data: List[Dict]) -> List[NodeConfig]:
        """获取节点配置"""
        # 默认节点配置
        default_nodes = [
            NodeConfig(
                node_id="node_001",
                system_id="storage_system_001",
                node_name="储能系统001",
                capacity_kwh=430.0,
                max_power_kw=200.0,
            ),
            NodeConfig(
                node_id="node_002",
                system_id="storage_system_002",
                node_name="储能系统002",
                capacity_kwh=430.0,
                max_power_kw=200.0,
            ),
        ]

        if not node_data:
            return default_nodes

        # 从请求数据创建节点配置
        return [
            NodeConfig(
                node_id=node.get('node_id', ''),
                system_id=node.get('system_id', ''),
                node_name=node.get('node_name', ''),
                capacity_kwh=float(node.get('capacity_kwh', 430.0)),
                max_power_kw=float(node.get('max_power_kw', 200.0)),
            )
            for node in node_data
        ]

    def run(self, host: str = '0.0.0.0', port: int = 8000, debug: bool = False):
        """运行应用"""
        logger.info(f"Starting Power Trading API on {host}:{port}")
        self.app.run(host=host, port=port, debug=debug)


def main():
    """主函数"""
    import os

    # 设置日志级别
    os.getenv('LOG_LEVEL', 'INFO')

    # 创建API实例
    api = PowerTradingAPI()

    # 运行服务
    api.run(
        host=os.getenv('API_HOST', '0.0.0.0'),
        port=int(os.getenv('API_PORT', '8000')),
        debug=os.getenv('DEBUG', 'false').lower() == 'true',
    )


if __name__ == "__main__":
    main()
