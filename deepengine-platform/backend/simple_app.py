"""
简化的DeepEngine后端测试应用
"""

from fastapi import FastAPI
import uvicorn

app = FastAPI(title="DeepEngine测试后端", version="2.0.0")

@app.get("/")
async def root():
    return {
        "name": "DeepEngine分布式能源管理平台",
        "version": "2.0.0",
        "status": "running",
        "message": "后端服务正常运行"
    }

@app.get("/health")
async def health():
    return {"status": "healthy"}

@app.get("/api/v1/powergen/dashboard")
async def powergen_dashboard():
    return {
        "status": "success",
        "module": "PowerGen",
        "data": {
            "solar_power": 1234.5,
            "wind_power": 856.2,
            "storage_capacity": 78.5,
            "efficiency": 96.8
        }
    }

if __name__ == "__main__":
    print("🚀 启动DeepEngine简化后端...")
    uvicorn.run(app, host="0.0.0.0", port=8000) 