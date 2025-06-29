"""
VPP储能调度算法服务
提供储能设备调度优化算法接口

@author: VPP Team
@version: 2.0.0
@date: 2024  - 01  - 01
"""

import time
import traceback
from contextlib import asynccontextmanager
from typing import Any, Dict, Optional

import uvicorn
from config.parser import load_config
from fastapi import Depends, FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.middleware.gzip import GZipMiddleware
from fastapi.responses import JSONResponse
from input.loader import InputLoader
from model.loader import ModelLoader
from output.loader import OutputLoader
from prometheus_fastapi_instrumentator import Instrumentator
from pydantic import BaseModel, Field, validator

from common.exceptions import VppAlgorithmException
from common.log_util import setup_logger

# 设置日志
logger = setup_logger(__name__)

# 全局变量
model_loader: Optional[ModelLoader] = None
input_loader: Optional[InputLoader] = None
output_loader: Optional[OutputLoader] = None
app_config: Optional[Dict] = None


# 请求模型
class OptimizationRequest(BaseModel):
    """优化请求模型"""

    node_id: str = Field(..., description="节点ID", min_length=1, max_length=50)
    start_time: str = Field(
        ..., description="开始时间", regex=r'\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}'
    )
    end_time: str = Field(
        ..., description="结束时间", regex=r'\d{4}-\d{2}-\d{2} \d{2}:\d{2}:\d{2}'
    )
    strategy_type: str = Field("default", description="策略类型")
    parameters: Optional[Dict[str, Any]] = Field(
        default_factory=dict, description="算法参数"
    )

    @validator('end_time')
    def validate_time_range(cls, v, values):
        """验证时间范围"""
        if 'start_time' in values and v <= values['start_time']:
            raise ValueError('结束时间必须大于开始时间')
        return v


class BackCalcRequest(BaseModel):
    """回算请求模型"""

    node_id: str = Field(..., description="节点ID", min_length=1, max_length=50)
    calc_date: str = Field(..., description="计算日期", regex=r'\d{4}-\d{2}-\d{2}')
    data_type: str = Field("actual", description="数据类型")
    parameters: Optional[Dict[str, Any]] = Field(
        default_factory=dict, description="回算参数"
    )


class HealthResponse(BaseModel):
    """健康检查响应模型"""

    status: str
    service: str
    version: str
    timestamp: str
    uptime: float
    dependencies: Dict[str, str]


class OptimizationResponse(BaseModel):
    """优化响应模型"""

    status: str
    message: str
    node_id: str
    optimization_id: str
    result: Dict[str, Any]
    execution_time: float


# 启动和关闭事件处理
@asynccontextmanager
async def lifespan(app: FastAPI):
    """应用生命周期管理"""
    # 启动时初始化
    await startup_event()
    yield
    # 关闭时清理
    await shutdown_event()


async def startup_event():
    """应用启动时的初始化操作"""
    global model_loader, input_loader, output_loader, app_config

    try:
        logger.info("🚀 Starting VPP Algorithm Service...")

        # 加载配置
        app_config = load_config()
        logger.info("✅ Configuration loaded successfully")

        # 初始化组件
        model_loader = ModelLoader(app_config)
        input_loader = InputLoader(app_config)
        output_loader = OutputLoader(app_config)

        # 预加载模型
        await model_loader.preload_models()
        logger.info("✅ Models preloaded successfully")

        # 测试数据库连接
        await input_loader.test_connection()
        logger.info("✅ Database connection verified")

        logger.info("🎉 VPP Algorithm Service started successfully")

    except Exception as e:
        logger.error(f"❌ Failed to start service: {str(e)}")
        logger.error(traceback.format_exc())
        raise


async def shutdown_event():
    """应用关闭时的清理操作"""
    global model_loader, input_loader, output_loader

    try:
        logger.info("🛑 Shutting down VPP Algorithm Service...")

        # 清理资源
        if model_loader:
            await model_loader.cleanup()
        if input_loader:
            await input_loader.cleanup()
        if output_loader:
            await output_loader.cleanup()

        logger.info("✅ Service shutdown completed")

    except Exception as e:
        logger.error(f"❌ Error during shutdown: {str(e)}")


