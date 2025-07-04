"""
API路由模块
"""

from fastapi import APIRouter
from .powergen import router as powergen_router
from .smartload import router as smartload_router
from .vppcloud import router as vppcloud_router

# 创建主API路由器
api_router = APIRouter()

# 添加配置接口
@api_router.get("/config", tags=["Config"])
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

# 注册子模块路由
api_router.include_router(powergen_router, prefix="/powergen", tags=["PowerGen"])
api_router.include_router(smartload_router, prefix="/smartload", tags=["SmartLoad"])
api_router.include_router(vppcloud_router, prefix="/vppcloud", tags=["VPPCloud"]) 