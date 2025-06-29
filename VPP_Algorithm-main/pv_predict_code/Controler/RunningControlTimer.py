import datetime
import time

import pandas as pd
from dateutil.tz import tzlocal
from log.Log import logger


class RunningControl:
    """
    该类为通过Class的方式实现定时任务，实现的方式为单线程利用sleep的方式
    """

    def __init__(self, interval_minute=30):
        self.interval_minute = interval_minute
        if interval_minute is not None:
            self.interval_minute = int(interval_minute)
        self.last_run_time = None
        self.next_run_time = None

        # 时钟
        self.clock = {}
        low = 0
        upper = self.interval_minute
        for i in range(0, 60):
            if i >= upper:
                low = upper
                upper = min(60, upper + self.interval_minute)
            self.clock[i] = low

    def adjust_datetime(self, timestamp: datetime.datetime):
        """
        在定时任务激活时，对时间向后周期取整
        例如: 时间:2019 -  5-5 19:21:38 周期 5分钟;
        输出: 2019 -  5-5 19:20:00
        :param timestamp:
        :return:
        """
        # 计算整分钟
        c_min = timestamp.minute
        return pd.to_datetime(
            timestamp.strftime("%Y-%m-%d %H:{}:00".format(self.clock[c_min]))
        ).tz_localize(tzlocal())

    def get_runtime(self) -> "datetime.datetime":
        current_time = datetime.datetime.now().astimezone(tzlocal())
        next_run_time = self.adjust_datetime(
            current_time + datetime.timedelta(minutes=self.interval_minute)
        )

        logger.info(
            "sleep @ {} ,  next run time:{}".format(
                datetime.datetime.now(), next_run_time
            )
        )
        _sec_to_sleep = (
            next_run_time - current_time
        ).total_seconds() + 2 * 60  # 2分钟为害怕数据延迟上传的缓冲时间
        logger.info("going to sleep {} seconds".format(_sec_to_sleep))
        time.sleep(_sec_to_sleep)
        logger.info("wakeup: %s", datetime.datetime.now())

        # check if next_run_time is equal to current_time,  print error,  if not.
        if next_run_time != self.adjust_datetime(datetime.datetime.now()):
            logger.info("The estimated run time is not equal to the current time.")

        # retreat interval time because get series add interval
        self.last_run_time = self.adjust_datetime(
            datetime.datetime.now()
        ) - datetime.timedelta(minutes=self.interval_minute)
        self.next_run_time = next_run_time
        return next_run_time


# if __name__ == "__main__":
#     rc  =  RunningControl(interval_minute  =  5)
#     st  =  datetime.datetime.now()

#     while st < datetime.datetime.now()  +  datetime.timedelta(hours  =  2):
#         print("{} -> {}".format(st,  adjust_datetime(st,  1)))
#         import random as rd

#         st  =  st  +  datetime.timedelta(seconds  =  rd.randint(60,  600))
