"""
VPP日志工具模块
提供统一的日志配置和管理功能

@author: VPP Team
@version: 2.0.0
@date: 2024  - 01  - 01
"""

import logging
import logging.handlers
import os
import sys
from datetime import datetime
from pathlib import Path
from typing import Any, Dict, Optional

import yaml


class VppLoggerConfig:
    """VPP日志配置类"""

    def __init__(self, config_path: Optional[str] = None):
        self.config = self._load_config(config_path)
        self._ensure_log_directory()

    def _load_config(self, config_path: Optional[str]) -> Dict[str, Any]:
        """加载日志配置"""
        if config_path and os.path.exists(config_path):
            with open(config_path, 'r', encoding='utf  -  8') as f:
                config = yaml.safe_load(f)
                return config.get('logging', {})

        # 默认配置
        return {
            'level': 'INFO',
            'format': '%(asctime)s  -  %(name)s  -  %(levelname)s  -  [%(filename)s:%(lineno)d]  -  %(message)s',
            'file': {
                'enabled': True,
                'path': 'logs  /  vpp_algorithm.log',
                'max_size': '100MB',
                'backup_count': 7,
                'rotation': 'daily',
            },
            'console': {'enabled': True, 'colored': True},
        }

    def _ensure_log_directory(self):
        """确保日志目录存在"""
        if self.config.get('file', {}).get('enabled'):
            log_path = self.config['file']['path']
            log_dir = os.path.dirname(log_path)
            if log_dir:
                Path(log_dir).mkdir(parents=True, exist_ok=True)


class ColoredFormatter(logging.Formatter):
    """彩色日志格式化器"""

    # ANSI颜色代码
    COLORS = {
        'DEBUG': '\033[36m',  # 青色
        'INFO': '\033[32m',  # 绿色
        'WARNING': '\033[33m',  # 黄色
        'ERROR': '\033[31m',  # 红色
        'CRITICAL': '\033[35m',  # 紫色
        'RESET': '\033[0m',  # 重置
    }

    def format(self, record: logging.LogRecord) -> str:
        # 为了支持彩色输出，我们直接在这里处理颜色
        if sys.stdout.isatty():  # 只在终端中使用颜色
            color = self.COLORS.get(record.levelname, '')
            reset = self.COLORS['RESET']
            original_levelname = record.levelname
            original_name = record.name

            record.levelname = f"{color}{record.levelname}{reset}"
            record.name = f"{color}{record.name}{reset}"

            formatted = super().format(record)

            # 恢复原始值
            record.levelname = original_levelname
            record.name = original_name

            return formatted

        return super().format(record)


class VppLogger:
    """VPP日志管理器"""

    _instances: Dict[str, logging.Logger] = {}
    _config: Optional[VppLoggerConfig] = None

    @classmethod
    def setup_config(cls, config_path: Optional[str] = None):
        """设置全局日志配置"""
        cls._config = VppLoggerConfig(config_path)

    @classmethod
    def get_logger(cls, name: str) -> logging.Logger:
        """获取日志器实例"""
        if name in cls._instances:
            return cls._instances[name]

        if cls._config is None:
            cls.setup_config()

        # 确保_config不为None
        assert cls._config is not None

        logger = logging.getLogger(name)
        logger.setLevel(getattr(logging, cls._config.config['level'].upper()))

        # 避免重复添加处理器
        if logger.handlers:
            logger.handlers.clear()

        # 添加控制台处理器
        if cls._config.config.get('console', {}).get('enabled', True):
            console_handler = cls._create_console_handler()
            logger.addHandler(console_handler)

        # 添加文件处理器
        if cls._config.config.get('file', {}).get('enabled', False):
            file_handler = cls._create_file_handler()
            logger.addHandler(file_handler)

        # 防止日志传播到根日志器
        logger.propagate = False

        cls._instances[name] = logger
        return logger

    @classmethod
    def _create_console_handler(cls) -> logging.StreamHandler:
        """创建控制台处理器"""
        assert cls._config is not None

        handler = logging.StreamHandler(sys.stdout)

        # 设置格式
        format_str = cls._config.config.get('format')
        if cls._config.config.get('console', {}).get('colored', True):
            formatter = ColoredFormatter(format_str)
        else:
            formatter = logging.Formatter(format_str)

        handler.setFormatter(formatter)
        handler.setLevel(getattr(logging, cls._config.config['level'].upper()))

        return handler

    @classmethod
    def _create_file_handler(cls) -> logging.Handler:
        """创建文件处理器"""
        assert cls._config is not None

        file_config = cls._config.config['file']
        log_path = file_config['path']

        # 根据轮转策略选择处理器
        rotation = file_config.get('rotation', 'size')

        if rotation == 'daily':
            handler = logging.handlers.TimedRotatingFileHandler(
                filename=log_path,
                when='midnight',
                interval=1,
                backupCount=file_config.get('backup_count', 7),
                encoding='utf  -  8',
            )
            handler.suffix = "%Y-%m-%d.log"
        elif rotation == 'size':
            max_size = cls._parse_size(file_config.get('max_size', '100MB'))
            handler = logging.handlers.RotatingFileHandler(
                filename=log_path,
                maxBytes=max_size,
                backupCount=file_config.get('backup_count', 7),
                encoding='utf  -  8',
            )
        else:
            handler = logging.FileHandler(log_path, encoding='utf  -  8')

        # 设置格式
        format_str = cls._config.config.get('format')
        formatter = logging.Formatter(format_str)
        handler.setFormatter(formatter)
        handler.setLevel(getattr(logging, cls._config.config['level'].upper()))

        return handler

    @staticmethod
    def _parse_size(size_str: str) -> int:
        """解析大小字符串"""
        size_str = size_str.upper()
        if size_str.endswith('KB'):
            return int(size_str[:-2]) * 1024
        elif size_str.endswith('MB'):
            return int(size_str[:-2]) * 1024 * 1024
        elif size_str.endswith('GB'):
            return int(size_str[:-2]) * 1024 * 1024 * 1024
        else:
            return int(size_str)


