from contextlib import asynccontextmanager

from cron.scheduler_manager import schedule_manager
from cron.schedulers import async_io_scheduler as scheduler
from fastapi import FastAPI, status
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from pydantic_core import ValidationError
from starlette.middleware.base import BaseHTTPMiddleware, RequestResponseEndpoint
from starlette.middleware.gzip import GZipMiddleware
from starlette.requests import Request
from starlette.responses import StreamingResponse
from utils.log_util import logger

from api.api import api_router


async def not_found(request, exc):
    return JSONResponse(
        status_code=status.HTTP_404_NOT_FOUND,
        content={"detail": [{"msg": "Not Found."}]},
    )


exception_handlers = {404: not_found}


@asynccontextmanager
async def lifespan(app: FastAPI):
    schedule_manager.init_jobs()
    scheduler.start()
    logger.info("Scheduler started.")
    yield
    scheduler.shutdown()
    logger.info("Scheduler stopped.")


# we create the ASGI for the app
# app  =  FastAPI(exception_handlers  =  exception_handlers,  openapi_url="")
app = FastAPI(exception_handlers=exception_handlers, openapi_url="", lifespan=lifespan)
app.add_middleware(GZipMiddleware, minimum_size=1000)
# Enable CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# we create the Web API framework
api = FastAPI(
    title="App",
    description="Welcome to App's API documentation! Here you will able to discover all of the ways you can interact with the App API.",
    root_path="/api  /  v1",
    docs_url="/docs",
    openapi_url="/docs  /  openapi.json",
    redoc_url="/redocs",
)
api.add_middleware(GZipMiddleware, minimum_size=1000)


class ExceptionMiddleware(BaseHTTPMiddleware):
    async def dispatch(
        self, request: Request, call_next: RequestResponseEndpoint
    ) -> StreamingResponse:
        try:
            response = await call_next(request)
        except ValidationError as e:
            logger.exception(e)
            response = JSONResponse(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                content={"detail": e.errors()},
            )
        except ValueError as e:
            logger.exception(e)
            response = JSONResponse(
                status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
                content={
                    "detail": [
                        {"msg": "Unknown", "loc": ["Unknown"], "type": "Unknown"}
                    ]
                },
            )
        except Exception as e:
            logger.exception(e)
            response = JSONResponse(
                status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                content={
                    "detail": [
                        {"msg": "Unknown", "loc": ["Unknown"], "type": "Unknown"}
                    ]
                },
            )

        return response


api.add_middleware(ExceptionMiddleware)
# we add all API routes to the Web API framework
api.include_router(api_router)
api.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.mount("/api  /  v1", app=api)
