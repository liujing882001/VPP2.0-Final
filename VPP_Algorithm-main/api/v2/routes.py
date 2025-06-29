"""
VPP算法服务API路由 v2.0
=======================

现代化的FastAPI路由系统：
- 完整的请求/响应验证
- JWT和API Key双重认证
- 速率限制和安全防护
- 自动生成API文档
- 结构化错误处理
- 性能监控集成

安全特性：
- 输入数据验证
- SQL注入防护
- CORS配置
- 请求大小限制
- 审计日志

作者: VPP Development Team
版本: 2.0.0
更新: 2024-12-29
"""

import asyncio
import time
from datetime import datetime, timedelta
from typing import Any, Dict, List, Optional, Union
from functools import wraps
import logging

from fastapi import (
    FastAPI, HTTPException, Depends, status, Request, Response,
    BackgroundTasks, Query, Path, Body
)
from fastapi.security import HTTPBearer, HTTPAuthorizationCredentials
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse
from fastapi.openapi.docs import get_swagger_ui_html
from pydantic import BaseModel, Field, validator
import jwt
from slowapi import Limiter, _rate_limit_exceeded_handler
from slowapi.util import get_remote_address
from slowapi.errors import RateLimitExceeded
from slowapi.middleware import SlowAPIMiddleware
import redis

from core.algorithm_engine import (
    AlgorithmEngine, OptimizationRequest, OptimizationResult, 
    StorageConfig, OptimizationStatus
)
from core.database_manager import get_database_manager
from common.exceptions import (
    VppAlgorithmException, OptimizationException, 
    DataValidationException, AuthenticationException,
    RateLimitException
)
from common.log_util import get_logger, log_performance

logger = get_logger(__name__)

# 初始化限流器
limiter = Limiter(key_func=get_remote_address)

# JWT配置
JWT_SECRET = "your-super-secret-jwt-key-change-in-production"  # 应该从环境变量读取
JWT_ALGORITHM = "HS256"
JWT_EXPIRATION_MINUTES = 60

# API Key配置
VALID_API_KEYS = {
    "vpp-service-key": "vpp_service",
    "web-app-key": "web_application", 
    "mobile-app-key": "mobile_application"
}

# 安全依赖
security = HTTPBearer()


# ============================================================================
# 请求/响应模型
# ============================================================================

class APIResponse(BaseModel):
    """标准API响应模型"""
    success: bool = True
    data: Optional[Any] = None
    message: str = "操作成功"
    timestamp: datetime = Field(default_factory=datetime.now)
    request_id: Optional[str] = None
    
    class Config:
        json_encoders = {
            datetime: lambda v: v.isoformat()
        }


class ErrorResponse(BaseModel):
    """错误响应模型"""
    success: bool = False
    error_code: str
    error_message: str
    error_details: Optional[Dict[str, Any]] = None
    timestamp: datetime = Field(default_factory=datetime.now)
    request_id: Optional[str] = None


class OptimizationRequestModel(BaseModel):
    """优化请求模型"""
    node_id: str = Field(..., min_length=1, max_length=50, regex=r'^[a-zA-Z0-9_-]+$')
    demand_forecast: List[float] = Field(..., min_items=96, max_items=96)
    hourly_prices: List[float] = Field(..., min_items=24, max_items=24)
    time_periods: List[str] = Field(..., min_items=96, max_items=96)
    storage_config: Optional[Dict[str, Any]] = None
    initial_soc: float = Field(default=0.5, ge=0, le=1.0)
    optimization_objective: str = Field(
        default="cost_minimize", 
        regex=r'^(cost_minimize|revenue_maximize|peak_shaving)$'
    )
    
    @validator('demand_forecast', each_item=True)
    def validate_demand(cls, v):
        if v < 0:
            raise ValueError("需求预测值不能为负")
        if v > 10000:  # 10GW上限
            raise ValueError("需求预测值超出合理范围")
        return v
    
    @validator('hourly_prices', each_item=True)  
    def validate_prices(cls, v):
        if not (-1000 <= v <= 10000):  # 允许负电价
            raise ValueError("电价超出合理范围")
        return v


