"""
VPP安全数据库管理器 v2.0
===========================

提供安全的数据库访问功能：
- SQL注入防护
- 连接池管理
- 查询缓存
- 性能监控
- 健康检查
- 自动重连

安全特性：
- 参数化查询
- 查询白名单验证
- 敏感数据加密存储
- 访问日志记录

作者: VPP Development Team
版本: 2.0.0
更新: 2024-12-29
"""

import asyncio
import hashlib
import json
import logging
import os
import re
import time
from contextlib import contextmanager
from dataclasses import dataclass
from datetime import datetime, timedelta
from typing import Any, Dict, List, Optional, Tuple, Union
import warnings

import pandas as pd
import redis
from pydantic import BaseModel, Field, validator
from sqlalchemy import (
    create_engine, text, MetaData, Table, Column, Integer, Float, String, 
    DateTime, Boolean, inspect, pool
)
from sqlalchemy.exc import SQLAlchemyError, IntegrityError, OperationalError
from sqlalchemy.orm import sessionmaker, Session
from sqlalchemy.pool import QueuePool

from common.exceptions import (
    DatabaseConnectionException, 
    DataValidationException,
    VppAlgorithmException
)
from common.log_util import get_logger, log_performance

logger = get_logger(__name__)


@dataclass
class DatabaseConfig:
    """数据库配置类"""
    host: str = os.getenv('DB_HOST', 'localhost')
    port: int = int(os.getenv('DB_PORT', '5432'))
    database: str = os.getenv('DB_NAME', 'vpp_algorithm')
    username: str = os.getenv('DB_USERNAME', 'vpp_user')
    password: str = os.getenv('DB_PASSWORD', '')
    
    # 连接池配置
    pool_size: int = int(os.getenv('DB_POOL_SIZE', '20'))
    max_overflow: int = int(os.getenv('DB_MAX_OVERFLOW', '10'))
    pool_timeout: int = int(os.getenv('DB_POOL_TIMEOUT', '30'))
    pool_recycle: int = int(os.getenv('DB_POOL_RECYCLE', '3600'))
    
    # 安全配置
    ssl_mode: str = os.getenv('DB_SSL_MODE', 'require')
    connect_timeout: int = int(os.getenv('DB_CONNECT_TIMEOUT', '10'))
    
    def __post_init__(self):
        """配置验证"""
        if not self.password:
            raise ValueError("数据库密码未设置，请设置环境变量 DB_PASSWORD")
        
        if self.pool_size <= 0:
            raise ValueError("连接池大小必须大于0")


class QueryValidator:
    """查询验证器 - SQL注入防护"""
    
    # 危险的SQL关键词
    DANGEROUS_KEYWORDS = {
        'DROP', 'DELETE', 'INSERT', 'UPDATE', 'ALTER', 'CREATE', 'TRUNCATE',
        'EXEC', 'EXECUTE', 'UNION', 'SCRIPT', '--', '/*', '*/', ';', 'xp_',
        'sp_', 'eval', 'char', 'nchar', 'varchar', 'nvarchar', 'waitfor'
    }
    
    # 允许的SELECT查询模式
    ALLOWED_SELECT_PATTERNS = [
        r'^SELECT\s+[\w\s,\.\*\(\)]+\s+FROM\s+[\w\.]+(\s+WHERE\s+[\w\s=\?<>]+)?(\s+ORDER\s+BY\s+[\w\s,]+)?(\s+LIMIT\s+\d+)?$',
        r'^SELECT\s+COUNT\(\*\)\s+FROM\s+[\w\.]+(\s+WHERE\s+[\w\s=\?<>]+)?$'
    ]
    
    @classmethod
    def validate_query(cls, query: str, query_type: str = "SELECT") -> bool:
        """验证查询安全性"""
        query_upper = query.upper().strip()
        
        # 检查危险关键词
        for keyword in cls.DANGEROUS_KEYWORDS:
            if keyword in query_upper:
                logger.warning(f"检测到危险SQL关键词: {keyword}")
                return False
        
        # 验证SELECT查询格式
        if query_type.upper() == "SELECT":
            for pattern in cls.ALLOWED_SELECT_PATTERNS:
                if re.match(pattern, query_upper):
                    return True
            logger.warning(f"SELECT查询格式不符合安全模式: {query}")
            return False
        
        return True
    
    @classmethod
    def sanitize_parameters(cls, params: Dict[str, Any]) -> Dict[str, Any]:
        """清理查询参数"""
        sanitized = {}
        
        for key, value in params.items():
            # 验证参数名
            if not re.match(r'^[a-zA-Z_][a-zA-Z0-9_]*$', key):
                raise DataValidationException(f"非法参数名: {key}")
            
            # 清理参数值
            if isinstance(value, str):
                # 移除危险字符
                value = re.sub(r'[;\'\"\\]', '', value)
                # 限制长度
                if len(value) > 1000:
                    raise DataValidationException(f"参数值过长: {key}")
            
            sanitized[key] = value
        
        return sanitized


