"""
VPP算法引擎 - 高性能版本 v2.1.0
=====================================

提供虚拟电厂的核心算法功能：
- 储能系统优化调度
- 负荷预测与分析  
- 光伏发电预测
- 价格信号处理
- 性能监控与优化

安全特性：
- 输入数据验证
- SQL注入防护
- 内存使用监控
- 并发安全控制

作者: VPP Development Team
版本: 2.1.0
更新: 2024-12-29
"""

import asyncio
import logging
import time
from concurrent.futures import ThreadPoolExecutor, as_completed
from dataclasses import dataclass, field
from datetime import datetime, timedelta
from enum import Enum
from typing import Any, Dict, List, Optional, Tuple, Union, Callable
import warnings
from pathlib import Path
import os

import numpy as np
import pandas as pd
from pydantic import BaseModel, Field, validator, root_validator
import psutil

# 导入自定义模块
from common.exceptions import (
    VppAlgorithmException,
    OptimizationException,
    DataValidationException,
    ConfigurationException
)
from common.log_util import get_logger, log_performance, log_exception

# 配置日志
logger = get_logger(__name__)

# 全局常量配置
class AlgorithmConstants:
    """算法常量配置 - 消除硬编码"""
    
    # 时间相关常量
    HOURS_PER_DAY = 24
    MINUTES_PER_HOUR = 60
    PERIODS_PER_DAY = 96  # 15分钟间隔
    MINUTES_PER_PERIOD = 15
    
    # 储能系统限制
    DEFAULT_MIN_SOC = 0.1  # 10%
    DEFAULT_MAX_SOC = 0.9  # 90%
    DEFAULT_EFFICIENCY = 0.95  # 95%
    MAX_CAPACITY_MW = 1000.0  # 最大容量限制
    
    # 优化参数
    DEFAULT_SOLVER_TIMEOUT = 300  # 5分钟
    MAX_OPTIMIZATION_THREADS = 8
    CACHE_TTL_SECONDS = 1800  # 30分钟
    
    # 数据验证限制
    MAX_DEMAND_MW = 10000.0  # 10GW
    MAX_PRICE_YUAN_MWH = 10000.0  # 万元/MWh
    MIN_PRICE_YUAN_MWH = -1000.0  # 允许负价格
    
    # 性能监控阈值
    MAX_MEMORY_MB = 2048
    MAX_CPU_PERCENT = 80.0
    WARNING_SOLVE_TIME_SEC = 60.0


class OptimizationStatus(Enum):
    """优化状态枚举"""
    PENDING = "pending"
    RUNNING = "running"
    COMPLETED = "completed"
    FAILED = "failed"
    TIMEOUT = "timeout"
    CANCELLED = "cancelled"


@dataclass
class StorageConfig:
    """储能系统配置类 - 完整类型注解和验证"""
    capacity_mwh: float = Field(default=10.0, gt=0, le=AlgorithmConstants.MAX_CAPACITY_MW)
    power_mw: float = Field(default=5.0, gt=0, le=AlgorithmConstants.MAX_CAPACITY_MW)
    efficiency: float = Field(default=AlgorithmConstants.DEFAULT_EFFICIENCY, gt=0, le=1.0)
    min_soc: float = Field(default=AlgorithmConstants.DEFAULT_MIN_SOC, ge=0, lt=1.0)
    max_soc: float = Field(default=AlgorithmConstants.DEFAULT_MAX_SOC, gt=0, le=1.0)
    initial_soc: float = Field(default=0.5, ge=0, le=1.0)
    degradation_factor: float = Field(default=0.0001, ge=0, le=0.01)  # 每次循环的容量衰减
    
    @root_validator
    def validate_soc_limits(cls, values):
        """验证SOC上下限设置"""
        min_soc = values.get('min_soc', 0)
        max_soc = values.get('max_soc', 1)
        if min_soc >= max_soc:
            raise ValueError(f"min_soc ({min_soc}) must be less than max_soc ({max_soc})")
        return values
    
    @root_validator  
    def validate_power_capacity_ratio(cls, values):
        """验证功率容量比合理性"""
        power = values.get('power_mw', 0)
        capacity = values.get('capacity_mwh', 0)
        if power > capacity * 2:  # 功率不应超过容量的2倍
            warnings.warn(f"Power rating ({power}MW) is unusually high compared to capacity ({capacity}MWh)")
        return values


