"""
VPP算法服务完整测试套件 v2.0
==============================

测试覆盖范围：
- 单元测试：核心算法功能
- 集成测试：API接口和数据库
- 性能测试：并发和响应时间
- 安全测试：输入验证和SQL注入防护
- 边界测试：极端情况处理

作者: VPP Development Team
版本: 2.0.0  
更新: 2024-12-29
"""

import asyncio
import json
import os
import pytest
import time
from datetime import datetime, timedelta
from typing import Dict, List, Any
from unittest.mock import Mock, patch, MagicMock
import warnings

import numpy as np
import pandas as pd
from fastapi.testclient import TestClient
from pydantic import ValidationError
import psutil

# 导入被测试的模块
from core.algorithm_engine import (
    AlgorithmEngine, StorageConfig, OptimizationRequest,
    OptimizationResult, PriceSignalProcessor, StorageOptimizer,
    AlgorithmConstants
)
from core.database_manager import (
    DatabaseManager, DatabaseConfig, QueryValidator, 
    CacheManager, get_database_manager
)
from api.v2.routes import app, verify_api_key, verify_jwt_token
from common.exceptions import (
    VppAlgorithmException, OptimizationException,
    DataValidationException, AuthenticationException
)


# ============================================================================
# 测试配置和工具
# ============================================================================

class TestConfig:
    """测试配置常量"""
    SAMPLE_NODE_ID = "test_node_001"
    SAMPLE_DEMAND_FORECAST = [100.0 + 50 * np.sin(i * np.pi / 48) for i in range(96)]
    SAMPLE_HOURLY_PRICES = [0.3 + 0.2 * np.sin(i * np.pi / 12) for i in range(24)]
    SAMPLE_TIME_PERIODS = [f"{i//4:02d}:{(i%4)*15:02d}" for i in range(96)]
    
    # 性能测试阈值
    MAX_OPTIMIZATION_TIME_SEC = 5.0
    MAX_API_RESPONSE_TIME_MS = 2000
    MAX_MEMORY_USAGE_MB = 500
    
    # 并发测试参数
    CONCURRENT_REQUESTS = 10
    STRESS_TEST_REQUESTS = 100


@pytest.fixture
def algorithm_engine():
    """算法引擎测试夹具"""
    return AlgorithmEngine()


@pytest.fixture  
def storage_config():
    """储能配置测试夹具"""
    return StorageConfig(
        capacity_mwh=50.0,
        power_mw=25.0,
        efficiency=0.95,
        min_soc=0.1,
        max_soc=0.9,
        initial_soc=0.5
    )


@pytest.fixture
def api_client():
    """API客户端测试夹具"""
    return TestClient(app)


@pytest.fixture
def mock_database():
    """模拟数据库测试夹具"""
    mock_db = Mock(spec=DatabaseManager)
    mock_db.health_check.return_value = {
        "status": "healthy",
        "response_time_ms": 15.5,
        "connection_pool": {"active_connections": 2}
    }
    mock_db.execute_query.return_value = []
    mock_db.save_optimization_result.return_value = True
    return mock_db


# ============================================================================
# 单元测试 - 核心算法
# ============================================================================

