"""
VPP算法综合测试套件
提供全面的测试覆盖，目标覆盖率85%+

@author: VPP Team
@version: 2.1.0
@date: 2024  - 01  - 01
"""

import os
import sys
import unittest
from unittest.mock import Mock, patch

import numpy as np

# 添加项目路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from common.exceptions import (
    DatabaseConnectionException,
    DataValidationException,
    OptimizationException,
    VppAlgorithmException,
)

# 导入被测试模块
from core.algorithm_engine import (
    AlgorithmEngine,
    OptimizationStatus,
    PriceSignalProcessor,
    StorageConfig,
    StorageOptimizer,
)
from core.database_manager import DatabaseConfig, QueryValidator


class TestStorageConfig(unittest.TestCase):
    """储能配置测试"""

    def test_valid_config_creation(self):
        """测试创建有效配置"""
        config = StorageConfig(
            capacity_kwh=430.0,
            max_charge_power=200.0,
            max_discharge_power=200.0,
            efficiency=0.95,
        )
        self.assertEqual(config.capacity_kwh, 430.0)
        self.assertEqual(config.efficiency, 0.95)

    def test_config_validation_success(self):
        """测试配置验证成功"""
        config = StorageConfig()
        try:
            config.validate()
        except Exception:
            self.fail("Valid config should not raise exception")

    def test_invalid_capacity_validation(self):
        """测试无效容量验证"""
        config = StorageConfig(capacity_kwh=-100.0)
        with self.assertRaises(DataValidationException):
            config.validate()

    def test_invalid_efficiency_validation(self):
        """测试无效效率验证"""
        config = StorageConfig(efficiency=1.5)
        with self.assertRaises(DataValidationException):
            config.validate()

    def test_invalid_soc_limits_validation(self):
        """测试无效SOC限制验证"""
        config = StorageConfig(capacity_kwh=430.0, max_soc=500.0)
        with self.assertRaises(DataValidationException):
            config.validate()


class TestPriceSignalProcessor(unittest.TestCase):
    """价格信号处理器测试"""

    def test_expand_hourly_to_15min_success(self):
        """测试小时电价扩展成功"""
        hourly_prices = np.array([0.5] * 24)
        result = PriceSignalProcessor.expand_hourly_to_15min(hourly_prices)
        self.assertEqual(len(result), 96)
        self.assertTrue(np.all(result == 0.5))

    def test_expand_hourly_to_15min_invalid_length(self):
        """测试无效长度的小时电价扩展"""
        hourly_prices = np.array([0.5] * 23)  # 错误长度
        with self.assertRaises(DataValidationException):
            PriceSignalProcessor.expand_hourly_to_15min(hourly_prices)

    def test_calculate_time_of_use_weights(self):
        """测试分时电价权重计算"""
        periods = ['valley', 'flat', 'peak', 'top']
        weights = PriceSignalProcessor.calculate_time_of_use_weights(periods)

        self.assertEqual(weights['valley'], 0.0001)
        self.assertEqual(weights['flat'], 0.0001)
        self.assertEqual(weights['peak'], -0.0015)
        self.assertEqual(weights['top'], -0.0015)


class TestStorageOptimizer(unittest.TestCase):
    """储能优化器测试"""

    def setUp(self):
        """测试设置"""
        self.config = StorageConfig()
        self.optimizer = StorageOptimizer(self.config)

        # 创建测试数据
        self.demand_forecast = np.random.uniform(800, 1200, 96)
        self.electricity_prices = np.random.uniform(0.3, 0.9, 96)
        self.time_periods = ['valley'] * 32 + ['peak'] * 32 + ['flat'] * 32

    def test_optimizer_initialization(self):
        """测试优化器初始化"""
        self.assertIsNotNone(self.optimizer)
        self.assertEqual(self.optimizer.config, self.config)

    def test_input_data_validation_success(self):
        """测试输入数据验证成功"""
        try:
            self.optimizer._validate_input_data(
                self.demand_forecast, self.electricity_prices, self.time_periods
            )
        except Exception:
            self.fail("Valid data should not raise exception")

    def test_input_data_validation_invalid_demand_length(self):
        """测试无效需求数据长度验证"""
        invalid_demand = np.array([100.0] * 95)  # 错误长度
        with self.assertRaises(DataValidationException):
            self.optimizer._validate_input_data(
                invalid_demand, self.electricity_prices, self.time_periods
            )

    def test_input_data_validation_invalid_prices_length(self):
        """测试无效价格数据长度验证"""
        invalid_prices = np.array([0.5] * 95)  # 错误长度
        with self.assertRaises(DataValidationException):
            self.optimizer._validate_input_data(
                self.demand_forecast, invalid_prices, self.time_periods
            )

    @patch('cvxpy.Problem.solve')
    def test_optimization_success(self, mock_solve):
        """测试优化成功"""
        # 模拟求解成功
        mock_solve.return_value = 1000.0
        mock_problem = Mock()
        mock_problem.status = 'optimal'

        with patch('cvxpy.Problem', return_value=mock_problem):
            with patch('cvxpy.Variable') as mock_var:
                mock_var.return_value.value = np.random.uniform(-50, 50, 96)

                result = self.optimizer.optimize_daily_schedule(
                    self.demand_forecast, self.electricity_prices, self.time_periods
                )

                self.assertEqual(result.status, OptimizationStatus.SUCCESS)
                self.assertIsNotNone(result.solve_time)


