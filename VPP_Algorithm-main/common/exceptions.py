"""
VPP算法异常定义模块
定义了完整的异常层次结构
"""

from typing import Any


class VppAlgorithmException(Exception):
    """VPP算法基础异常类"""

    def __init__(self, message: str, error_code: str = "VPP_ERROR"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)


class DatabaseConnectionException(VppAlgorithmException):
    """数据库连接异常"""

    def __init__(self, database_url: str, cause: Exception):
        self.database_url = database_url
        self.cause = cause
        message = f"Failed to connect to database: {database_url}"
        super().__init__(message, "DB_CONNECTION_ERROR")


class OptimizationException(VppAlgorithmException):
    """优化算法异常"""

    def __init__(self, node_id: str, algorithm_name: str, cause: Exception):
        self.node_id = node_id
        self.algorithm_name = algorithm_name
        self.cause = cause
        message = f"Optimization failed for node {node_id} using {algorithm_name}: {str(cause)}"
        super().__init__(message, "OPTIMIZATION_ERROR")


class DataValidationException(VppAlgorithmException):
    """数据验证异常"""

    def __init__(self, field_name: str, field_value: Any, expected: str):
        self.field_name = field_name
        self.field_value = field_value
        self.expected = expected
        message = (
            f"Validation failed for {field_name} = {field_value}, expected: {expected}"
        )
        super().__init__(message, "VALIDATION_ERROR")


class ConfigurationException(VppAlgorithmException):
    """配置异常"""

    def __init__(self, config_key: str, message: str):
        self.config_key = config_key
        full_message = f"Configuration error for {config_key}: {message}"
        super().__init__(full_message, "CONFIG_ERROR")


class AuthenticationException(VppAlgorithmException):
    """认证异常"""

    def __init__(self, message: str = "Authentication failed"):
        super().__init__(message, "AUTH_ERROR")


class RateLimitException(VppAlgorithmException):
    """速率限制异常"""

    def __init__(self, limit: int, window: int):
        message = f"Rate limit exceeded: {limit} requests per {window} seconds"
        super().__init__(message, "RATE_LIMIT_ERROR")


class WeatherDataException(VppAlgorithmException):
    """天气数据异常"""

    def __init__(self, location: str, cause: Exception):
        self.location = location
        self.cause = cause
        message = f"Failed to fetch weather data for {location}: {str(cause)}"
        super().__init__(message, "WEATHER_DATA_ERROR")


class ForecastException(VppAlgorithmException):
    """预测异常"""

    def __init__(self, forecast_type: str, time_range: str, cause: Exception):
        self.forecast_type = forecast_type
        self.time_range = time_range
        self.cause = cause
        message = f"Forecast failed for {forecast_type} in {time_range}: {str(cause)}"
        super().__init__(message, "FORECAST_ERROR")
