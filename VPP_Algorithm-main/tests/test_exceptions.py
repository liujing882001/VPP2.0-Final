"""
VPP算法服务异常处理单元测试

@author: VPP Team
@version: 2.0.0
@date: 2024 - 01 - 01
"""

import json
import unittest
from unittest.mock import Mock

from common.exceptions import (
    ConfigurationException,
    DatabaseConnectionException,
    DataValidationException,
    ModelLoadException,
    OptimizationException,
    RateLimitException,
    ResourceNotFoundException,
    ServiceUnavailableException,
    TimeoutException,
    VppAlgorithmException,
    create_database_exception,
    create_model_exception,
    create_not_found_exception,
    create_optimization_exception,
    create_timeout_exception,
    create_validation_exception,
    handle_algorithm_exceptions,
)


class TestVppAlgorithmException(unittest.TestCase):
    """测试基础异常类"""

    def test_basic_exception(self):
        """测试基础异常创建"""
        exception = VppAlgorithmException("测试错误")

        self.assertEqual(exception.message, "测试错误")
        self.assertEqual(exception.code, "ALGORITHM_ERROR")
        self.assertEqual(exception.details, {})
        self.assertIsNone(exception.cause)

    def test_exception_with_details(self):
        """测试带详细信息的异常"""
        details = {"node_id": "test_node", "value": 123}
        exception = VppAlgorithmException(
            "详细错误", code="CUSTOM_ERROR", details=details
        )

        self.assertEqual(exception.code, "CUSTOM_ERROR")
        self.assertEqual(exception.details, details)

    def test_exception_with_cause(self):
        """测试带原因的异常"""
        cause = ValueError("原始错误")
        exception = VppAlgorithmException("包装错误", cause=cause)

        self.assertEqual(exception.cause, cause)
        self.assertIsNotNone(exception.traceback)

    def test_to_dict(self):
        """测试异常转字典"""
        details = {"field": "value"}
        cause = ValueError("原始错误")
        exception = VppAlgorithmException(
            "测试错误", code="TEST_ERROR", details=details, cause=cause
        )

        result = exception.to_dict()

        self.assertEqual(result["error_type"], "VppAlgorithmException")
        self.assertEqual(result["message"], "测试错误")
        self.assertEqual(result["code"], "TEST_ERROR")
        self.assertEqual(result["details"], details)
        self.assertEqual(result["cause"], "原始错误")
        self.assertIn("traceback", result)

    def test_str_representation(self):
        """测试字符串表示"""
        exception = VppAlgorithmException("测试错误", code="TEST_ERROR")
        self.assertEqual(str(exception), "[TEST_ERROR] 测试错误")


class TestSpecificExceptions(unittest.TestCase):
    """测试特定异常类"""

    def test_model_load_exception(self):
        """测试模型加载异常"""
        exception = ModelLoadException("test_model")

        self.assertEqual(exception.code, "MODEL_LOAD_ERROR")
        self.assertIn("test_model", exception.message)
        self.assertEqual(exception.details["model_name"], "test_model")

    def test_data_validation_exception(self):
        """测试数据验证异常"""
        exception = DataValidationException("age", 150, "0 - 120")

        self.assertEqual(exception.code, "DATA_VALIDATION_ERROR")
        self.assertIn("age", exception.message)
        self.assertIn("150", exception.message)
        self.assertIn("0 - 120", exception.message)

    def test_database_connection_exception(self):
        """测试数据库连接异常"""
        exception = DatabaseConnectionException("postgresql: /  / localhost:5432 / vpp")

        self.assertEqual(exception.code, "DATABASE_CONNECTION_ERROR")
        self.assertIn("postgresql: /  / localhost:5432 / vpp", exception.message)

    def test_optimization_exception(self):
        """测试优化异常"""
        exception = OptimizationException("node_001", "genetic_algorithm")

        self.assertEqual(exception.code, "OPTIMIZATION_ERROR")
        self.assertEqual(exception.details["node_id"], "node_001")
        self.assertEqual(exception.details["algorithm_name"], "genetic_algorithm")

    def test_configuration_exception(self):
        """测试配置异常"""
        exception = ConfigurationException("database.host", expected_type="string")

        self.assertEqual(exception.code, "CONFIGURATION_ERROR")
        self.assertEqual(exception.details["config_key"], "database.host")
        self.assertEqual(exception.details["expected_type"], "string")

    def test_timeout_exception(self):
        """测试超时异常"""
        exception = TimeoutException("model_training", 300.5)

        self.assertEqual(exception.code, "TIMEOUT_ERROR")
        self.assertEqual(exception.details["operation"], "model_training")
        self.assertEqual(exception.details["timeout_seconds"], 300.5)

    def test_resource_not_found_exception(self):
        """测试资源未找到异常"""
        exception = ResourceNotFoundException("model", "lightgbm_v1.0")

        self.assertEqual(exception.code, "RESOURCE_NOT_FOUND")
        self.assertEqual(exception.details["resource_type"], "model")
        self.assertEqual(exception.details["resource_id"], "lightgbm_v1.0")

    def test_service_unavailable_exception(self):
        """测试服务不可用异常"""
        exception = ServiceUnavailableException("prediction_service")

        self.assertEqual(exception.code, "SERVICE_UNAVAILABLE")
        self.assertEqual(exception.details["service_name"], "prediction_service")

    def test_rate_limit_exception(self):
        """测试速率限制异常"""
        exception = RateLimitException(100, 60)

        self.assertEqual(exception.code, "RATE_LIMIT_EXCEEDED")
        self.assertEqual(exception.details["limit"], 100)
        self.assertEqual(exception.details["window_seconds"], 60)


