import copy
import datetime
import math
import random
from typing import Dict

import numpy as np
import pandas as pd
import xgboost as xgb
from model import BaseModelMainClass
from output.loader import output_callback
from sklearn.metrics import (
    mean_absolute_error,
    mean_absolute_percentage_error,
    mean_squared_error,
    r2_score,
)
from utils.log_util import logger


class ModelMainClass(BaseModelMainClass):
    def __init__(self, project, model, node, args: Dict) -> None:
        self.project = project
        self.model = model
        self.node = node
        self.args = args

    def preprocess_data(
        self, raw_df: pd.DataFrame, column_name: str, new_column_name: str
    ):
        df = copy.deepcopy(raw_df)
        # 转换时间戳类型
        df[new_column_name] = pd.to_datetime(df[column_name])
        # 去除重复时间戳
        df.drop_duplicates(
            subset=new_column_name, keep="last", inplace=True, ignore_index=True
        )

        return df

    def process_data(self, input_data, now_time, start_time):
        # 数据预处理
        df_PV = self.preprocess_data(
            input_data["df_PV"], "count_data_time", "count_data_time"
        )
        df_weather = self.preprocess_data(input_data["df_weather"], "ts", "timeStamp")
        df_weather_future = self.preprocess_data(
            input_data["df_weather_future"], "ts", "timeStamp"
        )

        # 打印查询到的光伏数据最后一条的时刻
        last_df_time = df_PV.loc[len(df_PV) - 1, "count_data_time"]
        logger.info(f"changle test env:: last df count_data_time: {last_df_time}")
        # 计算每天的日出日落时刻
        sunriseTime = []
        sunsetTime = []
        sunriseTime.append(df_PV.loc[0, "count_data_time"])
        for i in range(1, len(df_PV)):
            if (
                df_PV.loc[i, "count_data_time"].year
                > df_PV.loc[i - 1, "count_data_time"].year
                or df_PV.loc[i, "count_data_time"].month
                > df_PV.loc[i - 1, "count_data_time"].month
                or df_PV.loc[i, "count_data_time"].day
                > df_PV.loc[i - 1, "count_data_time"].day
            ):
                sunriseTime.append(df_PV.loc[i, "count_data_time"])
                sunsetTime.append(df_PV.loc[i - 1, "count_data_time"])
        # 如果df的最后一条数据的时间戳小于当前时刻减15分钟，说明最后一条数据是日落时刻的

        now_time_dt = datetime.datetime.strptime(now_time, "%Y/%m/%d %H:%M:%S")
        if df_PV.loc[
            len(df_PV) - 1, "count_data_time"
        ] < now_time_dt - datetime.timedelta(minutes=16):
            sunsetTime.append(df_PV.loc[len(df_PV) - 1, "count_data_time"])
        logger.info(f"changle test env:: sunriseTime: {sunriseTime}")
        logger.info(f"changle test env:: sunsetTime: {sunsetTime}")

        # 整理光伏数据
        # 生成以15分钟间隔的时间序列
        times_15min = pd.date_range(start_time, now_time, freq="15min")
        df_load = pd.DataFrame({"timeStamp": times_15min})
        # df_load["timeStamp"]  =  pd.to_datetime(df["count_data_time"])
        # df_load["PVLoad"]  =  df["h_total_use"].astype('float')
        # 将原始数据映射到时间戳完整的df中
        df_load["PVLoad"] = df_load["timeStamp"].map(
            df_PV.set_index("count_data_time")["h_total_use"]
        )
        df_load["PVLoad"] = df_load["PVLoad"].apply(lambda x: float(x))
        logger.info(f"changle test env:: df_load length: {len(df_load)}")

        # 处理日落到日出之间的空值，填充为0
        # 第一个日出之前的光伏功率填充为0
        df_load.loc[df_load["timeStamp"] < sunriseTime[0], "PVLoad"] = 0
        # 循环处理前一天日落到后一天日出之前的数据填充为0
        for i in range(len(sunriseTime) - 1):
            df_load.loc[
                df_load["timeStamp"].between(sunsetTime[i], sunriseTime[i + 1]),
                "PVLoad",
            ] = 0
        # 最后一个日落时刻到当前时刻填充为0
        if len(sunsetTime) == len(sunriseTime):
            df_load.loc[df_load["timeStamp"] > sunsetTime[-1], "PVLoad"] = 0

        # 删除含空值的行
        df_load.dropna(inplace=True, ignore_index=True)
        logger.info(
            f"changle test env:: df_load length after PV map and drop: {len(df_load)}"
        )

        # 整理天气特征
        df_weather = df_weather[
            ["timeStamp", "rt_ssr", "rt_ws10", "rt_tt2", "rt_dt", "rt_ps", "rt_rain"]
        ]
        # 删除含空值的行
        df_weather.dropna(inplace=True, ignore_index=True)
        # 将除了timeStamp的列转为float类型
        for col in ["rt_ssr", "rt_ws10", "rt_tt2", "rt_dt", "rt_ps", "rt_rain"]:
            df_weather[col] = df_weather[col].apply(lambda x: float(x))

        # 计算相对湿度
        df_weather["cal_rh"] = np.nan
        for i in df_weather.index:
            if (
                df_weather.loc[i, "rt_tt2"] is not np.nan
                and df_weather.loc[i, "rt_dt"] is not np.nan
            ):
                # 通过温度和露点温度计算相对湿度
                temp = (
                    math.exp(
                        17.2693
                        * (df_weather.loc[i, "rt_dt"] - 273.15)
                        / (df_weather.loc[i, "rt_dt"] - 35.86)
                    )
                    / math.exp(
                        17.2693
                        * (df_weather.loc[i, "rt_tt2"] - 273.15)
                        / (df_weather.loc[i, "rt_tt2"] - 35.86)
                    )
                    * 100
                )
                if temp < 0:
                    temp = 0
                elif temp > 100:
                    temp = 100
                df_weather.loc[i, "cal_rh"] = temp
            else:
                rt_tt2 = df_weather.loc[i, "rt_tt2"]
                rt_dt = df_weather.loc[i, "rt_dt"]
                logger.info(f"rt_tt2 is {rt_tt2},  rt_dt is {rt_dt}")

        # 合并功率数据和天气数据
        df_load = pd.merge(df_load, df_weather, on="timeStamp", how="left")
        # 插值填充缺失值
        df_load = df_load.interpolate()
        df_load.dropna(inplace=True, ignore_index=True)
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: df_load length after merge load and weather then drop: {len(df_load)}"
        )

        feature_list = ["rt_ssr", "rt_ws10", "rt_tt2", "cal_rh"]
        # 不区分工作日
        data_X = df_load[feature_list]
        data_Y = df_load["PVLoad"]
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: length of data_X {len(data_X)}"
        )

        return data_X, data_Y, df_weather_future

    def train(self, data_X, data_Y, model_cfgs, data_length: int, split_length: int):
        # 训练集、测试集划分
        X_train = data_X.iloc[-data_length:-split_length]
        Y_train = data_Y.iloc[-data_length:-split_length]
        X_test = data_X.iloc[-split_length:]
        Y_test = data_Y.iloc[-split_length:]

        train_xgb = xgb.DMatrix(X_train, label=Y_train)
        test_xgb = xgb.DMatrix(X_test, label=Y_test)
        # 超参数设置
        xgb_params = model_cfgs["xgb_params"]
        xgb_model = xgb.train(
            xgb_params,
            train_xgb,
            evals=[(train_xgb, "train"), (test_xgb, "val")],
            num_boost_round=100,
            early_stopping_rounds=10,
            verbose_eval=5,
            xgb_model=None,
        )

        Y_predicted = xgb_model.predict(test_xgb)
        Y_predicted[Y_predicted < 0.5] = 0
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: r2:{r2_score(Y_test,  Y_predicted)}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: mse:{mean_squared_error(Y_test,  Y_predicted)}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: mae:{mean_absolute_error(Y_test,  Y_predicted)}"
        )
        accuracy = 1 - mean_absolute_percentage_error(Y_test, Y_predicted)
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: mape accuracy:{accuracy}"
        )

        return xgb_model

    def predict(
        self,
        xgb_model,
        df_weather_future: pd.DataFrame,
        now_time,
        future_time,
    ):
        # 生成未来5天以15分钟间隔的时间序列
        future_times_15min = pd.date_range(now_time, future_time, freq="15min")
        # 创建DataFrame并添加timeStamp列
        df_future = pd.DataFrame({"timeStamp": future_times_15min})

        df_weather_future = df_weather_future[
            ["timeStamp", "pred_ssrd", "pred_ws10", "pred_tt2", "pred_rh"]
        ]
        # 删除含空值的行
        df_weather_future.dropna(inplace=True, ignore_index=True)
        # 将除了timeStamp的列转为float类型
        for col in ["pred_ssrd", "pred_ws10", "pred_tt2", "pred_rh"]:
            df_weather_future[col] = df_weather_future[col].apply(lambda x: float(x))
        # 将预测天气数据整理到预测df中
        df_future["rt_ssr"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_ssrd"]
        )
        df_future["rt_ws10"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_ws10"]
        )
        df_future["rt_tt2"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_tt2"]
        )
        df_future["cal_rh"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_rh"]
        )
        # 插值填充预测缺失值
        df_future = df_future.interpolate()
        df_future.dropna(inplace=True, ignore_index=True)

        feature_list = ["rt_ssr", "rt_ws10", "rt_tt2", "cal_rh"]
        # 不区分工作日
        X_future = df_future[feature_list]
        future_xgb = xgb.DMatrix(X_future)
        Y_future = xgb_model.predict(future_xgb)
        df_future["load"] = Y_future
        # 如果预测光照强度小于1，将光伏发电预测值置为0
        df_future.loc[df_future["rt_ssr"] < 1, "load"] = 0
        df_future.loc[df_future["load"] < 0.5, "load"] = 0

        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: length of df_future {len(df_future)}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: future 5days load is \n {df_future.iloc[-10:]}"
        )

        return df_future

    def process_output(self, df_future, model_cfgs):
        node = model_cfgs["nodes"]["node"]
        for i in range(len(df_future)):
            df_future.loc[i, "id"] = (
                node["node_id"]
                + "_"
                + node["out_system_id"]
                + "_"
                + df_future.loc[i, "timeStamp"].strftime("%Y%m%d%H%M%S")
            )
            df_future.loc[i, "node_id"] = node["node_id"]
            # 区分in_system_id和out_system_id
            df_future.loc[i, "system_id"] = node["out_system_id"]
            df_future.loc[i, "predict_value"] = str(df_future.loc[i, "load"])
            df_future.loc[i, "predict_adjustable_amount"] = str(
                df_future.loc[i, "load"] * random.uniform(0.05, 0.1)
            )
            df_future.loc[i, "count_data_time"] = df_future.loc[
                i, "timeStamp"
            ].strftime("%Y-%m-%d %H:%M:%S.%f")[
                :-3
            ]  # 保留毫秒并精确到前3位

        df_future = df_future[
            [
                "id",
                "node_id",
                "system_id",
                "predict_value",
                "predict_adjustable_amount",
                "count_data_time",
            ]
        ]

        return df_future

    """
    input_data: InputData
    model_cfgs: Dict
    output_func: Dict (!!!不可删除,  output_callback装饰器会用到!!!)
    """

    @output_callback
    def run(self, input_data: Dict, model_cfgs: Dict, output_func: Dict):
        # 模型输入
        # 获取时间范围
        logger.info(f"model_cfgs: {model_cfgs}")
        now_time, start_time, future_time = (
            model_cfgs["time_range"]["now_time"],
            model_cfgs["time_range"]["start_time"],
            model_cfgs["time_range"]["future_time"],
        )
        # 数据处理
        data_X, data_Y, df_weather_future = self.process_data(
            input_data, now_time, start_time
        )
        # 不区分工作日
        xgb_model = self.train(
            data_X, data_Y, model_cfgs, data_length=len(data_X), split_length=96
        )
        # 预测未来5天负荷
        df_future = self.predict(xgb_model, df_weather_future, now_time, future_time)
        # 输出结果处理
        df_future = self.process_output(df_future, model_cfgs)

        return {"df_future": df_future}

    @output_callback
    def test_run(self, input_data, model_cfgs, output_func):
        logger.info("test run")
        df = pd.DataFrame(
            {
                "id": pd.Series(1, index=list(range(4)), dtype="str"),
                "node_id": "1",
                "system_id": "1",
                "predict_value": "1",
                "predict_adjustable_amount": "1",
                "count_data_time": "2022  - 01  - 01 00:00:00",
            }
        )
        return {"future": df}
