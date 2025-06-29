import datetime

from apscheduler.triggers.cron import CronTrigger
from pytz import timezone

from common.log_util import get_logger

logger = get_logger(__name__)

from config import global_config
from config.parser import ConfigParser
from cron.schedulers import async_io_scheduler as scheduler
from input.loader import input_loader
from model.loader import model_loader
from output.loader import output_loader, output_process


class SchedulerManager:
    def __init__(self):
        pass

    def init_jobs(self):
        # 项目配置文件
        index = 0
        for project, project_config in global_config.items():
            # 模型配置文件
            for model, model_configs in project_config.items():
                # 节点配置文件
                for node, model_config in model_configs.items():
                    index += 1
                    self.add_job(
                        project=project,
                        model=model,
                        node=node,
                        model_cfgs=model_config,
                        index=index,
                    )

    def run_job(self, project, model, node, model_config):
        model_instance = model_loader(
            project=project, model=model, node=node, model_cfg=model_config
        )
        model_config = ConfigParser(project, node, model, model_config).parse_config()
        input_data = input_loader(
            project=project, node=node, model=model, model_cfg=model_config
        )
        output_func_dict = output_loader(
            project=project, node=node, model=model, model_cfg=model_config
        )
        result = model_instance.run(
            input_data=input_data, model_cfgs=model_config["model"]
        )
        for name, output_func in output_func_dict.items():
            if output_func:
                output_func(model_config["model"], result[name])
            output_process(project, node, model, result, output_func_dict, model_config)

    def add_job(self, project, model, node, model_cfgs, index):
        cron = model_cfgs["cron"]
        tz = timezone(cron["timezone"])
        id = f"{project}_{model}_{node}_{'_'.join(cron['schedule'].split())}"
        kwargs = {
            "project": project,
            "model": model,
            "node": node,
            "model_config": model_cfgs,
        }
        # 如果immediate为True,  则立即执行
        if cron.get("immediate"):
            next_run_time = datetime.datetime.now(tz=tz) + datetime.timedelta(
                seconds=10 * index
            )
            scheduler.add_job(
                func=self.run_job,
                trigger=CronTrigger.from_crontab(cron["schedule"], timezone=tz),
                id=id,
                kwargs=kwargs,
                next_run_time=next_run_time,
            )
        else:
            scheduler.add_job(
                func=self.run_job,
                trigger=CronTrigger.from_crontab(cron["schedule"], timezone=tz),
                id=id,
                kwargs=kwargs,
            )

        logger.info(f"Add job {project}_{model}_{node} with cron {cron}")


schedule_manager = SchedulerManager()


if __name__ == "__main__":
    date_format = "%Y-%m-%d  %H:%M:%S"
    future_time = datetime.datetime.strptime("2025  - 02  -  25  00:00:00", date_format)
    # 项目配置文件
    for project, project_config in global_config.items():
        # 模型配置文件
        for model, model_configs in project_config.items():
            # 节点配置文件
            for node, model_config in model_configs.items():
                now_time = datetime.datetime.strptime(
                    model_config["model"]["time_range"]["now_time"], date_format
                )
                while 1:
                    now_time = now_time + datetime.timedelta(days=1)
                    logger.info(f"当前时间: {now_time}")
                    # 如果now_time > future_time,  则不执行
                    if now_time > future_time:
                        logger.info(
                            f"当前时间: {now_time} > 未来时间: {future_time},  不执行"
                        )
                        break
                    model_config["model"]["time_range"]["now_time"] = now_time.strftime(
                        date_format
                    )
                    schedule_manager.run_job(
                        project=project,
                        model=model,
                        node=node,
                        model_config=model_config,
                    )
