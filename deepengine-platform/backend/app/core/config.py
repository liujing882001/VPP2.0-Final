"""
应用配置管理
"""

from pydantic_settings import BaseSettings
from typing import List, Optional
import os
from functools import lru_cache


class Settings(BaseSettings):
    """应用设置"""
    
    # 基本配置
    APP_NAME: str = "DeepEngine分布式能源管理平台"
    VERSION: str = "2.0.0"
    DEBUG: bool = True
    ENVIRONMENT: str = "development"
    
    # API配置
    API_V1_STR: str = "/api/v1"
    
    # 安全配置
    SECRET_KEY: str = "deepengine-secret-key-change-in-production"
    ACCESS_TOKEN_EXPIRE_MINUTES: int = 30
    ALGORITHM: str = "HS256"
    
    # 跨域配置
    ALLOWED_HOSTS: List[str] = ["*"]
    
    # 数据库配置
    DATABASE_URL: str = "postgresql://deepengine:password@localhost:5432/deepengine_db"
    
    # Redis配置
    REDIS_URL: str = "redis://localhost:6379/0"
    
    # InfluxDB配置
    INFLUXDB_URL: str = "http://localhost:8086"
    INFLUXDB_TOKEN: str = "deepengine-token"
    INFLUXDB_ORG: str = "deepengine"
    INFLUXDB_BUCKET: str = "energy_data"
    
    # AI服务配置
    AI_SERVICE_URL: str = "http://localhost:8001"
    
    # 日志配置
    LOG_LEVEL: str = "INFO"
    
    # 监控配置
    ENABLE_METRICS: bool = True
    
    class Config:
        env_file = ".env"
        case_sensitive = True


@lru_cache()
def get_settings() -> Settings:
    """获取应用设置（缓存）"""
    return Settings() 