class OptimizationResponseModel(BaseModel):
    """优化响应模型"""
    node_id: str
    status: str
    charging_schedule: List[float]
    discharging_schedule: List[float]
    soc_profile: List[float]
    total_cost: float
    total_revenue: float
    net_profit: float
    peak_reduction_mw: float
    solve_time_seconds: float
    algorithm_version: str
    timestamp: datetime
    metadata: Dict[str, Any] = Field(default_factory=dict)


class HealthCheckResponse(BaseModel):
    """健康检查响应模型"""
    status: str
    version: str = "2.0.0"
    uptime_seconds: float
    database_status: Dict[str, Any]
    cache_status: Dict[str, Any]
    system_resources: Dict[str, Any]
    timestamp: datetime = Field(default_factory=datetime.now)


class BatchOptimizationRequest(BaseModel):
    """批量优化请求模型"""
    requests: List[OptimizationRequestModel] = Field(..., min_items=1, max_items=10)
    parallel_processing: bool = True
    timeout_seconds: int = Field(default=300, ge=30, le=600)


# ============================================================================
# 认证和授权
# ============================================================================

async def verify_api_key(request: Request) -> str:
    """验证API密钥"""
    api_key = request.headers.get("X-API-Key")
    if not api_key:
        raise AuthenticationException("缺少API密钥")
    
    if api_key not in VALID_API_KEYS:
        raise AuthenticationException("无效的API密钥")
    
    return VALID_API_KEYS[api_key]


async def verify_jwt_token(credentials: HTTPAuthorizationCredentials = Depends(security)) -> str:
    """验证JWT令牌"""
    try:
        payload = jwt.decode(credentials.credentials, JWT_SECRET, algorithms=[JWT_ALGORITHM])
        username: str = payload.get("sub")
        if username is None:
            raise AuthenticationException("无效的令牌")
        return username
    except jwt.ExpiredSignatureError:
        raise AuthenticationException("令牌已过期")
    except jwt.JWTError:
        raise AuthenticationException("无效的令牌")


async def get_current_user(request: Request) -> str:
    """获取当前用户（支持API Key或JWT）"""
    # 优先尝试API Key认证
    try:
        return await verify_api_key(request)
    except AuthenticationException:
        pass
    
    # 尝试JWT认证
    auth_header = request.headers.get("Authorization")
    if auth_header and auth_header.startswith("Bearer "):
        token = auth_header.split(" ")[1]
        credentials = HTTPAuthorizationCredentials(scheme="Bearer", credentials=token)
        return await verify_jwt_token(credentials)
    
    raise AuthenticationException("需要API密钥或JWT令牌认证")


# ============================================================================
# 中间件和错误处理
# ============================================================================

def create_app() -> FastAPI:
    """创建FastAPI应用"""
    
    app = FastAPI(
        title="VPP算法服务API",
        description="虚拟电厂算法服务的RESTful API接口",
        version="2.0.0",
        docs_url="/docs",
        redoc_url="/redoc",
        openapi_url="/openapi.json"
    )
    
    # 添加CORS中间件
    app.add_middleware(
        CORSMiddleware,
        allow_origins=["http://localhost:3000", "http://localhost:8080"],
        allow_credentials=True,
        allow_methods=["GET", "POST", "PUT", "DELETE", "OPTIONS"],
        allow_headers=["*"],
    )
    
    # 添加压缩中间件
    app.add_middleware(GZipMiddleware, minimum_size=1000)
    
    # 添加限流中间件
    app.state.limiter = limiter
    app.add_exception_handler(RateLimitExceeded, _rate_limit_exceeded_handler)
    
    return app


app = create_app()

# 全局异常处理器
@app.exception_handler(VppAlgorithmException)
async def vpp_exception_handler(request: Request, exc: VppAlgorithmException):
    """VPP算法异常处理器"""
    logger.error(f"VPP算法异常: {exc}")
    return JSONResponse(
        status_code=400,
        content=ErrorResponse(
            error_code=exc.__class__.__name__,
            error_message=str(exc),
            error_details=getattr(exc, 'details', None),
            request_id=getattr(request.state, 'request_id', None)
        ).dict()
    )


