import datetime

from apscheduler.events import EVENT_JOB_EXECUTED, JobExecutionEvent
from apscheduler.executors.pool import ProcessPoolExecutor
from apscheduler.schedulers.asyncio import AsyncIOScheduler

from common.log_util import get_logger

logger = get_logger(__name__)

executors = {
    "default": {"type": "threadpool", "max_workers": 20},  # 最大工作线程数20
    "processpool": ProcessPoolExecutor(
        max_workers=1
    ),  # 最大工作进程数为1, 防止出现多进程多次执行定时任务
}
job_defaults = {
    "coalesce": False,  # 关闭新job的合并，当job延误或者异常原因未执行时
    "max_instances": 1,  # 并发运行新job默认最大实例多少
    "misfire_grace_time": 60,  # 60秒的容错时间
}


def job_listener(event: JobExecutionEvent):
    if event.exception:
        # 处理任务执行失败的情况
        logger.error(f"Job {event.job_id} failed with exception {event.exception}")
    else:
        # 处理任务成功执行的情况
        logger.info(
            f"Job {event.job_id} executed successfully at {datetime.datetime.now()}"
        )


async_io_scheduler = AsyncIOScheduler(executors=executors, job_defaults=job_defaults)
async_io_scheduler.add_listener(callback=job_listener, mask=EVENT_JOB_EXECUTED)