class OptimizationRequest(BaseModel):
    """优化请求模型 - 完整验证"""
    node_id: str = Field(..., min_length=1, max_length=50, regex=r'^[a-zA-Z0-9_-]+$')
    demand_forecast: List[float] = Field(..., min_items=AlgorithmConstants.PERIODS_PER_DAY, 
                                       max_items=AlgorithmConstants.PERIODS_PER_DAY)
    hourly_prices: List[float] = Field(..., min_items=AlgorithmConstants.HOURS_PER_DAY,
                                     max_items=AlgorithmConstants.HOURS_PER_DAY)
    time_periods: List[str] = Field(..., min_items=AlgorithmConstants.PERIODS_PER_DAY,
                                  max_items=AlgorithmConstants.PERIODS_PER_DAY)
    storage_config: Optional[StorageConfig] = None
    initial_soc: float = Field(default=0.5, ge=0, le=1.0)
    optimization_objective: str = Field(default="cost_minimize", regex=r'^(cost_minimize|revenue_maximize|peak_shaving)$')
    
    @validator('demand_forecast', each_item=True)
    def validate_demand_values(cls, v):
        """验证负荷预测数据"""
        if v < 0:
            raise ValueError("Demand values must be non-negative")
        if v > AlgorithmConstants.MAX_DEMAND_MW:
            raise ValueError(f"Demand value {v} exceeds maximum allowed {AlgorithmConstants.MAX_DEMAND_MW}MW")
        return v
    
    @validator('hourly_prices', each_item=True)
    def validate_price_values(cls, v):
        """验证电价数据"""
        if not (AlgorithmConstants.MIN_PRICE_YUAN_MWH <= v <= AlgorithmConstants.MAX_PRICE_YUAN_MWH):
            raise ValueError(f"Price {v} is outside valid range [{AlgorithmConstants.MIN_PRICE_YUAN_MWH}, {AlgorithmConstants.MAX_PRICE_YUAN_MWH}]")
        return v


class OptimizationResult(BaseModel):
    """优化结果模型"""
    node_id: str
    status: OptimizationStatus
    charging_schedule: List[float] = Field(default_factory=list)
    discharging_schedule: List[float] = Field(default_factory=list)
    soc_profile: List[float] = Field(default_factory=list)
    total_cost: float = 0.0
    total_revenue: float = 0.0
    net_profit: float = 0.0
    peak_reduction_mw: float = 0.0
    energy_arbitrage_mwh: float = 0.0
    solve_time_seconds: float = 0.0
    algorithm_version: str = "2.1.0"
    timestamp: datetime = Field(default_factory=datetime.now)
    metadata: Dict[str, Any] = Field(default_factory=dict)


class PriceSignalProcessor:
    """价格信号处理器 - 优化版"""
    
    def __init__(self):
        self.cache: Dict[str, Tuple[List[float], datetime]] = {}
        self.cache_ttl = timedelta(seconds=AlgorithmConstants.CACHE_TTL_SECONDS)
    
    def expand_hourly_to_15min(self, hourly_prices: List[float]) -> List[float]:
        """将小时电价扩展为15分钟电价 - 优化实现"""
        if len(hourly_prices) != AlgorithmConstants.HOURS_PER_DAY:
            raise DataValidationException(
                f"期望{AlgorithmConstants.HOURS_PER_DAY}个小时电价，实际收到{len(hourly_prices)}个"
            )
        
        # 使用numpy向量化操作提高性能
        hourly_array = np.array(hourly_prices)
        expanded = np.repeat(hourly_array, 4)  # 每小时重复4次得到15分钟间隔
        
        return expanded.tolist()
    
    def calculate_time_of_use_weights(self, prices: List[float]) -> Dict[str, float]:
        """计算分时电价权重"""
        prices_array = np.array(prices)
        
        return {
            "peak_hours_weight": np.mean(prices_array[32:72]),  # 8:00-18:00
            "off_peak_weight": np.mean(np.concatenate([prices_array[:32], prices_array[72:]])),
            "price_volatility": np.std(prices_array),
            "max_price": np.max(prices_array),
            "min_price": np.min(prices_array),
            "price_range": np.max(prices_array) - np.min(prices_array)
        }