# 便捷函数
def setup_logger(name: str, config_path: Optional[str] = None) -> logging.Logger:
    """设置并获取日志器"""
    if config_path:
        VppLogger.setup_config(config_path)
    return VppLogger.get_logger(name)


def get_logger(name: str) -> logging.Logger:
    """获取日志器（兼容旧版本）"""
    return VppLogger.get_logger(name)


# 日志装饰器
def log_execution_time(logger: Optional[logging.Logger] = None):
    """记录函数执行时间的装饰器"""

    def decorator(func):
        def wrapper(*args, **kwargs):
            _logger = logger or get_logger(func.__module__)
            start_time = datetime.now()

            try:
                _logger.debug(f"开始执行 {func.__name__}")
                result = func(*args, **kwargs)
                execution_time = (datetime.now() - start_time).total_seconds()
                _logger.info(f"{func.__name__} 执行完成，耗时: {execution_time:.3f}秒")
                return result
            except Exception as e:
                execution_time = (datetime.now() - start_time).total_seconds()
                _logger.error(
                    f"{func.__name__} 执行失败，耗时: {execution_time:.3f}秒，错误: {str(e)}"
                )
                raise

        return wrapper

    return decorator


def log_exception(logger: Optional[logging.Logger] = None):
    """记录异常的装饰器"""

    def decorator(func):
        def wrapper(*args, **kwargs):
            _logger = logger or get_logger(func.__module__)

            try:
                return func(*args, **kwargs)
            except Exception as e:
                _logger.exception(f"{func.__name__} 发生异常: {str(e)}")
                raise

        return wrapper

    return decorator


# 初始化默认配置
if __name__ != "__main__":
    VppLogger.setup_config()


def get_logger(name: str, level: str = "INFO") -> logging.Logger:
    """
    获取配置好的日志记录器

    Args:
        name: 日志记录器名称
        level: 日志级别

    Returns:
        logging.Logger: 配置好的日志记录器
    """
    logger = logging.getLogger(name)

    if not logger.handlers:
        # 创建处理器
        handler = logging.StreamHandler()

        # 创建格式器
        formatter = logging.Formatter(
            '%(asctime)s  -  %(name)s  -  %(levelname)s  -  %(message)s'
        )
        handler.setFormatter(formatter)

        # 添加处理器到日志记录器
        logger.addHandler(handler)
        logger.setLevel(getattr(logging, level.upper()))

    return logger


def setup_file_logger(name: str, log_file: str, level: str = "INFO") -> logging.Logger:
    """
    设置文件日志记录器

    Args:
        name: 日志记录器名称
        log_file: 日志文件路径
        level: 日志级别

    Returns:
        logging.Logger: 配置好的文件日志记录器
    """
    logger = logging.getLogger(name)

    # 确保日志目录存在
    os.makedirs(os.path.dirname(log_file), exist_ok=True)

    # 创建文件处理器
    file_handler = logging.FileHandler(log_file)

    # 创建格式器
    formatter = logging.Formatter(
        '%(asctime)s  -  %(name)s  -  %(levelname)s  -  %(funcName)s:%(lineno)d  -  %(message)s'
    )
    file_handler.setFormatter(formatter)

    # 添加处理器
    logger.addHandler(file_handler)
    logger.setLevel(getattr(logging, level.upper()))

    return logger
