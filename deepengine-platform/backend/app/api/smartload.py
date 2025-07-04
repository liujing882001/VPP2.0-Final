"""
SmartLoad智慧用能管理API
"""

from fastapi import APIRouter
import random
import datetime

router = APIRouter()

@router.get("/dashboard")
async def get_smartload_dashboard():
    """获取SmartLoad仪表盘数据"""
    return {
        "status": "success",
        "data": {
            "current_load": {
                "total": round(random.uniform(800, 1200), 1),
                "unit": "kW",
                "change": f"{random.uniform(-10, 10):+.1f}%"
            },
            "load_forecast": {
                "peak_hour": "14:00",
                "peak_load": round(random.uniform(1000, 1500), 1),
                "confidence": round(random.uniform(85, 95), 1)
            },
            "demand_response": {
                "active_programs": random.randint(3, 8),
                "potential_reduction": round(random.uniform(15, 30), 1),
                "unit": "%"
            }
        },
        "timestamp": datetime.datetime.now().isoformat()
    }

@router.get("/devices")
async def get_load_devices():
    """获取负荷设备列表"""
    return {
        "status": "success", 
        "message": "SmartLoad设备管理功能开发中",
        "data": []
    } 