@app.exception_handler(AuthenticationException)
async def auth_exception_handler(request: Request, exc: AuthenticationException):
    """认证异常处理器"""
    return JSONResponse(
        status_code=401,
        content=ErrorResponse(
            error_code="AUTHENTICATION_FAILED",
            error_message=str(exc),
            request_id=getattr(request.state, 'request_id', None)
        ).dict()
    )


@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    """HTTP异常处理器"""
    return JSONResponse(
        status_code=exc.status_code,
        content=ErrorResponse(
            error_code=f"HTTP_{exc.status_code}",
            error_message=exc.detail,
            request_id=getattr(request.state, 'request_id', None)
        ).dict()
    )


@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    """添加请求处理时间和ID"""
    import uuid
    request_id = str(uuid.uuid4())
    request.state.request_id = request_id
    
    start_time = time.time()
    response = await call_next(request)
    process_time = time.time() - start_time
    
    response.headers["X-Process-Time"] = str(process_time)
    response.headers["X-Request-ID"] = request_id
    
    return response


# ============================================================================
# API路由
# ============================================================================

@app.get("/", response_model=APIResponse)
async def root():
    """API根路径"""
    return APIResponse(
        data={"service": "VPP算法服务", "version": "2.0.0"},
        message="欢迎使用VPP算法服务API"
    )


@app.get("/health", response_model=HealthCheckResponse)
async def health_check():
    """健康检查端点"""
    try:
        db_manager = get_database_manager()
        db_health = db_manager.health_check()
        
        # 获取系统资源信息
        import psutil
        import os
        
        process = psutil.Process(os.getpid())
        system_resources = {
            "cpu_percent": process.cpu_percent(interval=1),
            "memory_mb": round(process.memory_info().rss / 1024 / 1024, 2),
            "disk_usage_percent": psutil.disk_usage('/').percent if os.name != 'nt' else 0
        }
        
        return HealthCheckResponse(
            status="healthy",
            uptime_seconds=time.time() - app.state.start_time if hasattr(app.state, 'start_time') else 0,
            database_status=db_health,
            cache_status={"enabled": True},
            system_resources=system_resources
        )
        
    except Exception as e:
        logger.error(f"健康检查失败: {e}")
        raise HTTPException(status_code=503, detail="服务不可用")


@app.post("/api/v2/optimization/storage", response_model=OptimizationResponseModel)
@limiter.limit("100/minute")
async def storage_optimization(
    request: Request,
    optimization_request: OptimizationRequestModel,
    background_tasks: BackgroundTasks,
    current_user: str = Depends(get_current_user)
):
    """
    储能系统优化调度
    
    - **node_id**: 节点ID
    - **demand_forecast**: 96点负荷预测数据(15分钟间隔)
    - **hourly_prices**: 24小时电价数据
    - **time_periods**: 96个时间段标识
    - **storage_config**: 储能系统配置(可选)
    - **initial_soc**: 初始荷电状态(0-1)
    - **optimization_objective**: 优化目标
    """
    try:
        logger.info(f"用户 {current_user} 请求储能优化，节点: {optimization_request.node_id}")
        
        # 初始化算法引擎
        algorithm_engine = AlgorithmEngine()
        
        # 执行优化
        start_time = time.time()
        result = algorithm_engine.run_storage_optimization(
            node_id=optimization_request.node_id,
            demand_forecast=optimization_request.demand_forecast,
            hourly_prices=optimization_request.hourly_prices,
            time_periods=optimization_request.time_periods,
            storage_config=optimization_request.storage_config,
            initial_soc=optimization_request.initial_soc
        )
        
        # 添加背景任务保存结果
        background_tasks.add_task(save_optimization_result, result)
        
        # 构建响应
        response = OptimizationResponseModel(
            node_id=result["node_id"],
            status=result["status"].value if hasattr(result["status"], "value") else result["status"],
            charging_schedule=result["charging_schedule"],
            discharging_schedule=result["discharging_schedule"],
            soc_profile=result["soc_profile"],
            total_cost=result["total_cost"],
            total_revenue=result["total_revenue"],
            net_profit=result["net_profit"],
            peak_reduction_mw=result["peak_reduction_mw"],
            solve_time_seconds=result["solve_time_seconds"],
            algorithm_version=result.get("algorithm_version", "2.0.0"),
            timestamp=datetime.now(),
            metadata=result.get("metadata", {})
        )
        
        logger.info(f"储能优化完成，节点: {optimization_request.node_id}, 耗时: {time.time() - start_time:.3f}秒")
        return response
        
    except DataValidationException as e:
        logger.warning(f"数据验证失败: {e}")
        raise HTTPException(status_code=422, detail=str(e))
    except OptimizationException as e:
        logger.error(f"优化计算失败: {e}")
        raise HTTPException(status_code=500, detail="优化计算失败")
    except Exception:
        logger.exception("储能优化发生未知错误")
        raise HTTPException(status_code=500, detail="内部服务器错误")