class TestAlgorithmEngine:
    """算法引擎单元测试"""
    
    def test_engine_initialization(self, algorithm_engine):
        """测试引擎初始化"""
        assert algorithm_engine is not None
        assert algorithm_engine.storage_optimizer is None
        assert algorithm_engine.price_processor is not None
        assert algorithm_engine.perf_optimizer is not None
    
    def test_storage_optimizer_initialization(self, algorithm_engine, storage_config):
        """测试储能优化器初始化"""
        algorithm_engine.initialize_storage_optimizer(storage_config)
        
        assert algorithm_engine.storage_optimizer is not None
        assert algorithm_engine.storage_optimizer.config == storage_config
    
    def test_storage_optimization_basic(self, algorithm_engine):
        """测试基本储能优化"""
        result = algorithm_engine.run_storage_optimization(
            node_id=TestConfig.SAMPLE_NODE_ID,
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        # 验证结果格式
        assert "node_id" in result
        assert "charging_schedule" in result
        assert "discharging_schedule" in result
        assert "soc_profile" in result
        assert "total_cost" in result
        assert "total_revenue" in result
        assert "net_profit" in result
        
        # 验证数据长度
        assert len(result["charging_schedule"]) == 96
        assert len(result["discharging_schedule"]) == 96
        assert len(result["soc_profile"]) == 96
        
        # 验证数据类型和范围
        assert all(isinstance(x, (int, float)) for x in result["charging_schedule"])
        assert all(isinstance(x, (int, float)) for x in result["discharging_schedule"])
        assert all(0 <= x <= 1 for x in result["soc_profile"])
    
    @pytest.mark.parametrize("initial_soc", [0.0, 0.2, 0.5, 0.8, 1.0])
    def test_storage_optimization_different_soc(self, algorithm_engine, initial_soc):
        """测试不同初始SOC的优化"""
        result = algorithm_engine.run_storage_optimization(
            node_id=TestConfig.SAMPLE_NODE_ID,
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS,
            initial_soc=initial_soc
        )
        
        # 验证初始SOC设置
        assert abs(result["soc_profile"][0] - initial_soc) < 0.01
    
    def test_input_validation(self, algorithm_engine):
        """测试输入验证"""
        # 测试无效需求预测长度
        with pytest.raises(DataValidationException):
            algorithm_engine.run_storage_optimization(
                node_id=TestConfig.SAMPLE_NODE_ID,
                demand_forecast=[100.0] * 50,  # 错误长度
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
        
        # 测试无效电价长度
        with pytest.raises(DataValidationException):
            algorithm_engine.run_storage_optimization(
                node_id=TestConfig.SAMPLE_NODE_ID,
                demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
                hourly_prices=[0.5] * 12,  # 错误长度
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
        
        # 测试负需求值
        with pytest.raises(DataValidationException):
            algorithm_engine.run_storage_optimization(
                node_id=TestConfig.SAMPLE_NODE_ID,
                demand_forecast=[-10.0] * 96,  # 负值
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )


class TestStorageConfig:
    """储能配置测试"""
    
    def test_valid_config(self):
        """测试有效配置"""
        config = StorageConfig(
            capacity_mwh=100.0,
            power_mw=50.0,
            efficiency=0.95,
            min_soc=0.1,
            max_soc=0.9
        )
        
        assert config.capacity_mwh == 100.0
        assert config.power_mw == 50.0
        assert config.efficiency == 0.95
    
    def test_invalid_soc_limits(self):
        """测试无效SOC限制"""
        with pytest.raises(ValueError):
            StorageConfig(
                capacity_mwh=100.0,
                power_mw=50.0,
                min_soc=0.8,  # min > max
                max_soc=0.6
            )
    
    def test_capacity_limits(self):
        """测试容量限制"""
        with pytest.raises(ValueError):
            StorageConfig(
                capacity_mwh=-10.0,  # 负值
                power_mw=50.0
            )
        
        with pytest.raises(ValueError):
            StorageConfig(
                capacity_mwh=2000.0,  # 超出限制
                power_mw=50.0
            )


class TestPriceSignalProcessor:
    """价格信号处理器测试"""
    
    def test_hourly_to_15min_expansion(self):
        """测试小时电价扩展为15分钟"""
        processor = PriceSignalProcessor()
        hourly_prices = [0.5] * 24
        
        expanded_prices = processor.expand_hourly_to_15min(hourly_prices)
        
        assert len(expanded_prices) == 96
        assert all(p == 0.5 for p in expanded_prices)
    
    def test_invalid_hourly_prices_length(self):
        """测试无效小时电价长度"""
        processor = PriceSignalProcessor()
        
        with pytest.raises(DataValidationException):
            processor.expand_hourly_to_15min([0.5] * 12)  # 错误长度
    
    def test_time_of_use_weights(self):
        """测试分时电价权重计算"""
        processor = PriceSignalProcessor()
        
        # 创建模拟价格数据（峰谷电价）
        prices = [0.3] * 32 + [0.8] * 40 + [0.3] * 24  # 96个点
        
        weights = processor.calculate_time_of_use_weights(prices)
        
        assert "peak_hours_weight" in weights
        assert "off_peak_weight" in weights
        assert "price_volatility" in weights
        assert weights["peak_hours_weight"] > weights["off_peak_weight"]


# ============================================================================
# 数据库测试
# ============================================================================

class TestDatabaseManager:
    """数据库管理器测试"""
    
    def test_config_validation(self):
        """测试配置验证"""
        # 测试缺少密码
        with pytest.raises(ValueError):
            DatabaseConfig(password="")
        
        # 测试无效连接池大小
        with pytest.raises(ValueError):
            DatabaseConfig(password="test", pool_size=0)
    
    def test_query_validator(self):
        """测试查询验证器"""
        validator = QueryValidator()
        
        # 测试安全查询
        safe_query = "SELECT * FROM load_forecast WHERE node_id = :node_id"
        assert validator.validate_query(safe_query, "SELECT")
        
        # 测试危险查询
        dangerous_query = "SELECT * FROM users; DROP TABLE users; --"
        assert not validator.validate_query(dangerous_query, "SELECT")
        
        # 测试SQL注入尝试
        injection_query = "SELECT * FROM users WHERE id = '1' OR '1'='1'"
        assert not validator.validate_query(injection_query, "SELECT")
    
    def test_parameter_sanitization(self):
        """测试参数清理"""
        validator = QueryValidator()
        
        # 测试正常参数
        clean_params = validator.sanitize_parameters({
            "node_id": "test_001",
            "start_time": "2024-01-01",
            "limit": 100
        })
        
        assert clean_params["node_id"] == "test_001"
        assert clean_params["limit"] == 100
        
        # 测试危险参数
        with pytest.raises(DataValidationException):
            validator.sanitize_parameters({
                "'; DROP TABLE users; --": "malicious"
            })
    
    @patch('redis.Redis')
    def test_cache_manager(self, mock_redis):
        """测试缓存管理器"""
        mock_redis_instance = Mock()
        mock_redis.return_value = mock_redis_instance
        mock_redis_instance.ping.return_value = True
        mock_redis_instance.get.return_value = None
        
        cache_manager = CacheManager({
            'host': 'localhost',
            'port': 6379,
            'password': 'test'
        })
        
        # 测试缓存设置和获取
        test_query = "SELECT * FROM test"
        test_params = {"id": 1}
        test_data = [{"id": 1, "name": "test"}]
        
        cache_manager.set(test_query, test_params, test_data)
        
        # 验证Redis调用
        mock_redis_instance.setex.assert_called_once()


# ============================================================================
# API集成测试
# ============================================================================

class TestAPIIntegration:
    """API集成测试"""
    
    def test_health_check(self, api_client):
        """测试健康检查端点"""
        response = api_client.get("/health")
        
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"
        assert "version" in data
        assert "uptime_seconds" in data
    
    def test_root_endpoint(self, api_client):
        """测试根端点"""
        response = api_client.get("/")
        
        assert response.status_code == 200
        data = response.json()
        assert data["success"] is True
        assert "service" in data["data"]
    
    @patch('api.v2.routes.get_database_manager')
    def test_storage_optimization_endpoint(self, mock_db_manager, api_client):
        """测试储能优化端点"""
        # 模拟数据库管理器
        mock_db_manager.return_value = Mock()
        
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": TestConfig.SAMPLE_DEMAND_FORECAST,
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS,
            "initial_soc": 0.5
        }
        
        headers = {"X-API-Key": "vpp-service-key"}
        
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 200
        data = response.json()
        assert data["node_id"] == TestConfig.SAMPLE_NODE_ID
        assert "charging_schedule" in data
        assert "discharging_schedule" in data
    
    def test_authentication_required(self, api_client):
        """测试认证要求"""
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": TestConfig.SAMPLE_DEMAND_FORECAST,
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        # 无认证请求
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=request_data
        )
        
        assert response.status_code == 401
    
    def test_invalid_api_key(self, api_client):
        """测试无效API密钥"""
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": TestConfig.SAMPLE_DEMAND_FORECAST,
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        headers = {"X-API-Key": "invalid-key"}
        
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 401
    
    def test_input_validation_api(self, api_client):
        """测试API输入验证"""
        # 测试无效数据长度
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": [100.0] * 50,  # 错误长度
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        headers = {"X-API-Key": "vpp-service-key"}
        
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=request_data,
            headers=headers
        )
        
        assert response.status_code == 422