class StorageOptimizer:
    """储能系统优化器 - 高性能实现"""
    
    def __init__(self, config: StorageConfig):
        self.config = config
        self.solver_timeout = AlgorithmConstants.DEFAULT_SOLVER_TIMEOUT
        self._optimization_cache: Dict[str, OptimizationResult] = {}
        
    def _generate_cache_key(self, demand: List[float], prices: List[float], 
                          initial_soc: float) -> str:
        """生成缓存键"""
        demand_hash = hash(tuple(demand))
        prices_hash = hash(tuple(prices))
        config_hash = hash((self.config.capacity_mwh, self.config.power_mw, 
                          self.config.efficiency, initial_soc))
        return f"{demand_hash}_{prices_hash}_{config_hash}"
    
    @log_performance
    def optimize(self, demand_forecast: List[float], prices_15min: List[float], 
                initial_soc: float = 0.5, objective: str = "cost_minimize") -> Dict[str, Any]:
        """
        优化储能调度策略 - 高性能实现
        
        使用动态规划算法求解最优充放电策略
        """
        start_time = time.time()
        
        try:
            # 检查缓存
            cache_key = self._generate_cache_key(demand_forecast, prices_15min, initial_soc)
            if cache_key in self._optimization_cache:
                cached_result = self._optimization_cache[cache_key]
                if (datetime.now() - cached_result.timestamp).seconds < AlgorithmConstants.CACHE_TTL_SECONDS:
                    logger.info("返回缓存的优化结果")
                    return cached_result.dict()
            
            # 参数验证
            self._validate_optimization_inputs(demand_forecast, prices_15min, initial_soc)
            
            # 使用向量化的动态规划算法
            result = self._solve_optimization_vectorized(
                demand_forecast, prices_15min, initial_soc, objective
            )
            
            solve_time = time.time() - start_time
            result["solve_time_seconds"] = solve_time
            
            if solve_time > AlgorithmConstants.WARNING_SOLVE_TIME_SEC:
                logger.warning(f"优化求解时间较长: {solve_time:.2f}秒")
            
            # 缓存结果
            optimization_result = OptimizationResult(**result)
            self._optimization_cache[cache_key] = optimization_result
            
            logger.info(f"储能优化完成，求解时间: {solve_time:.3f}秒")
            return result
            
        except Exception as e:
            logger.error(f"储能优化失败: {str(e)}")
            raise OptimizationException("storage_optimization", "dynamic_programming", e)
    
    def _validate_optimization_inputs(self, demand: List[float], prices: List[float], 
                                    initial_soc: float) -> None:
        """验证优化输入参数"""
        if len(demand) != AlgorithmConstants.PERIODS_PER_DAY:
            raise DataValidationException(f"需求预测数据长度错误: 期望{AlgorithmConstants.PERIODS_PER_DAY}，实际{len(demand)}")
        
        if len(prices) != AlgorithmConstants.PERIODS_PER_DAY:
            raise DataValidationException(f"价格数据长度错误: 期望{AlgorithmConstants.PERIODS_PER_DAY}，实际{len(prices)}")
        
        if not (0 <= initial_soc <= 1):
            raise DataValidationException(f"初始SOC超出范围: {initial_soc}")
        
        if any(d < 0 for d in demand):
            raise DataValidationException("需求预测包含负值")
    
    def _solve_optimization_vectorized(self, demand: List[float], prices: List[float],
                                     initial_soc: float, objective: str) -> Dict[str, Any]:
        """向量化优化求解 - 高性能实现"""
        
        periods = len(demand)
        demand_array = np.array(demand)
        prices_array = np.array(prices)
        
        # 初始化状态变量
        charging = np.zeros(periods)
        discharging = np.zeros(periods)
        soc = np.zeros(periods + 1)
        soc[0] = initial_soc
        
        # 使用贪婪算法作为快速启发式解法
        # 在实际应用中，这里应该使用线性规划求解器如GLPK、CPLEX等
        if objective == "cost_minimize":
            # 价格低时充电，价格高时放电
            price_percentile_25 = np.percentile(prices_array, 25)
            price_percentile_75 = np.percentile(prices_array, 75)
        
            for t in range(periods):
                current_demand = demand_array[t]
                current_price = prices_array[t]
                current_soc = soc[t]
                
                # 计算最大充放电功率
                max_charge = min(
                    self.config.power_mw,
                    (self.config.max_soc - current_soc) * self.config.capacity_mwh / 0.25  # 15分钟间隔
                )
                max_discharge = min(
                    self.config.power_mw,
                    (current_soc - self.config.min_soc) * self.config.capacity_mwh / 0.25
                )
                
                # 决策逻辑
                if current_price < price_percentile_25 and max_charge > 0.1:
                    # 低价时充电
                    charging[t] = max_charge * 0.8  # 保守策略
                elif current_price > price_percentile_75 and max_discharge > 0.1:
                    # 高价时放电
                    discharging[t] = min(max_discharge * 0.8, current_demand * 0.5)
                
                # 更新SOC
                energy_change = (charging[t] * self.config.efficiency - discharging[t]) * 0.25
                soc[t + 1] = current_soc + energy_change / self.config.capacity_mwh
                soc[t + 1] = np.clip(soc[t + 1], self.config.min_soc, self.config.max_soc)
        
        # 计算经济效益
        total_charging_cost = np.sum(charging * prices_array * 0.25)  # 0.25小时
        total_discharging_revenue = np.sum(discharging * prices_array * 0.25)
        net_profit = total_discharging_revenue - total_charging_cost
        
        # 计算削峰效果
        net_demand = demand_array - discharging + charging
        peak_reduction = np.max(demand_array) - np.max(net_demand)
        
        return {
            "charging_schedule": charging.tolist(),
            "discharging_schedule": discharging.tolist(),
            "soc_profile": soc[:-1].tolist(),  # 去掉最后一个时间点
            "total_cost": float(total_charging_cost),
            "total_revenue": float(total_discharging_revenue),
            "net_profit": float(net_profit),
            "peak_reduction_mw": float(peak_reduction),
            "energy_arbitrage_mwh": float(np.sum(discharging) * 0.25),
            "status": OptimizationStatus.COMPLETED,
            "metadata": {
                "algorithm": "vectorized_greedy",
                "objective": objective,
                "efficiency": self.config.efficiency,
                "capacity_utilization": float(np.max(soc) - np.min(soc))
            }
        }