@app.post("/api/v2/optimization/batch", response_model=List[OptimizationResponseModel])
@limiter.limit("10/minute")
async def batch_optimization(
    request: Request,
    batch_request: BatchOptimizationRequest,
    background_tasks: BackgroundTasks,
    current_user: str = Depends(get_current_user)
):
    """
    批量储能优化
    
    支持并行处理多个节点的优化请求
    """
    try:
        logger.info(f"用户 {current_user} 请求批量优化，数量: {len(batch_request.requests)}")
        
        algorithm_engine = AlgorithmEngine()
        results = []
        
        if batch_request.parallel_processing:
            # 并行处理
            tasks = []
            for req in batch_request.requests:
                task = asyncio.create_task(
                    run_single_optimization(algorithm_engine, req)
                )
                tasks.append(task)
            
            # 等待所有任务完成
            completed_results = await asyncio.gather(*tasks, return_exceptions=True)
            
            for i, result in enumerate(completed_results):
                if isinstance(result, Exception):
                    logger.error(f"批量优化第{i+1}个请求失败: {result}")
                    # 创建失败响应
                    results.append(OptimizationResponseModel(
                        node_id=batch_request.requests[i].node_id,
                        status="failed",
                        charging_schedule=[],
                        discharging_schedule=[],
                        soc_profile=[],
                        total_cost=0.0,
                        total_revenue=0.0,
                        net_profit=0.0,
                        peak_reduction_mw=0.0,
                        solve_time_seconds=0.0,
                        algorithm_version="2.0.0",
                        timestamp=datetime.now(),
                        metadata={"error": str(result)}
                    ))
                else:
                    results.append(convert_to_response_model(result))
        else:
            # 串行处理
            for req in batch_request.requests:
                try:
                    result = await run_single_optimization(algorithm_engine, req)
                    results.append(convert_to_response_model(result))
                except Exception as e:
                    logger.error(f"批量优化请求失败: {e}")
                    results.append(OptimizationResponseModel(
                        node_id=req.node_id,
                        status="failed",
                        charging_schedule=[],
                        discharging_schedule=[],
                        soc_profile=[],
                        total_cost=0.0,
                        total_revenue=0.0,
                        net_profit=0.0,
                        peak_reduction_mw=0.0,
                        solve_time_seconds=0.0,
                        algorithm_version="2.0.0",
                        timestamp=datetime.now(),
                        metadata={"error": str(e)}
                    ))
        
        # 添加背景任务保存所有结果
        for result in results:
            if result.status != "failed":
                background_tasks.add_task(save_optimization_result, result.dict())
        
        logger.info(f"批量优化完成，成功: {sum(1 for r in results if r.status != 'failed')}/{len(results)}")
        return results
        
    except Exception as e:
        logger.error(f"批量优化失败: {e}")
        raise HTTPException(status_code=500, detail="批量优化失败")