# 创建FastAPI应用
app = FastAPI(
    title="VPP储能调度算法服务",
    description="提供虚拟电厂储能设备调度优化算法接口",
    version="2.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
    openapi_url="/openapi.json",
    lifespan=lifespan,
)

# 添加中间件
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # 生产环境应该限制具体域名
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.add_middleware(GZipMiddleware, minimum_size=1000)

# 添加Prometheus监控
instrumentator = Instrumentator()
instrumentator.instrument(app).expose(app)

# 全局变量：服务启动时间
start_time = time.time()


# 中间件：请求日志
@app.middleware("http")
async def log_requests(request: Request, call_next):
    """记录请求日志"""
    start_time = time.time()

    # 记录请求信息
    logger.info(
        f"📥 {request.method} {request.url.path}  -  Client: {request.client.host}"
    )

    try:
        response = await call_next(request)
        process_time = time.time() - start_time

        # 记录响应信息
        logger.info(
            f"📤 {request.method} {request.url.path}  -  {response.status_code}  -  {process_time:.3f}s"
        )

        # 添加响应头
        response.headers["X  -  Process  -  Time"] = str(process_time)
        response.headers["X  -  Service  -  Version"] = "2.0.0"

        return response

    except Exception as e:
        process_time = time.time() - start_time
        logger.error(
            f"❌ {request.method} {request.url.path}  -  Error: {str(e)}  -  {process_time:.3f}s"
        )
        raise


# 全局异常处理器
@app.exception_handler(VppAlgorithmException)
async def algorithm_exception_handler(request: Request, exc: VppAlgorithmException):
    """处理算法异常"""
    logger.error(f"Algorithm error: {exc.message}  -  Code: {exc.code}")
    return JSONResponse(
        status_code=400,
        content={
            "status": "error",
            "message": exc.message,
            "code": exc.code,
            "timestamp": time.time(),
        },
    )


@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    """处理HTTP异常"""
    logger.error(f"HTTP error: {exc.detail}  -  Status: {exc.status_code}")
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "status": "error",
            "message": exc.detail,
            "code": exc.status_code,
            "timestamp": time.time(),
        },
    )


@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    """处理通用异常"""
    error_msg = f"Internal server error: {str(exc)}"
    logger.error(error_msg)
    logger.error(traceback.format_exc())

    return JSONResponse(
        status_code=500,
        content={
            "status": "error",
            "message": "服务内部错误",
            "code": 500,
            "timestamp": time.time(),
        },
    )


# 依赖注入
async def get_model_loader() -> ModelLoader:
    """获取模型加载器"""
    if model_loader is None:
        raise HTTPException(status_code=503, detail="服务未就绪")
    return model_loader


async def get_input_loader() -> InputLoader:
    """获取输入加载器"""
    if input_loader is None:
        raise HTTPException(status_code=503, detail="服务未就绪")
    return input_loader


async def get_output_loader() -> OutputLoader:
    """获取输出加载器"""
    if output_loader is None:
        raise HTTPException(status_code=503, detail="服务未就绪")
    return output_loader


# API路由
@app.get("/health", response_model=HealthResponse)
async def health_check():
    """健康检查接口"""
    global start_time

    dependencies = {}

    # 检查各组件状态
    try:
        if model_loader:
            dependencies["model_loader"] = "healthy"
        if input_loader:
            dependencies["database"] = (
                "healthy" if await input_loader.test_connection() else "unhealthy"
            )
        if output_loader:
            dependencies["output_loader"] = "healthy"
    except Exception as e:
        logger.error(f"Health check failed: {str(e)}")
        dependencies["error"] = str(e)

    return HealthResponse(
        status="healthy",
        service="VPP储能调度算法服务",
        version="2.0.0",
        timestamp=str(time.time()),
        uptime=time.time() - start_time,
        dependencies=dependencies,
    )


