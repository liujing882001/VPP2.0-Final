import copy
import math
import random
from typing import Dict

import lightgbm as lgb
import numpy as np
import pandas as pd
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
        df_power = self.preprocess_data(
            input_data["df_power"], "count_data_time", "count_data_time"
        )
        df_date = self.preprocess_data(input_data["df_date"], "date", "date")
        # 把df_date列都赋值为1
        df_date["date_type"] = 1
        df_weather = self.preprocess_data(input_data["df_weather"], "ts", "timeStamp")
        df_date_future = self.preprocess_data(
            input_data["df_date_future"], "date", "date"
        )
        df_weather_future = self.preprocess_data(
            input_data["df_weather_future"], "ts", "timeStamp"
        )

        # 整理历史功率数据
        # 生成以15分钟间隔的时间序列
        times_15min = pd.date_range(start_time, now_time, freq="15min")
        # 创建DataFrame并添加timeStamp列
        df_load = pd.DataFrame({"timeStamp": times_15min})

        # 将原始数据映射到时间戳完整的df中
        df_load["load"] = df_load["timeStamp"].map(
            df_power.set_index("count_data_time")["h_total_use"]
        )

        df_load["load"] = df_load["load"].apply(lambda x: float(x))
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: df_load length: {len(df_load)}"
        )

        # 删除含空值的行
        df_load.dropna(inplace=True, ignore_index=True)
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: df_load length after PV map and drop: {len(df_load)}"
        )

        # 如果需求负荷小于0，删除
        df_load = df_load[df_load["load"] > 0]
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: df_load has nan or not\n{df_load.isna().any()}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: df_load length: {len(df_load)}"
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

        df_load = self.extend_datetime_stamp_feature(df_load)
        df_load = self.extend_date_type_feature(df_load, df_date)
        feature_list = [
            "rt_ssr",
            "rt_ws10",
            "rt_tt2",
            "cal_rh",
            "rt_ps",
            "rt_rain",
            "datetime_minute",
            "datetime_hour",
            "datetime_day",
            "datetime_weekday",
            "datetime_week",
            "datetime_day_of_week",
            "datetime_week_of_year",
            "datetime_month",
            "datetime_days_in_month",
            "datetime_quarter",
            "datetime_day_of_year",
            "datetime_year",
            "date_type",
        ]
        # 不区分工作日
        data_X = df_load[feature_list]
        data_Y = df_load["load"]
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}:: length of data_X {len(data_X)}"
        )
        # 区分工作日/非工作日
        # df_load_workday  =  copy.deepcopy(df_load[df_load["date_type"] == 1])
        # data_X_workday  =  df_load_workday[feature_list]
        # data_Y_workday  =  df_load_workday["load"]
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}:: length of df_load_workday {len(df_load_workday)}")
        # df_load_offday  =  copy.deepcopy(df_load[df_load["date_type"] == 2])
        # data_X_offday  =  df_load_offday[feature_list]
        # data_Y_offday  =  df_load_offday["load"]
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}:: length of df_load_offday {len(df_load_offday)}")

        return data_X, data_Y, df_date_future, df_weather_future
        # return data_X_workday,  data_Y_workday,  data_X_offday,  data_Y_offday,  df_date_future,  df_weather_future

    def extend_datetime_stamp_feature(self, df: pd.DataFrame):
        """
        增加时间特征
        :param df:
        :return:
        """
        df["datetime_minute"] = df["timeStamp"].apply(lambda x: x.minute)
        df["datetime_hour"] = df["timeStamp"].apply(lambda x: x.hour)
        df["datetime_day"] = df["timeStamp"].apply(lambda x: x.day)

        df["datetime_weekday"] = df["timeStamp"].apply(lambda x: x.weekday())
        df["datetime_week"] = df["timeStamp"].apply(lambda x: x.week)
        df["datetime_day_of_week"] = df["timeStamp"].apply(lambda x: x.dayofweek)

        df["datetime_week_of_year"] = df["timeStamp"].apply(lambda x: x.weekofyear)
        df["datetime_month"] = df["timeStamp"].apply(lambda x: x.month)
        df["datetime_days_in_month"] = df["timeStamp"].apply(lambda x: x.daysinmonth)

        df["datetime_quarter"] = df["timeStamp"].apply(lambda x: x.quarter)
        df["datetime_day_of_year"] = df["timeStamp"].apply(lambda x: x.dayofyear)
        df["datetime_year"] = df["timeStamp"].apply(lambda x: x.year)

        return df

    def extend_date_type_feature(self, df: pd.DataFrame, df_date: pd.DataFrame):
        """
        增加日期类型特征：
        1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节
        """
        df["date"] = df["timeStamp"].apply(
            lambda x: x.replace(hour=0, minute=0, second=0, microsecond=0)
        )
        df["date_type"] = df["date"].map(df_date.set_index("date")["date_type"])

        return df

    def train(self, data_X, data_Y, model_cfgs, data_length: int, split_length: int):
        # 训练集、测试集划分
        X_train = data_X.iloc[-data_length:-split_length]
        Y_train = data_Y.iloc[-data_length:-split_length]
        X_test = data_X.iloc[-split_length:]
        Y_test = data_Y.iloc[-split_length:]

        # 超参数设置
        lgbm_params = model_cfgs["lgbm_params"]
        lgb_model = lgb.LGBMRegressor(**lgbm_params)
        lgb_model.fit(X_train, Y_train)

        Y_predicted = lgb_model.predict(X_test)
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

        return lgb_model

    def predict(
        self,
        lgb_model: lgb.LGBMRegressor,
        df_date_future: pd.DataFrame,
        df_weather_future: pd.DataFrame,
        now_time,
        future_time,
    ):
        # 生成未来5天以15分钟间隔的时间序列
        future_times_15min = pd.date_range(now_time, future_time, freq="15min")
        # 创建DataFrame并添加timeStamp列
        df_future = pd.DataFrame({"timeStamp": future_times_15min})

        df_future = self.extend_datetime_stamp_feature(df_future)
        df_future = self.extend_date_type_feature(df_future, df_date_future)

        df_weather_future = df_weather_future[
            [
                "timeStamp",
                "pred_ssrd",
                "pred_ws10",
                "pred_tt2",
                "pred_rh",
                "pred_ps",
                "pred_rain",
            ]
        ]
        # 删除含空值的行
        df_weather_future.dropna(inplace=True, ignore_index=True)
        # 将除了timeStamp的列转为float类型
        for col in [
            "pred_ssrd",
            "pred_ws10",
            "pred_tt2",
            "pred_rh",
            "pred_ps",
            "pred_rain",
        ]:
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
        df_future["rt_ps"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_ps"]
        )
        df_future["rt_rain"] = df_future["timeStamp"].map(
            df_weather_future.set_index("timeStamp")["pred_rain"]
        )
        # 插值填充预测缺失值
        df_future = df_future.interpolate()
        df_future.dropna(inplace=True, ignore_index=True)

        feature_list = [
            "rt_ssr",
            "rt_ws10",
            "rt_tt2",
            "cal_rh",
            "rt_ps",
            "rt_rain",
            "datetime_minute",
            "datetime_hour",
            "datetime_day",
            "datetime_weekday",
            "datetime_week",
            "datetime_day_of_week",
            "datetime_week_of_year",
            "datetime_month",
            "datetime_days_in_month",
            "datetime_quarter",
            "datetime_day_of_year",
            "datetime_year",
            "date_type",
        ]
        # 不区分工作日
        X_future = df_future[feature_list]
        Y_future = lgb_model.predict(X_future)
        df_future["load"] = Y_future
        # 区分工作日/非工作日
        # df_future_workday  =  copy.deepcopy(df_future[df_future["date_type"] == 1])
        # X_future_workday  =  df_future_workday[feature_list]
        # Y_future_workday  =  lgb_model_workday.predict(X_future_workday)
        # df_future_workday["load"]  =  Y_future_workday
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}:: df_future_workday is \n {df_future_workday.iloc[-10:]}")
        # df_future_offday  =  copy.deepcopy(df_future[df_future["date_type"] > 1])
        # X_future_offday  =  df_future_offday[feature_list]
        # Y_future_offday  =  lgb_model_offday.predict(X_future_offday)
        # df_future_offday["load"]  =  Y_future_offday
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}:: df_future_offday is \n {df_future_offday.iloc[-10:]}")

        # df_future  =  pd.merge(df_future,  df_future_workday,  how="outer")
        # df_future  =  pd.merge(df_future,  df_future_offday,  how="outer")
        # df_future.dropna(inplace  =  True,  ignore_index  =  True)

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

    # @output_callback
    def run(self, input_data: Dict, model_cfgs: Dict, output_func: Dict = None):
        # 模型输入
        # 获取时间范围
        logger.info(f"model_cfgs: {model_cfgs}")
        now_time, start_time, future_time = (
            model_cfgs["time_range"]["now_time"],
            model_cfgs["time_range"]["start_time"],
            model_cfgs["time_range"]["future_time"],
        )
        # 数据处理
        data_X, data_Y, df_date_future, df_weather_future = self.process_data(
            input_data, now_time, start_time
        )
        # 不区分工作日
        lgb_model = self.train(
            data_X, data_Y, model_cfgs, data_length=len(data_X), split_length=96
        )
        # 区分工作日/非工作日训练模型
        # lgb_model_workday  =  self.train(data_X_workday,  data_Y_workday,  data_length  = 96  *  15,  split_length  = 96)
        # lgb_model_offday  =  self.train(data_X_offday,  data_Y_offday,  data_length  =  len(data_X_offday),  split_length  = 96)
        # 预测未来5天负荷
        df_future = self.predict(
            lgb_model, df_date_future, df_weather_future, now_time, future_time
        )
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