class TestAlgorithmEngine(unittest.TestCase):
    """算法引擎测试"""

    def setUp(self):
        """测试设置"""
        self.engine = AlgorithmEngine()

    def test_engine_initialization(self):
        """测试引擎初始化"""
        self.assertIsNotNone(self.engine)
        self.assertIsNone(self.engine.storage_optimizer)
        self.assertIsNotNone(self.engine.price_processor)

    def test_storage_optimizer_initialization(self):
        """测试储能优化器初始化"""
        config = StorageConfig()
        self.engine.initialize_storage_optimizer(config)

        self.assertIsNotNone(self.engine.storage_optimizer)
        self.assertEqual(self.engine.storage_optimizer.config, config)

    def test_input_validation_valid_data(self):
        """测试有效输入数据验证"""
        errors = self.engine.validate_optimization_inputs(
            node_id="test_node",
            demand_forecast=[100.0] * 96,
            hourly_prices=[0.5] * 24,
            time_periods=["flat"] * 96,
        )
        self.assertEqual(len(errors), 0)

    def test_input_validation_invalid_data(self):
        """测试无效输入数据验证"""
        errors = self.engine.validate_optimization_inputs(
            demand_forecast=[100.0] * 95,  # 错误长度
            hourly_prices=[0.5] * 23,  # 错误长度
            time_periods=["flat"] * 95,  # 错误长度
        )
        self.assertGreater(len(errors), 0)

    @patch.object(StorageOptimizer, 'optimize_daily_schedule')
    def test_storage_optimization_success(self, mock_optimize):
        """测试储能优化成功"""
        # 设置模拟返回值
        from core.algorithm_engine import OptimizationResult, OptimizationStatus

        mock_result = OptimizationResult(
            status=OptimizationStatus.SUCCESS, objective_value=1000.0, solve_time=0.5
        )
        mock_optimize.return_value = mock_result

        # 初始化优化器
        config = StorageConfig()
        self.engine.initialize_storage_optimizer(config)

        result = self.engine.run_storage_optimization(
            node_id="test_node",
            demand_forecast=[100.0] * 96,
            hourly_prices=[0.5] * 24,
            time_periods=["flat"] * 96,
        )

        self.assertEqual(result["status"], "success")
        self.assertEqual(result["objective_value"], 1000.0)


class TestQueryValidator(unittest.TestCase):
    """查询验证器测试"""

    def test_valid_select_query(self):
        """测试有效SELECT查询"""
        query = "SELECT  *  FROM table_name WHERE id  =  1"
        try:
            QueryValidator.validate_query(query, "SELECT")
        except Exception:
            self.fail("Valid SELECT query should not raise exception")

    def test_dangerous_keyword_detection(self):
        """测试危险关键词检测"""
        dangerous_queries = [
            "SELECT  *  FROM table; DROP TABLE users;",
            "SELECT  *  FROM table WHERE id  =  1 UNION SELECT password FROM users",
            "SELECT  *  FROM table; DELETE FROM users;",
        ]

        for query in dangerous_queries:
            with self.assertRaises(DataValidationException):
                QueryValidator.validate_query(query, "SELECT")

    def test_parameter_sanitization(self):
        """测试参数清理"""
        params = {"user_id": 123, "name": "test_user", "email": "test@example.com"}

        sanitized = QueryValidator.sanitize_parameters(params)
        self.assertEqual(sanitized["user_id"], 123)
        self.assertEqual(sanitized["name"], "test_user")
        self.assertEqual(sanitized["email"], "test@example.com")

    def test_invalid_parameter_name(self):
        """测试无效参数名"""
        invalid_params = {"user'; DROP TABLE users; --": "malicious"}

        with self.assertRaises(DataValidationException):
            QueryValidator.sanitize_parameters(invalid_params)