# ============================================================================
# 性能测试
# ============================================================================

class TestPerformance:
    """性能测试"""
    
    def test_optimization_performance(self, algorithm_engine):
        """测试优化性能"""
        start_time = time.time()
        
        result = algorithm_engine.run_storage_optimization(
            node_id=TestConfig.SAMPLE_NODE_ID,
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        elapsed_time = time.time() - start_time
        
        # 验证性能要求
        assert elapsed_time < TestConfig.MAX_OPTIMIZATION_TIME_SEC
        assert result["solve_time_seconds"] < TestConfig.MAX_OPTIMIZATION_TIME_SEC
    
    def test_memory_usage(self, algorithm_engine):
        """测试内存使用"""
        process = psutil.Process()
        memory_before = process.memory_info().rss / 1024 / 1024  # MB
        
        # 运行多次优化
        for _ in range(10):
            algorithm_engine.run_storage_optimization(
                node_id=f"test_node_{_}",
                demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
        
        memory_after = process.memory_info().rss / 1024 / 1024  # MB
        memory_increase = memory_after - memory_before
        
        # 验证内存增长合理
        assert memory_increase < TestConfig.MAX_MEMORY_USAGE_MB
    
    @pytest.mark.asyncio
    async def test_concurrent_optimization(self, algorithm_engine):
        """测试并发优化"""
        async def run_optimization(node_id: str):
            return algorithm_engine.run_storage_optimization(
                node_id=node_id,
                demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
        
        start_time = time.time()
        
        # 创建并发任务
        tasks = [
            run_optimization(f"node_{i}")
            for i in range(TestConfig.CONCURRENT_REQUESTS)
        ]
        
        results = await asyncio.gather(*tasks)
        
        elapsed_time = time.time() - start_time
        
        # 验证所有请求都成功
        assert len(results) == TestConfig.CONCURRENT_REQUESTS
        assert all("node_id" in result for result in results)
        
        # 验证并发性能
        avg_time_per_request = elapsed_time / TestConfig.CONCURRENT_REQUESTS
        assert avg_time_per_request < TestConfig.MAX_OPTIMIZATION_TIME_SEC
    
    def test_api_response_time(self, api_client):
        """测试API响应时间"""
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": TestConfig.SAMPLE_DEMAND_FORECAST,
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        headers = {"X-API-Key": "vpp-service-key"}
        
        start_time = time.time()
        
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=request_data,
            headers=headers
        )
        
        elapsed_time = (time.time() - start_time) * 1000  # 转换为毫秒
        
        assert response.status_code == 200
        assert elapsed_time < TestConfig.MAX_API_RESPONSE_TIME_MS
        
        # 检查响应头中的处理时间
        process_time = float(response.headers.get("X-Process-Time", "0"))
        assert process_time < TestConfig.MAX_OPTIMIZATION_TIME_SEC


# ============================================================================
# 安全测试
# ============================================================================

class TestSecurity:
    """安全测试"""
    
    def test_sql_injection_prevention(self):
        """测试SQL注入防护"""
        validator = QueryValidator()
        
        # 常见SQL注入模式
        injection_attempts = [
            "'; DROP TABLE users; --",
            "' OR '1'='1",
            "' UNION SELECT * FROM passwords --",
            "'; EXEC xp_cmdshell('format c:'); --",
            "' AND 1=1 --",
            "') OR ('1'='1",
        ]
        
        for injection in injection_attempts:
            # 测试查询验证
            malicious_query = f"SELECT * FROM users WHERE name = '{injection}'"
            assert not validator.validate_query(malicious_query, "SELECT")
            
            # 测试参数清理
            try:
                validator.sanitize_parameters({"malicious_param": injection})
                # 如果没有抛出异常，验证参数被清理
                cleaned = validator.sanitize_parameters({"param": injection})
                assert injection not in str(cleaned["param"])
            except DataValidationException:
                # 预期的异常
                pass
    
    def test_input_size_limits(self, api_client):
        """测试输入大小限制"""
        # 创建超大输入数据
        oversized_data = {
            "node_id": "test_node",
            "demand_forecast": [1000.0] * 96,
            "hourly_prices": [999.0] * 24,  # 超出价格限制
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        headers = {"X-API-Key": "vpp-service-key"}
        
        response = api_client.post(
            "/api/v2/optimization/storage",
            json=oversized_data,
            headers=headers
        )
        
        # 应该返回验证错误
        assert response.status_code == 422
    
    def test_parameter_validation(self):
        """测试参数验证"""
        validator = QueryValidator()
        
        # 测试危险参数名
        dangerous_params = [
            {"'; DROP TABLE users; --": "value"},
            {"param'; DELETE FROM data; --": "value"},
            {"<script>alert('xss')</script>": "value"},
        ]
        
        for params in dangerous_params:
            with pytest.raises(DataValidationException):
                validator.sanitize_parameters(params)
    
    def test_rate_limiting_simulation(self, api_client):
        """测试速率限制（模拟）"""
        request_data = {
            "node_id": TestConfig.SAMPLE_NODE_ID,
            "demand_forecast": TestConfig.SAMPLE_DEMAND_FORECAST,
            "hourly_prices": TestConfig.SAMPLE_HOURLY_PRICES,
            "time_periods": TestConfig.SAMPLE_TIME_PERIODS
        }
        
        headers = {"X-API-Key": "vpp-service-key"}
        
        # 发送大量请求（实际环境中会被限流）
        responses = []
        for _ in range(5):  # 降低数量避免测试超时
            response = api_client.post(
                "/api/v2/optimization/storage",
                json=request_data,
                headers=headers
            )
            responses.append(response.status_code)
        
        # 至少有一些请求成功
        success_count = sum(1 for status in responses if status == 200)
        assert success_count > 0


# ============================================================================
# 边界测试
# ============================================================================

class TestBoundaryConditions:
    """边界条件测试"""
    
    def test_extreme_demand_values(self, algorithm_engine):
        """测试极端需求值"""
        # 测试全零需求
        zero_demand = [0.0] * 96
        result = algorithm_engine.run_storage_optimization(
            node_id="zero_demand_test",
            demand_forecast=zero_demand,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        assert "node_id" in result
        assert all(d >= 0 for d in result["discharging_schedule"])
        
        # 测试高需求
        high_demand = [1000.0] * 96
        result = algorithm_engine.run_storage_optimization(
            node_id="high_demand_test",
            demand_forecast=high_demand,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        assert "node_id" in result
    
    def test_extreme_price_values(self, algorithm_engine):
        """测试极端价格值"""
        # 测试负电价
        negative_prices = [-0.1] * 24
        result = algorithm_engine.run_storage_optimization(
            node_id="negative_price_test",
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=negative_prices,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        assert "node_id" in result
        
        # 测试高电价
        high_prices = [10.0] * 24
        result = algorithm_engine.run_storage_optimization(
            node_id="high_price_test",
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=high_prices,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS
        )
        
        assert "node_id" in result
    
    def test_extreme_soc_values(self, algorithm_engine):
        """测试极端SOC值"""
        # 测试最小SOC
        result = algorithm_engine.run_storage_optimization(
            node_id="min_soc_test",
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS,
            initial_soc=0.0
        )
        
        assert result["soc_profile"][0] >= 0.0
        
        # 测试最大SOC
        result = algorithm_engine.run_storage_optimization(
            node_id="max_soc_test",
            demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
            hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
            time_periods=TestConfig.SAMPLE_TIME_PERIODS,
            initial_soc=1.0
        )
        
        assert result["soc_profile"][0] <= 1.0


# ============================================================================
# 压力测试
# ============================================================================

@pytest.mark.slow
class TestStress:
    """压力测试（标记为慢速测试）"""
    
    def test_sustained_load(self, algorithm_engine):
        """测试持续负载"""
        start_time = time.time()
        
        for i in range(50):  # 运行50次优化
            result = algorithm_engine.run_storage_optimization(
                node_id=f"stress_test_node_{i}",
                demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
            
            assert "node_id" in result
        
        total_time = time.time() - start_time
        avg_time = total_time / 50
        
        # 验证平均性能
        assert avg_time < TestConfig.MAX_OPTIMIZATION_TIME_SEC
    
    @pytest.mark.asyncio
    async def test_high_concurrency(self, algorithm_engine):
        """测试高并发"""
        async def run_optimization_with_delay(node_id: str):
            # 添加随机延迟模拟真实场景
            await asyncio.sleep(np.random.uniform(0, 0.1))
            return algorithm_engine.run_storage_optimization(
                node_id=node_id,
                demand_forecast=TestConfig.SAMPLE_DEMAND_FORECAST,
                hourly_prices=TestConfig.SAMPLE_HOURLY_PRICES,
                time_periods=TestConfig.SAMPLE_TIME_PERIODS
            )
        
        # 创建大量并发任务
        tasks = [
            run_optimization_with_delay(f"concurrent_node_{i}")
            for i in range(TestConfig.STRESS_TEST_REQUESTS)
        ]
        
        start_time = time.time()
        results = await asyncio.gather(*tasks, return_exceptions=True)
        elapsed_time = time.time() - start_time
        
        # 统计成功和失败
        successful_results = [r for r in results if not isinstance(r, Exception)]
        failed_results = [r for r in results if isinstance(r, Exception)]
        
        # 验证大部分请求成功
        success_rate = len(successful_results) / len(results)
        assert success_rate > 0.8  # 至少80%成功率
        
        # 记录性能统计
        # 使用日志记录替代print输出
        logger.info(f"压力测试结果: 总请求数={len(results)}, 成功数={len(successful_results)}, "
                   f"失败数={len(failed_results)}, 成功率={success_rate:.2%}, "
                   f"总耗时={elapsed_time:.2f}秒, 平均响应时间={elapsed_time/len(results):.3f}秒")


# ============================================================================
# 测试运行配置
# ============================================================================

if __name__ == "__main__":
    # 设置测试环境
    os.environ.setdefault("DB_PASSWORD", "test_password")
    os.environ.setdefault("REDIS_PASSWORD", "test_redis_password")
    
    # 运行测试
    pytest.main([
        __file__,
        "-v",  # 详细输出
        "--tb=short",  # 简短错误信息
        "--disable-warnings",  # 禁用警告
        "-m", "not slow",  # 默认不运行慢速测试
        "--cov=core",  # 代码覆盖率
        "--cov=api",
        "--cov-report=html",  # HTML覆盖率报告
        "--cov-report=term-missing"  # 终端显示未覆盖行
    ])
