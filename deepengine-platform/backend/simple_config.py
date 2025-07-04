from fastapi import FastAPI
import uvicorn

app = FastAPI()

@app.get("/api/config")
async def get_config():
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

@app.get("/health")
async def health():
    return {"status": "healthy"}

if __name__ == "__main__":
    print("🚀 启动配置服务器...")
    uvicorn.run(app, host="127.0.0.1", port=8000, log_level="info")