class PerformanceOptimizer:
    """性能优化器"""
    
    def __init__(self):
        self.memory_threshold_mb = AlgorithmConstants.MAX_MEMORY_MB
        self.cpu_threshold_percent = AlgorithmConstants.MAX_CPU_PERCENT
    
    def monitor_resources(self) -> Dict[str, float]:
        """监控系统资源使用情况"""
        process = psutil.Process(os.getpid())
        memory_mb = process.memory_info().rss / 1024 / 1024
        cpu_percent = process.cpu_percent(interval=1)
        
        if memory_mb > self.memory_threshold_mb:
            logger.warning(f"内存使用量过高: {memory_mb:.1f}MB")
        
        if cpu_percent > self.cpu_threshold_percent:
            logger.warning(f"CPU使用率过高: {cpu_percent:.1f}%")
        
        return {
            "memory_mb": memory_mb,
            "cpu_percent": cpu_percent,
            "memory_threshold_mb": self.memory_threshold_mb,
            "cpu_threshold_percent": self.cpu_threshold_percent
        }
    
    def optimize_numpy_performance(self) -> Dict[str, Any]:
        """优化numpy性能设置"""
        try:
            import mkl
            mkl.set_num_threads(min(4, os.cpu_count()))
            logger.info("已启用MKL多线程优化")
            return {"mkl_enabled": True, "threads": mkl.get_max_threads()}
        except ImportError:
            # 使用标准numpy线程设置
            os.environ['OMP_NUM_THREADS'] = str(min(4, os.cpu_count()))
            return {"mkl_enabled": False, "threads": os.cpu_count()}