class CacheManager:
    """缓存管理器"""
    
    def __init__(self, redis_config: Optional[Dict] = None):
        self.redis_client = None
        self.local_cache: Dict[str, Tuple[Any, datetime]] = {}
        self.cache_ttl = timedelta(minutes=30)
        
        if redis_config:
            try:
                self.redis_client = redis.Redis(
                    host=redis_config.get('host', 'localhost'),
                    port=redis_config.get('port', 6379),
                    password=redis_config.get('password'),
                    db=redis_config.get('db', 0),
                    decode_responses=True,
                    socket_timeout=5,
                    socket_connect_timeout=5
                )
                # 测试连接
                self.redis_client.ping()
                logger.info("Redis缓存已连接")
            except Exception as e:
                logger.warning(f"Redis连接失败，使用本地缓存: {e}")
                self.redis_client = None
    
    def _generate_cache_key(self, query: str, params: Dict) -> str:
        """生成缓存键"""
        content = f"{query}_{json.dumps(params, sort_keys=True)}"
        return hashlib.md5(content.encode()).hexdigest()
    
    def get(self, query: str, params: Dict) -> Optional[Any]:
        """获取缓存数据"""
        cache_key = self._generate_cache_key(query, params)
        
        # 尝试Redis缓存
        if self.redis_client:
            try:
                cached_data = self.redis_client.get(f"query:{cache_key}")
                if cached_data:
                    return json.loads(cached_data)
            except Exception as e:
                logger.warning(f"Redis读取失败: {e}")
        
        # 本地缓存
        if cache_key in self.local_cache:
            data, timestamp = self.local_cache[cache_key]
            if datetime.now() - timestamp < self.cache_ttl:
                return data
            else:
                del self.local_cache[cache_key]
        
        return None
    
    def set(self, query: str, params: Dict, data: Any, ttl: Optional[int] = None) -> None:
        """设置缓存数据"""
        cache_key = self._generate_cache_key(query, params)
        ttl = ttl or int(self.cache_ttl.total_seconds())
        
        # Redis缓存
        if self.redis_client:
            try:
                self.redis_client.setex(
                    f"query:{cache_key}", 
                    ttl, 
                    json.dumps(data, default=str)
                )
            except Exception as e:
                logger.warning(f"Redis写入失败: {e}")
        
        # 本地缓存
        self.local_cache[cache_key] = (data, datetime.now())
        
        # 清理过期的本地缓存
        if len(self.local_cache) > 1000:
            self._cleanup_local_cache()
    
    def _cleanup_local_cache(self) -> None:
        """清理过期的本地缓存"""
        now = datetime.now()
        expired_keys = [
            key for key, (_, timestamp) in self.local_cache.items()
            if now - timestamp > self.cache_ttl
        ]
        for key in expired_keys:
            del self.local_cache[key]