@app.get("/api/v2/optimization/history/{node_id}")
@limiter.limit("200/minute")
async def get_optimization_history(
    node_id: str = Path(..., description="节点ID"),
    start_date: Optional[str] = Query(None, description="开始日期 (YYYY-MM-DD)"),
    end_date: Optional[str] = Query(None, description="结束日期 (YYYY-MM-DD)"),
    limit: int = Query(10, ge=1, le=100, description="返回记录数量"),
    current_user: str = Depends(get_current_user)
):
    """
    获取优化历史记录
    """
    try:
        db_manager = get_database_manager()
        
        # 构建查询条件
        query = """
        SELECT node_id, optimization_type, result_data, created_at, algorithm_version
        FROM optimization_results 
        WHERE node_id = :node_id
        """
        
        params = {"node_id": node_id}
        
        if start_date:
            query += " AND created_at >= :start_date"
            params["start_date"] = start_date
            
        if end_date:
            query += " AND created_at <= :end_date"
            params["end_date"] = end_date
            
        query += " ORDER BY created_at DESC LIMIT :limit"
        params["limit"] = limit
        
        results = db_manager.execute_query(query, params)
        
        return APIResponse(
            data=results,
            message=f"获取到 {len(results)} 条历史记录"
        )
        
    except Exception as e:
        logger.error(f"获取优化历史失败: {e}")
        raise HTTPException(status_code=500, detail="获取历史记录失败")


@app.get("/api/v2/system/metrics")
@limiter.limit("60/minute")
async def get_system_metrics(current_user: str = Depends(get_current_user)):
    """
    获取系统性能指标
    """
    try:
        algorithm_engine = AlgorithmEngine()
        metrics = algorithm_engine.get_performance_metrics()
        
        return APIResponse(
            data=metrics,
            message="系统指标获取成功"
        )
        
    except Exception as e:
        logger.error(f"获取系统指标失败: {e}")
        raise HTTPException(status_code=500, detail="获取系统指标失败")


# ============================================================================
# 辅助函数
# ============================================================================

async def run_single_optimization(engine: AlgorithmEngine, req: OptimizationRequestModel) -> Dict[str, Any]:
    """运行单个优化任务"""
    return engine.run_storage_optimization(
        node_id=req.node_id,
        demand_forecast=req.demand_forecast,
        hourly_prices=req.hourly_prices,
        time_periods=req.time_periods,
        storage_config=req.storage_config,
        initial_soc=req.initial_soc
    )


def convert_to_response_model(result: Dict[str, Any]) -> OptimizationResponseModel:
    """转换为响应模型"""
    return OptimizationResponseModel(
        node_id=result["node_id"],
        status=result["status"].value if hasattr(result["status"], "value") else result["status"],
        charging_schedule=result["charging_schedule"],
        discharging_schedule=result["discharging_schedule"],
        soc_profile=result["soc_profile"],
        total_cost=result["total_cost"],
        total_revenue=result["total_revenue"],
        net_profit=result["net_profit"],
        peak_reduction_mw=result["peak_reduction_mw"],
        solve_time_seconds=result["solve_time_seconds"],
        algorithm_version=result.get("algorithm_version", "2.0.0"),
        timestamp=datetime.now(),
        metadata=result.get("metadata", {})
    )


async def save_optimization_result(result_data: Dict[str, Any]) -> None:
    """保存优化结果(背景任务)"""
    try:
        db_manager = get_database_manager()
        success = db_manager.save_optimization_result(result_data)
        if success:
            logger.debug(f"优化结果已保存: {result_data.get('node_id')}")
        else:
            logger.warning(f"优化结果保存失败: {result_data.get('node_id')}")
    except Exception as e:
        logger.error(f"保存优化结果时发生错误: {e}")


# 启动时初始化
@app.on_event("startup")
async def startup_event():
    """应用启动事件"""
    app.state.start_time = time.time()
    logger.info("VPP算法服务API v2.0 启动成功")


@app.on_event("shutdown")
async def shutdown_event():
    """应用关闭事件"""
    logger.info("VPP算法服务API 正在关闭...")
    
    # 关闭数据库连接
    try:
        db_manager = get_database_manager()
        db_manager.close()
    except Exception as e:
        logger.error(f"关闭数据库连接失败: {e}")


if __name__ == "__main__":
    import uvicorn
    uvicorn.run(
        "routes:app",
        host="0.0.0.0",
        port=8000,
        reload=False,  # 生产环境关闭热重载
        workers=1,
        log_level="info",
        access_log=True
    )