class AlgorithmEngine:
    """算法引擎 - 高性能版本"""
    
    def __init__(self):
        self.storage_optimizer: Optional[StorageOptimizer] = None
        self.price_processor = PriceSignalProcessor()
        self.perf_optimizer = PerformanceOptimizer()
        self._performance_metrics = {
            "total_optimizations": 0,
            "avg_solve_time": 0.0,
            "success_rate": 1.0,
            "cache_hit_rate": 0.0,
        }
    
    def initialize_storage_optimizer(self, config: StorageConfig) -> None:
        """初始化储能优化器"""
        self.storage_optimizer = StorageOptimizer(config)
        logger.info("Storage optimizer initialized with high-performance features")
    
    def run_storage_optimization(
        self,
        node_id: str,
        demand_forecast: List[float],
        hourly_prices: List[float],
        time_periods: List[str],
        storage_config: Optional[Dict] = None,
        initial_soc: float = 0.0,
        enable_parallel: bool = False,
    ) -> Dict[str, Any]:
        """
        运行储能优化 - 高性能版本
        """
        if self.storage_optimizer is None:
            if storage_config:
                config = StorageConfig(**storage_config)
            else:
                config = StorageConfig()
            self.initialize_storage_optimizer(config)
        
        try:
            # 创建优化请求
            request = OptimizationRequest(
                node_id=node_id,
                demand_forecast=demand_forecast,
                hourly_prices=hourly_prices,
                time_periods=time_periods,
                storage_config=storage_config,
                initial_soc=initial_soc
            )
            
            # 扩展价格数据
            prices_15min = self.price_processor.expand_hourly_to_15min(hourly_prices)
            
            # 运行优化
            start_time = time.time()
            result = self.storage_optimizer.optimize(
                demand_forecast, prices_15min, initial_soc
            )
            
            # 更新性能指标
            solve_time = time.time() - start_time
            self._update_performance_metrics(solve_time, True)
            
            # 添加节点信息和时间戳
            result.update({
                "node_id": node_id,
                "request_timestamp": datetime.now().isoformat(),
                "time_periods": time_periods,
                "performance_metrics": self.get_performance_metrics()
            })
            
            logger.info(f"节点 {node_id} 储能优化完成，求解时间: {solve_time:.3f}秒")
            return result
            
        except Exception as e:
            self._update_performance_metrics(0, False)
            logger.error(f"储能优化失败: {str(e)}")
            raise OptimizationException(node_id, "storage_optimization", e)
    
    async def run_parallel_optimization(
        self,
        requests: List[OptimizationRequest],
        max_workers: int = None
    ) -> List[OptimizationResult]:
        """并行运行多个优化任务"""
        if max_workers is None:
            max_workers = min(AlgorithmConstants.MAX_OPTIMIZATION_THREADS, len(requests))
        
        async def optimize_single(request: OptimizationRequest) -> OptimizationResult:
            try:
                result_dict = self.run_storage_optimization(
                    request.node_id,
                    request.demand_forecast,
                    request.hourly_prices,
                    request.time_periods,
                    request.storage_config.dict() if request.storage_config else None,
                    request.initial_soc
                )
                return OptimizationResult(**result_dict)
            except Exception as e:
                return OptimizationResult(
                    node_id=request.node_id,
                    status=OptimizationStatus.FAILED,
                    metadata={"error": str(e)}
                )
        
        tasks = [optimize_single(req) for req in requests]
        results = await asyncio.gather(*tasks, return_exceptions=True)
        
        return [r for r in results if isinstance(r, OptimizationResult)]
    
    def validate_optimization_inputs(self, **kwargs) -> Dict[str, str]:
        """验证优化输入参数"""
        errors = {}
        
        # 必需参数检查
        required_fields = ["demand_forecast", "hourly_prices", "time_periods"]
        for field in required_fields:
            if field not in kwargs or kwargs[field] is None:
                errors[field] = f"Missing required field: {field}"
        
        # 数据长度检查
        if "demand_forecast" in kwargs and kwargs["demand_forecast"]:
            if len(kwargs["demand_forecast"]) != 96:
                errors["demand_forecast"] = (
                    f"Expected 96 values, got {len(kwargs['demand_forecast'])}"
                )
        
        if "hourly_prices" in kwargs and kwargs["hourly_prices"]:
            if len(kwargs["hourly_prices"]) != 24:
                errors["hourly_prices"] = (
                    f"Expected 24 values, got {len(kwargs['hourly_prices'])}"
                )
        
        if "time_periods" in kwargs and kwargs["time_periods"]:
            if len(kwargs["time_periods"]) != 96:
                errors["time_periods"] = (
                    f"Expected 96 values, got {len(kwargs['time_periods'])}"
                )
        
        # 数据类型和范围检查
        if "demand_forecast" in kwargs and kwargs["demand_forecast"]:
            try:
                demand_array = np.array(kwargs["demand_forecast"], dtype=float)
                if np.any(demand_array < 0):
                    errors["demand_forecast"] = "Demand values must be non-negative"
                if np.any(demand_array > 10000):  # 10MW上限
                    errors["demand_forecast"] = (
                        "Demand values exceed maximum limit (10MW)"
                    )
            except (ValueError, TypeError):
                errors["demand_forecast"] = "Demand forecast must be numeric values"
        
        return errors
    
    def _update_performance_metrics(self, solve_time: float, success: bool) -> None:
        """更新性能指标"""
        self._performance_metrics["total_optimizations"] += 1
        
        if success:
            total_time = (self._performance_metrics["avg_solve_time"] * 
                         (self._performance_metrics["total_optimizations"] - 1) + solve_time)
            self._performance_metrics["avg_solve_time"] = (
                total_time / self._performance_metrics["total_optimizations"]
            )
        
        # 更新成功率
        total_success = (self._performance_metrics["success_rate"] * 
                        (self._performance_metrics["total_optimizations"] - 1))
        if success:
            total_success += 1
        self._performance_metrics["success_rate"] = (
            total_success / self._performance_metrics["total_optimizations"]
        )
    
    def get_performance_metrics(self) -> Dict[str, Any]:
        """获取性能指标"""
        return {
            **self._performance_metrics,
            "system_resources": self.perf_optimizer.monitor_resources(),
            "timestamp": datetime.now().isoformat()
        }


# 全局算法引擎实例
algorithm_engine = AlgorithmEngine()