class TestExceptionFactories(unittest.TestCase):
    """测试异常工厂函数"""

    def test_create_model_exception(self):
        """测试创建模型异常"""
        original_error = FileNotFoundError("模型文件不存在")
        exception = create_model_exception("test_model", original_error)

        self.assertIsInstance(exception, ModelLoadException)
        self.assertEqual(exception.cause, original_error)

    def test_create_validation_exception(self):
        """测试创建验证异常"""
        exception = create_validation_exception("price", -100, "price > 0")

        self.assertIsInstance(exception, DataValidationException)
        self.assertEqual(exception.details["field_name"], "price")

    def test_create_database_exception(self):
        """测试创建数据库异常"""
        original_error = ConnectionError("连接被拒绝")
        exception = create_database_exception(
            "postgresql: /  / localhost", original_error
        )

        self.assertIsInstance(exception, DatabaseConnectionException)
        self.assertEqual(exception.cause, original_error)

    def test_create_optimization_exception(self):
        """测试创建优化异常"""
        original_error = ValueError("参数无效")
        exception = create_optimization_exception("node_001", "pso", original_error)

        self.assertIsInstance(exception, OptimizationException)
        self.assertEqual(exception.cause, original_error)

    def test_create_timeout_exception(self):
        """测试创建超时异常"""
        exception = create_timeout_exception("data_processing", 120.0)

        self.assertIsInstance(exception, TimeoutException)
        self.assertEqual(exception.details["timeout_seconds"], 120.0)

    def test_create_not_found_exception(self):
        """测试创建未找到异常"""
        exception = create_not_found_exception("config", "algorithm.yaml")

        self.assertIsInstance(exception, ResourceNotFoundException)
        self.assertEqual(exception.details["resource_type"], "config")


class TestExceptionDecorator(unittest.TestCase):
    """测试异常处理装饰器"""

    def test_decorator_with_vpp_exception(self):
        """测试装饰器处理VPP异常"""

        @handle_algorithm_exceptions()
        def test_function():
            raise ModelLoadException("test_model")

        with self.assertRaises(ModelLoadException):
            test_function()

    def test_decorator_with_generic_exception(self):
        """测试装饰器处理通用异常"""

        @handle_algorithm_exceptions()
        def test_function():
            raise ValueError("通用错误")

        with self.assertRaises(VppAlgorithmException) as context:
            test_function()

        self.assertEqual(context.exception.code, "UNEXPECTED_ERROR")
        self.assertIsInstance(context.exception.cause, ValueError)

    def test_decorator_with_logger(self):
        """测试装饰器记录日志"""
        mock_logger = Mock()

        @handle_algorithm_exceptions(logger=mock_logger)
        def test_function():
            raise ValueError("测试错误")

        with self.assertRaises(VppAlgorithmException):
            test_function()

        mock_logger.exception.assert_called_once()

    def test_decorator_success_case(self):
        """测试装饰器正常执行"""

        @handle_algorithm_exceptions()
        def test_function():
            return "success"

        result = test_function()
        self.assertEqual(result, "success")


class TestExceptionSerialization(unittest.TestCase):
    """测试异常序列化"""

    def test_json_serialization(self):
        """测试JSON序列化"""
        exception = ModelLoadException("test_model")
        exception_dict = exception.to_dict()

        # 测试是否可以序列化为JSON
        json_str = json.dumps(exception_dict, ensure_ascii=False)
        self.assertIsInstance(json_str, str)

        # 测试反序列化
        deserialized = json.loads(json_str)
        self.assertEqual(deserialized["error_type"], "ModelLoadException")
        self.assertEqual(deserialized["code"], "MODEL_LOAD_ERROR")


if __name__ == '__main__':
    # 创建测试套件
    suite = unittest.TestSuite()

    # 添加测试用例
    suite.addTest(unittest.makeSuite(TestVppAlgorithmException))
    suite.addTest(unittest.makeSuite(TestSpecificExceptions))
    suite.addTest(unittest.makeSuite(TestExceptionFactories))
    suite.addTest(unittest.makeSuite(TestExceptionDecorator))
    suite.addTest(unittest.makeSuite(TestExceptionSerialization))

    # 运行测试
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)

    # 输出测试结果
    if result.wasSuccessful():
        print("\n✅ 所有测试通过!")
    else:
        print(
            f"\n❌ 测试失败: {len(result.failures)} 个失败, {len(result.errors)} 个错误"
        )