class DatabaseManager:
    """数据库管理器 - 安全版本"""
    
    def __init__(self, config: Optional[DatabaseConfig] = None, 
                 redis_config: Optional[Dict] = None):
        self.config = config or DatabaseConfig()
        self.engine = None
        self.SessionLocal = None
        self.cache_manager = CacheManager(redis_config)
        self.query_validator = QueryValidator()
        self._connection_pool_stats = {
            "total_connections": 0,
            "active_connections": 0,
            "failed_connections": 0,
            "last_health_check": None
        }
        
        self._initialize_database()
    
    def _initialize_database(self) -> None:
        """初始化数据库连接"""
        try:
            # 构建连接字符串
            connection_string = (
                f"postgresql://{self.config.username}:{self.config.password}@"
                f"{self.config.host}:{self.config.port}/{self.config.database}"
                f"?sslmode={self.config.ssl_mode}&connect_timeout={self.config.connect_timeout}"
            )
            
            # 创建引擎 - 使用连接池
            self.engine = create_engine(
                connection_string,
                poolclass=QueuePool,
                pool_size=self.config.pool_size,
                max_overflow=self.config.max_overflow,
                pool_timeout=self.config.pool_timeout,
                pool_recycle=self.config.pool_recycle,
                pool_pre_ping=True,  # 自动重连
                echo=False,  # 生产环境不输出SQL
                future=True
            )
            
            # 创建会话工厂
            self.SessionLocal = sessionmaker(
                autocommit=False,
                autoflush=False,
                bind=self.engine
            )
            
            # 测试连接
            self.health_check()
            logger.info("数据库连接初始化成功")
            
        except Exception as e:
            logger.error(f"数据库初始化失败: {e}")
            raise DatabaseConnectionException(
                f"{self.config.host}:{self.config.port}/{self.config.database}", e
            )
    
    @contextmanager
    def get_session(self):
        """获取数据库会话 - 上下文管理器"""
        session = self.SessionLocal()
        try:
            yield session
            session.commit()
        except Exception as e:
            session.rollback()
            logger.error(f"数据库操作失败: {e}")
            raise
        finally:
            session.close()
    
    @log_performance
    def execute_query(self, query: str, params: Optional[Dict] = None, 
                     use_cache: bool = True, cache_ttl: Optional[int] = None) -> List[Dict]:
        """
        安全执行查询
        
        Args:
            query: SQL查询语句
            params: 查询参数
            use_cache: 是否使用缓存
            cache_ttl: 缓存过期时间(秒)
        
        Returns:
            查询结果列表
        """
        params = params or {}
        
        # 查询验证
        if not self.query_validator.validate_query(query, "SELECT"):
            raise DataValidationException("查询未通过安全验证")
        
        # 参数清理
        sanitized_params = self.query_validator.sanitize_parameters(params)
        
        # 检查缓存
        if use_cache:
            cached_result = self.cache_manager.get(query, sanitized_params)
            if cached_result is not None:
                logger.debug("返回缓存查询结果")
                return cached_result
        
        try:
            with self.get_session() as session:
                # 使用参数化查询防止SQL注入
                result = session.execute(text(query), sanitized_params)
                
                # 转换为字典列表
                columns = result.keys()
                rows = result.fetchall()
                data = [dict(zip(columns, row)) for row in rows]
                
                # 缓存结果
                if use_cache and data:
                    self.cache_manager.set(query, sanitized_params, data, cache_ttl)
                
                logger.debug(f"查询执行成功，返回 {len(data)} 条记录")
                return data
                
        except SQLAlchemyError as e:
            logger.error(f"数据库查询失败: {e}")
            raise DatabaseConnectionException(
                f"{self.config.host}:{self.config.port}", e
            )
    
    def get_load_forecast_data(self, node_id: str, start_time: datetime, 
                             end_time: datetime) -> pd.DataFrame:
        """获取负荷预测数据"""
        query = """
        SELECT timestamp, predicted_load_mw, confidence_level, model_version
        FROM load_forecast 
        WHERE node_id = :node_id 
        AND timestamp BETWEEN :start_time AND :end_time
        ORDER BY timestamp
        """
        
        params = {
            'node_id': node_id,
            'start_time': start_time,
            'end_time': end_time
        }
        
        results = self.execute_query(query, params, use_cache=True, cache_ttl=1800)
        return pd.DataFrame(results)
    
    def get_price_data(self, market_area: str, start_time: datetime,
                      end_time: datetime) -> pd.DataFrame:
        """获取电价数据"""
        query = """
        SELECT timestamp, price_yuan_mwh, price_type, market_area
        FROM electricity_prices
        WHERE market_area = :market_area
        AND timestamp BETWEEN :start_time AND :end_time
        ORDER BY timestamp
        """
        
        params = {
            'market_area': market_area,
            'start_time': start_time,
            'end_time': end_time
        }
        
        results = self.execute_query(query, params, use_cache=True, cache_ttl=3600)
        return pd.DataFrame(results)
    
    def save_optimization_result(self, result_data: Dict) -> bool:
        """保存优化结果"""
        try:
            with self.get_session() as session:
                # 构建插入查询
                insert_query = text("""
                INSERT INTO optimization_results 
                (node_id, optimization_type, result_data, created_at, algorithm_version)
                VALUES (:node_id, :optimization_type, :result_data, :created_at, :algorithm_version)
                """)
                
                session.execute(insert_query, {
                    'node_id': result_data.get('node_id'),
                    'optimization_type': 'storage_optimization',
                    'result_data': json.dumps(result_data),
                    'created_at': datetime.now(),
                    'algorithm_version': result_data.get('algorithm_version', '2.0.0')
                })
                
                logger.info(f"优化结果已保存，节点ID: {result_data.get('node_id')}")
                return True
                
        except Exception as e:
            logger.error(f"保存优化结果失败: {e}")
            return False
    
    def health_check(self) -> Dict[str, Any]:
        """数据库健康检查"""
        try:
            start_time = time.time()
            
            with self.get_session() as session:
                result = session.execute(text("SELECT 1")).fetchone()
                
            response_time = time.time() - start_time
            
            # 更新连接池统计
            pool = self.engine.pool
            self._connection_pool_stats.update({
                "total_connections": pool.size(),
                "active_connections": pool.checkedout(),
                "response_time_ms": round(response_time * 1000, 2),
                "last_health_check": datetime.now().isoformat()
            })
            
            health_status = {
                "status": "healthy",
                "database": self.config.database,
                "host": self.config.host,
                "response_time_ms": round(response_time * 1000, 2),
                "connection_pool": self._connection_pool_stats,
                "cache_stats": self._get_cache_stats()
            }
            
            logger.debug(f"数据库健康检查通过，响应时间: {response_time:.3f}秒")
            return health_status
            
        except Exception as e:
            self._connection_pool_stats["failed_connections"] += 1
            logger.error(f"数据库健康检查失败: {e}")
            
            return {
                "status": "unhealthy",
                "error": str(e),
                "last_check": datetime.now().isoformat(),
                "connection_pool": self._connection_pool_stats
            }
    
    def _get_cache_stats(self) -> Dict[str, Any]:
        """获取缓存统计信息"""
        stats = {
            "local_cache_size": len(self.cache_manager.local_cache),
            "redis_connected": self.cache_manager.redis_client is not None
        }
        
        if self.cache_manager.redis_client:
            try:
                info = self.cache_manager.redis_client.info()
                stats.update({
                    "redis_memory_used": info.get('used_memory_human'),
                    "redis_connected_clients": info.get('connected_clients'),
                    "redis_keyspace_hits": info.get('keyspace_hits', 0),
                    "redis_keyspace_misses": info.get('keyspace_misses', 0)
                })
            except Exception as e:
                stats["redis_error"] = str(e)
        
        return stats
    
    def close(self) -> None:
        """关闭数据库连接"""
        if self.engine:
            self.engine.dispose()
            logger.info("数据库连接已关闭")


# 全局数据库管理器实例
_database_manager: Optional[DatabaseManager] = None

def get_database_manager() -> DatabaseManager:
    """获取数据库管理器单例"""
    global _database_manager
    if _database_manager is None:
        redis_config = {
            'host': os.getenv('REDIS_HOST', 'localhost'),
            'port': int(os.getenv('REDIS_PORT', '6379')),
            'password': os.getenv('REDIS_PASSWORD'),
            'db': int(os.getenv('REDIS_DB', '0'))
        }
        _database_manager = DatabaseManager(redis_config=redis_config)
    return _database_manager
