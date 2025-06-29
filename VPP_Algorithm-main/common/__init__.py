"""
VPP公共模块
提供通用的工具类和异常定义
"""

from .exceptions import (
    AuthenticationException,
    ConfigurationException,
    DatabaseConnectionException,
    DataValidationException,
    ForecastException,
    OptimizationException,
    RateLimitException,
    VppAlgorithmException,
    WeatherDataException,
)
from .log_util import get_logger, setup_file_logger

__all__ = [
    'get_logger',
    'setup_file_logger',
    'VppAlgorithmException',
    'DatabaseConnectionException',
    'OptimizationException',
    'DataValidationException',
    'ConfigurationException',
    'AuthenticationException',
    'RateLimitException',
    'WeatherDataException',
    'ForecastException',
]
