import datetime
from typing import Dict

from pytz import timezone

from common.log_util import get_logger

logger = get_logger(__name__)


class TimeRangeHandler:
    @classmethod
    def time_range_handler(cls, model_config: Dict) -> str:
        date_format = "%Y-%m-%d %H:%M:%S"
        time_zone = model_config["cron"]["timezone"]
        if model_config["model"]["time_range"].get("customize"):
            now_time = datetime.datetime.strptime(
                model_config["model"]["time_range"]["now_time"], date_format
            )
        else:
            # 获取当前时间
            now_time = datetime.datetime.now(tz=timezone(time_zone))

        logger.info(f"当前时间: {now_time}")

        # 将当前时间调整为15分钟的整点
        now_time = now_time.replace(
            tzinfo=timezone(time_zone),
            minute=(now_time.minute // 15) * 15,
            second=0,
            microsecond=0,
        )

        # 获取时间范围
        before_days = model_config["model"]["time_range"]["before_days"]
        after_days = model_config["model"]["time_range"]["after_days"]

        # 获取开始时间和结束时间
        start_time = (now_time + datetime.timedelta(days=before_days)).replace(
            hour=0, minute=0, second=0, microsecond=0
        )

        end_time = (now_time + datetime.timedelta(days=after_days)).replace(
            hour=23, minute=48, second=0, microsecond=0
        )

        # 获取当前月份
        effective_date = start_time.replace(day=1)

        # 更新时间范围
        model_config["model"]["time_range"]["now_time"] = now_time.strftime(date_format)
        model_config["model"]["time_range"]["start_time"] = start_time.strftime(
            date_format
        )
        model_config["model"]["time_range"]["end_time"] = end_time.strftime(date_format)
        model_config["model"]["time_range"]["effective_date"] = effective_date.strftime(
            date_format
        )
        model_config["model"]["time_range"]["month"] = effective_date.strftime("%Y-%m")


class ConfigParser:

    _handler = {
        "time_range": TimeRangeHandler.time_range_handler,
    }

    def __init__(self, project, node, model, config):
        self.project = project
        self.node = node
        self.model = model
        self.config = config

    def parse_config(self) -> Dict:
        self.parse_model_config()
        return self.config

    def parse_model_config(self) -> Dict:
        for key in self.config["model"].keys():
            if key in self._handler.keys():
                self._handler[key](self.config)


if __name__ == "__main__":
    from config import global_config

    for project, project_config in global_config.items():
        for model, model_configs in project_config.items():
            for node, model_config in model_configs.items():
                parser = ConfigParser(project, model, model_config)