@app.post("/optimization", response_model=OptimizationResponse)
async def optimization_endpoint(
    request: OptimizationRequest,
    model_loader: ModelLoader = Depends(get_model_loader),
    input_loader: InputLoader = Depends(get_input_loader),
    output_loader: OutputLoader = Depends(get_output_loader),
):
    """储能优化算法接口"""
    start_time = time.time()
    optimization_id = f"opt_{int(start_time)}_{request.node_id}"

    try:
        logger.info(f"🔄 Starting optimization for node: {request.node_id}")

        # 1. 加载输入数据
        input_data = await input_loader.load_data(
            node_id=request.node_id,
            start_time=request.start_time,
            end_time=request.end_time,
        )

        # 2. 选择并运行模型
        model = await model_loader.get_model(request.strategy_type)
        result = await model.optimize(input_data, request.parameters)

        # 3. 保存结果
        await output_loader.save_result(optimization_id, result)

        execution_time = time.time() - start_time
        logger.info(
            f"✅ Optimization completed for node: {request.node_id} in {execution_time:.3f}s"
        )

        return OptimizationResponse(
            status="success",
            message="优化完成",
            node_id=request.node_id,
            optimization_id=optimization_id,
            result=result,
            execution_time=execution_time,
        )

    except Exception as e:
        execution_time = time.time() - start_time
        error_msg = f"优化失败: {str(e)}"
        logger.error(f"❌ {error_msg}  -  Execution time: {execution_time:.3f}s")
        raise HTTPException(status_code=500, detail=error_msg)


@app.post("/back_calc")
async def back_calc_endpoint(
    request: BackCalcRequest,
    model_loader: ModelLoader = Depends(get_model_loader),
    input_loader: InputLoader = Depends(get_input_loader),
    output_loader: OutputLoader = Depends(get_output_loader),
):
    """回算接口"""
    start_time = time.time()
    calc_id = f"calc_{int(start_time)}_{request.node_id}"

    try:
        logger.info(f"🔄 Starting back calculation for node: {request.node_id}")

        # 1. 加载历史数据
        historical_data = await input_loader.load_historical_data(
            node_id=request.node_id,
            calc_date=request.calc_date,
            data_type=request.data_type,
        )

        # 2. 执行回算
        calc_model = await model_loader.get_calc_model()
        result = await calc_model.calculate(historical_data, request.parameters)

        # 3. 保存回算结果
        await output_loader.save_calc_result(calc_id, result)

        execution_time = time.time() - start_time
        logger.info(
            f"✅ Back calculation completed for node: {request.node_id} in {execution_time:.3f}s"
        )

        return {
            "status": "success",
            "message": "回算完成",
            "node_id": request.node_id,
            "calc_id": calc_id,
            "result": result,
            "execution_time": execution_time,
        }

    except Exception as e:
        execution_time = time.time() - start_time
        error_msg = f"回算失败: {str(e)}"
        logger.error(f"❌ {error_msg}  -  Execution time: {execution_time:.3f}s")
        raise HTTPException(status_code=500, detail=error_msg)


@app.get("/models")
async def list_models(model_loader: ModelLoader = Depends(get_model_loader)):
    """获取可用模型列表"""
    try:
        models = await model_loader.list_available_models()
        return {"status": "success", "models": models}
    except Exception as e:
        logger.error(f"Failed to list models: {str(e)}")
        raise HTTPException(status_code=500, detail="获取模型列表失败")


@app.get("/nodes/{node_id}/status")
async def get_node_status(
    node_id: str, input_loader: InputLoader = Depends(get_input_loader)
):
    """获取节点状态"""
    try:
        status = await input_loader.get_node_status(node_id)
        return {"status": "success", "node_id": node_id, "data": status}
    except Exception as e:
        logger.error(f"Failed to get node status: {str(e)}")
        raise HTTPException(status_code=500, detail="获取节点状态失败")


if __name__ == "__main__":
    uvicorn.run(
        "service:app",
        host="0.0.0.0",
        port=8000,
        reload=False,
        workers=1,
        log_level="info",
        access_log=True,
    )