class TestDatabaseConfig(unittest.TestCase):
    """数据库配置测试"""

    def test_config_creation_from_env(self):
        """测试从环境变量创建配置"""
        with patch.dict(
            os.environ,
            {
                'DB_HOST': 'test_host',
                'DB_PORT': '5433',
                'DB_NAME': 'test_db',
                'DB_USER': 'test_user',
                'DB_PASSWORD': 'test_pass',
            },
        ):
            config = DatabaseConfig.from_environment()

            self.assertEqual(config.host, 'test_host')
            self.assertEqual(config.port, 5433)
            self.assertEqual(config.database, 'test_db')
            self.assertEqual(config.username, 'test_user')
            self.assertEqual(config.password, 'test_pass')

    def test_config_validation_success(self):
        """测试配置验证成功"""
        config = DatabaseConfig(
            host="localhost",
            port=5432,
            database="test_db",
            username="test_user",
            password="test_pass",
        )

        try:
            config.validate()
        except Exception:
            self.fail("Valid config should not raise exception")

    def test_config_validation_empty_password(self):
        """测试空密码验证"""
        config = DatabaseConfig(
            host="localhost",
            port=5432,
            database="test_db",
            username="test_user",
            password="",
        )

        with self.assertRaises(DataValidationException):
            config.validate()


class TestExceptionHandling(unittest.TestCase):
    """异常处理测试"""

    def test_vpp_algorithm_exception_creation(self):
        """测试VPP算法异常创建"""
        exception = VppAlgorithmException("Test message", "TEST_ERROR")

        self.assertEqual(exception.message, "Test message")
        self.assertEqual(exception.error_code, "TEST_ERROR")
        self.assertEqual(str(exception), "Test message")

    def test_optimization_exception_creation(self):
        """测试优化异常创建"""
        cause = Exception("Original error")
        exception = OptimizationException("node_1", "algorithm_1", cause)

        self.assertEqual(exception.node_id, "node_1")
        self.assertEqual(exception.algorithm_name, "algorithm_1")
        self.assertEqual(exception.cause, cause)
        self.assertIn("node_1", exception.message)
        self.assertIn("algorithm_1", exception.message)

    def test_data_validation_exception_creation(self):
        """测试数据验证异常创建"""
        exception = DataValidationException("test_field", "invalid_value", "> 0")

        self.assertEqual(exception.field_name, "test_field")
        self.assertEqual(exception.field_value, "invalid_value")
        self.assertEqual(exception.expected, "> 0")
        self.assertIn("test_field", exception.message)

    def test_database_connection_exception_creation(self):
        """测试数据库连接异常创建"""
        cause = Exception("Connection refused")
        exception = DatabaseConnectionException("localhost:5432  /  test", cause)

        self.assertEqual(exception.database_url, "localhost:5432  /  test")
        self.assertEqual(exception.cause, cause)
        self.assertIn("localhost:5432  /  test", exception.message)


def run_comprehensive_tests():
    """运行综合测试"""
    # 创建测试套件
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()

    # 添加所有测试类
    test_classes = [
        TestStorageConfig,
        TestPriceSignalProcessor,
        TestStorageOptimizer,
        TestAlgorithmEngine,
        TestQueryValidator,
        TestDatabaseConfig,
        TestExceptionHandling,
    ]

    for test_class in test_classes:
        tests = loader.loadTestsFromTestCase(test_class)
        suite.addTests(tests)

    # 运行测试
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)

    # 打印结果统计
    total_tests = result.testsRun
    failures = len(result.failures)
    errors = len(result.errors)
    success_rate = (
        ((total_tests - failures - errors) / total_tests * 100)
        if total_tests > 0
        else 0
    )

    # 使用日志记录测试报告
    logger.info("="*60)
    logger.info("VPP算法综合测试报告")
    logger.info("="*60)
    logger.info(f"失败: {failures}, 错误: {errors}, 成功率: {success_rate:.1f}%")

    if failures > 0:
        logger.error("失败详情:")
        for test, traceback in result.failures:
            logger.error(f"  {test}: {traceback}")

    if errors > 0:
        logger.error("错误详情:")
        for test, traceback in result.errors:
            logger.error(f"  {test}: {traceback}")

    return result


if __name__ == '__main__':
    run_comprehensive_tests()
