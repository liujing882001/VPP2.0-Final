"""
DeepEngine分布式能源管理平台 - FastAPI应用主入口

AI原生开发，提供高性能的分布式能源管理API服务
"""

from fastapi import FastAPI, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.trustedhost import TrustedHostMiddleware
from fastapi.responses import JSONResponse
import uvicorn
import logging
import time
from prometheus_client import Counter, Histogram, generate_latest
from prometheus_client import CONTENT_TYPE_LATEST

from app.core.config import get_settings
from app.core.logging import setup_logging
from app.api import api_router

# 设置日志
setup_logging()
logger = logging.getLogger(__name__)

# Prometheus监控指标
REQUEST_COUNT = Counter('deepengine_requests_total', 'Total requests', ['method', 'endpoint', 'status'])
REQUEST_DURATION = Histogram('deepengine_request_duration_seconds', 'Request duration')

# 获取配置
settings = get_settings()

# 创建FastAPI应用
app = FastAPI(
    title="DeepEngine分布式能源管理平台",
    description="""
    🌟 **AI原生的分布式能源管理平台**
    
    ## 核心功能模块
    
    ### 🔋 PowerGen - 智能发电管理
    - AI功率预测 (LSTM + Transformer融合模型)
    - 储能优化策略
    - 发电收益分析
    - 设备智能运维
    
    ### 🏠 SmartLoad - 智慧用能管理  
    - 负荷监控与预测
    - 需求响应优化
    - 用电成本分析
    - 智能调度策略
    
    ### ⚡ VPPCloud - 虚拟电厂平台
    - 分布式资源聚合
    - 电力市场交易
    - 实时调度优化
    - 收益分配管理
    
    ## 技术特色
    - 🤖 90%代码AI生成，开发效率提升5-10倍
    - 🎯 预测精度MAPE < 3%，API响应 < 2秒
    - 🔒 企业级安全，99.95%可用性保障
    - 📊 支持10万+设备并发，云原生架构
    
    """,
    version="2.0.0",
    contact={
        "name": "DeepEngine Team",
        "email": "tech@deepengine.com",
        "url": "https://github.com/deepengine/vpp-2.0-platform"
    },
    license_info={
        "name": "MIT License",
        "url": "https://opensource.org/licenses/MIT"
    },
    docs_url="/docs" if settings.DEBUG else None,
    redoc_url="/redoc" if settings.DEBUG else None,
    openapi_url="/openapi.json" if settings.DEBUG else None
)

# 中间件配置
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ALLOWED_HOSTS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.add_middleware(
    TrustedHostMiddleware,
    allowed_hosts=settings.ALLOWED_HOSTS
)

@app.middleware("http")
async def add_process_time_header(request: Request, call_next):
    """添加请求处理时间和监控"""
    start_time = time.time()
    
    # 处理请求
    response = await call_next(request)
    
    # 计算处理时间
    process_time = time.time() - start_time
    response.headers["X-Process-Time"] = str(process_time)
    
    # 更新Prometheus指标
    REQUEST_COUNT.labels(
        method=request.method,
        endpoint=request.url.path,
        status=response.status_code
    ).inc()
    REQUEST_DURATION.observe(process_time)
    
    return response

@app.exception_handler(Exception)
async def global_exception_handler(request: Request, exc: Exception):
    """全局异常处理器"""
    logger.error(f"Global exception: {exc}", exc_info=True)
    return JSONResponse(
        status_code=500,
        content={
            "error": "Internal server error",
            "message": "An unexpected error occurred",
            "request_id": getattr(request.state, 'request_id', 'unknown')
        }
    )

# 配置接口 - 在API路由器之前注册以避免冲突
@app.get("/api/config", tags=["Config"])
async def get_api_config():
    """获取前端配置"""
    return {
        "api_base_url": "http://127.0.0.1:8000",
        "version": "2.0.0",
        "modules": {
            "powergen": {"enabled": True, "endpoint": "/api/v1/powergen"},
            "smartload": {"enabled": True, "endpoint": "/api/v1/smartload"},
            "vppcloud": {"enabled": True, "endpoint": "/api/v1/vppcloud"}
        },
        "features": {
            "real_time_monitoring": True,
            "ai_prediction": True,
            "optimization": True,
            "analytics": True
        }
    }

# 路由注册
app.include_router(api_router, prefix="/api/v1")

@app.get("/", tags=["Root"])
async def root():
    """API根路径 - 返回系统状态"""
    return {
        "name": "DeepEngine分布式能源管理平台",
        "version": "2.0.0",
        "description": "AI原生的分布式能源管理平台",
        "status": "running",
        "features": {
            "powergen": "智能发电管理",
            "smartload": "智慧用能管理", 
            "vppcloud": "虚拟电厂平台"
        },
        "tech_stack": {
            "backend": "FastAPI + Python",
            "frontend": "React + TypeScript",
            "ai": "PyTorch + Transformers",
            "database": "PostgreSQL + Redis + InfluxDB"
        },
        "docs_url": "/docs" if settings.DEBUG else None
    }

@app.get("/health", tags=["Health"])
async def health_check():
    """健康检查接口"""
    return {
        "status": "healthy",
        "timestamp": time.time(),
        "version": "2.0.0",
        "environment": settings.ENVIRONMENT
    }

@app.get("/config", tags=["Config"])
async def get_config():
    """获取前端配置"""
    return {
        "api_base_url": "http://127.0.0.1:8000",
        "version": "2.0.0",
        "modules": {
            "powergen": {"enabled": True, "endpoint": "/api/v1/powergen"},
            "smartload": {"enabled": True, "endpoint": "/api/v1/smartload"},
            "vppcloud": {"enabled": True, "endpoint": "/api/v1/vppcloud"}
        },
        "features": {
            "real_time_monitoring": True,
            "ai_prediction": True,
            "optimization": True,
            "analytics": True
        }
    }



@app.get("/metrics", tags=["Monitoring"])
async def metrics():
    """Prometheus监控指标"""
    from fastapi import Response
    return Response(
        generate_latest(),
        media_type=CONTENT_TYPE_LATEST
    )

if __name__ == "__main__":
    # 开发环境直接运行
    uvicorn.run(
        "app.main:app",
        host="0.0.0.0",
        port=8000,
        reload=settings.DEBUG,
        log_level="debug" if settings.DEBUG else "info",
        workers=1 if settings.DEBUG else 4
    ) 