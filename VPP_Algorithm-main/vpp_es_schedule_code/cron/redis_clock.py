import asyncio
import functools
import os

from redlock import RedLock, RedLockError

from common.log_util import get_logger

logger  =  get_logger(__name__)

connection_details  =  [
    {
        "host": "localhost",
        "port": 6379,
        "db": 0,
    }
]


def lock(key):
    """
    redis分布式锁，基于redlock
    :param key: 唯一key，确保所有任务一致，但不与其他任务冲突
    :return:
    """

    def decorator(func):
        if asyncio.iscoroutinefunction(func):
            logger.info(f"协程{func.__name__}执行")

            @functools.wraps(func)
            async def wrapper(*args,  **kwargs):
                try:
                    with RedLock(
                        f"distributed_lock:{func.__name__}:{key}:{str(args)}",
                        connection_details  =  connection_details,
                        ttl  =  30000,   # 锁释放时间为30s
                    ):
                        return await func(*args,  **kwargs)
                except RedLockError:

        else:
            logger.info(f"非协程{func.__name__}执行")

            @functools.wraps(func)
            def wrapper(*args,  **kwargs):
                try:
                    lock_key  =  f"distributed_lock:{func.__name__}:{key}:{str(args)}"
                    logger.info(f"Trying to acquire lock for key: {lock_key}")
                    with RedLock(
                        f"distributed_lock:{func.__name__}:{key}:{str(args)}",
                        connection_details  =  connection_details,
                        ttl  =  30000,   # 锁释放时间为30s
                    ):
                        logger.info(f"Lock acquired for key: {lock_key}")
                        return func(*args,  **kwargs)
                except RedLockError:
                    logger.error(f"Failed to acquire lock for key: {lock_key}")

        return wrapper

    return decorator
