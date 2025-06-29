import copy
import warnings
from typing import Dict

warnings.filterwarnings("ignore")

import cvxpy as cp
import numpy as np
import pandas as pd
from model import BaseModelMainClass
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

    def process_data(self, input_data, start_time, end_time):
        # 数据预处理
        df_weather = self.preprocess_data(input_data["df_weather"], "ts", "timeStamp")
        df_date = self.preprocess_data(input_data["df_date"], "date", "date")
        df_load = self.preprocess_data(
            input_data["df_load"], "count_data_time", "timeStamp"
        )
        df_price = input_data["df_price"]

        # 整理天气特征
        df_weather = df_weather[["timeStamp", "pred_tt2"]]  # 特征筛选
        df_weather.dropna(inplace=True, ignore_index=True)  # 删除含空值的行
        df_weather["pred_tt2"] = df_weather["pred_tt2"].apply(
            lambda x: float(x)
        )  # 将除了timeStamp的列转为float类型
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::df_weather: \n{df_weather}"
        )

        # 校验电价表长度
        if len(df_price) == 24:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::df_price length is {len(df_price)}"
            )
        else:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::wrong df_price length is {len(df_price)}"
            )

        # 生成以15分钟间隔的时间序列
        # -----------------------------
        # 创建DataFrame并添加timeStamp列
        df = pd.DataFrame(
            {"timeStamp": pd.date_range(start_time, end_time, freq="15min")}
        )
        # 增加电力需求特征
        df["demandLoad"] = df["timeStamp"].map(
            df_load.set_index("timeStamp")["predict_value"]
        )
        df["demandLoad"] = df["demandLoad"].apply(lambda x: float(x))
        # 增加电价和峰谷平列
        df["price"] = np.array([x for element in df_price["price"] for x in [element]])
        df["property"] = np.array(
            [x for element in df_price["property"] for x in [element]]
        )
        df["day_of_week"] = df["timeStamp"].map(
            df_date.set_index("date")["day_of_week"]
        )
        # 填充缺失值
        df = df.ffill()
        df = df.bfill()
        # 填充负值
        df["demandLoad"] = df["demandLoad"].apply(lambda x: np.nan if x < 0 else x)
        # 填充缺失值
        df = df.ffill()
        df = df.bfill()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::df has nan or not\n{df.isna().any()}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::first df after fill \n{df.iloc[: 10]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::last df after fill \n{df.iloc[-10:]}"
        )

        return df

    def calculate(self, df, model_cfgs):
        """
        demandLoad: 需量功率(预测值)【d】
        power: 储能充放功率(正为放电,  负为充电)【e_c】
        price: 电价 【p】
        LastSoc: 前一天剩余电量 【e_r】
        e_l: 每个时段损耗电量
        usableDepth: 可用深度
        systemEfficiency: 系统效率：单边充放电功率损失 【c_l】
        designPower: 最大功率: 充放电功率上限【e_c_max】
        minPower: 充放电功率下限【e_c_min】
        designCapacity: 储能容量: 储能器 soc 上限【e_s_max】
        minCapacity: 储能器 soc 下限【e_s_min】
        lambda_valley: 谷电时段矫正系数
        lambda_flat: 平电时段矫正系数
        lambda_peak: 峰电时段矫正系数
        lambda_top: 尖电时段矫正系数
        """
        # 定义常量
        demandLoad = np.array(df["demandLoad"])
        price = np.array(df["price"])
        lastSoc = 0
        e_l = 15 / 96
        usableDepth = model_cfgs["usableDepth"]  #  = 0.97
        systemEfficiency = model_cfgs["systemEfficiency"]  #  = 0.933
        designPower = model_cfgs["designPower"]  #  =  100
        minPower = model_cfgs["minPower"]  #  =  -designPower
        designCapacity = model_cfgs["designCapacity"]  #  =  215
        minCapacity = model_cfgs["minCapacity"]  #  = 0
        lambda_valley = model_cfgs["lambda_valley"]  #  = 0.0001
        lambda_flat = model_cfgs["lambda_flat"]  #  = 0.0001
        lambda_peak = model_cfgs["lambda_peak"]  #  =  -1.5  *  lambda_valley
        lambda_top = model_cfgs["lambda_top"]  #  =  -1.5  *  lambda_valley
        # 谷平峰尖index集合
        # valley_index  =  []
        # flat_index  =  []
        # peak_index  =  []
        # top_index  =  []
        # for i in range(len(df)):
        #     if df.loc[i,  "property"] == "谷":
        #         valley_index.append(i)
        #     elif df.loc[i,  "property"] == "平":
        #         flat_index.append(i)
        #     elif df.loc[i,  "property"] == "峰":
        #         peak_index.append(i)
        #     elif df.loc[i,  "property"] == "尖":
        #         top_index.append(i)
        #     else:
        #         logger.error(f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: price property error")
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: valley_index: {valley_index}")
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: flat_index: {flat_index}")
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: peak_index: {peak_index}")
        # logger.info(f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: top_index: {top_index}")

        # 定义变量
        power = cp.Variable(len(df))
        soc = cp.Variable(len(df))

        # 目标函数
        # 每日收益
        profit = power @ price

        start_time = model_cfgs["time_range"]["start_time"]
        # 转换成datetime
        start_time = pd.to_datetime(start_time)
        # 谷电矫正项
        profit = profit + lambda_valley * cp.sum(soc[0:24])
        profit = profit + lambda_valley * cp.sum(soc[88:96])
        # 平电矫正项
        profit = profit + lambda_flat * cp.sum(soc[24:32])
        profit = profit + lambda_flat * cp.sum(soc[84:88])
        if start_time.month >= 7 and start_time.month <= 9:
            profit = profit + lambda_flat * cp.sum(soc[60:72])
        else:
            profit = profit + lambda_flat * cp.sum(soc[44:72])
        # 峰电矫正项
        profit = profit + lambda_peak * cp.sum(soc[32:44])
        profit = profit + lambda_peak * cp.sum(soc[72:76])
        if start_time.month >= 2 and start_time.month <= 11:
            profit = profit + lambda_peak * cp.sum(soc[76:84])
            if start_time.month == 9:
                profit = profit + lambda_peak * cp.sum(soc[44:60])
            elif start_time.month >= 7 and start_time.month <= 8:
                profit = profit + lambda_peak * cp.sum(soc[44:48])
                profit = profit + lambda_peak * cp.sum(soc[56:60])
                # 尖电矫正项
                profit = profit + lambda_top * cp.sum(soc[48:56])
        else:
            # 尖电矫正项
            profit = profit + lambda_top * cp.sum(soc[76:84])
        """
        # 谷电矫正项
        if len(valley_index) > 0:
            for j in valley_index:
                profit  =  profit  +  lambda_valley  *  soc[j]
        # 平电矫正项
        if len(flat_index) > 0:
            for j in flat_index:
                profit  =  profit  +  lambda_flat  *  soc[j]
        # 峰电矫正项
        if len(peak_index) > 0:
            for j in peak_index:
                profit  =  profit  +  lambda_peak  *  soc[j]
        # 尖电矫正项
        if len(top_index) > 0:
            for j in top_index:
                profit  =  profit  +  lambda_top  *  soc[j]
        """

        # 最终目标函数
        obj = cp.Maximize(profit)

        # 设置约束条件
        constraints = []
        # 放电功率小于需量
        constraints += [power * systemEfficiency <= demandLoad]
        # 储能系统每个时段的充放电功率限制
        constraints += [power <= designPower]
        # constraints += [power >= minPower  *  systemEfficiency]
        constraints += [power >= minPower]

        for i in range(1, len(df) + 1):
            # 充电功率和实时电量匹配
            constraints += [
                soc[i - 1] == lastSoc - cp.sum(power[:i]) * 0.25 * systemEfficiency
            ]
            # 对电量损耗的保底电量限制
            constraints += [soc[i - 1] >= 15 - i * e_l]

        # 储能器容量限制
        constraints += [soc >= minCapacity]
        # constraints += [soc <= designCapacity  *  usableDepth]
        constraints += [soc <= designCapacity]

        # 丑陋的东西
        constraints += [power[0:24] <= 0]
        constraints += [power[24:32] == 0]
        if start_time.month == 9:
            constraints += [power[32:60] >= 0]
        elif start_time.month >= 7 and start_time.month <= 8:
            constraints += [power[32:48] == 0]
            constraints += [power[48:60] >= 0]
        else:
            constraints += [power[32:44] >= 0]
            constraints += [power[44:60] <= 0]
        constraints += [power[60:72] <= 0]
        constraints += [power[72:84] >= 0]
        constraints += [power[84:88] == 0]
        constraints += [power[88:96] <= 0]
        """
        # 谷电矫正项
        if len(valley_index) > 0:
            for j in valley_index:
                constraints += [power[j] <= 0]
        else:
            logger.error(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: constraints valley_index: {valley_index}"
            )
        # 平电矫正项
        if len(flat_index) > 0:
            for j in flat_index:
                constraints += [power[j] <= 0]
        else:
            logger.error(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: constraints flat_index: {flat_index}"
            )
        # 峰电矫正项
        if len(peak_index) > 0:
            for j in peak_index:
                constraints += [power[j] >= 0]
        else:
            logger.error(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: constraints peak_index: {peak_index}"
            )
        # 尖电矫正项
        if len(top_index) > 0:
            for j in top_index:
                constraints += [power[j] >= 0]
        else:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: constraints top_index: {top_index}"
            )
        """
        # 优化问题求解
        # -----------
        prob = cp.Problem(obj, constraints)
        # result  =  prob.solve(verbose  =  False,  solver  =  cp.ECOS)
        result = prob.solve(verbose=False, solver=cp.CLARABEL)

        # 优化问题的解
        power_opt = power.value
        power_opt = np.around(power_opt, decimals=3)
        # 去掉零前面的负号
        for i in range(len(power_opt)):
            if abs(power_opt[i]) < 1:
                power_opt[i] = 0

        df["power_opt"] = power_opt
        df["power_final"] = df["power_opt"].where(
            df["power_opt"] >= 0, df["power_opt"] / systemEfficiency
        )
        df["power_final"] = np.around(df["power_final"], decimals=3)
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::before peak time process power_opt  =  \n{power_opt}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::strategy \n{df.iloc[:20]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::strategy \n{df.iloc[20:40]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::strategy \n{df.iloc[40:60]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::strategy \n{df.iloc[60:80]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::strategy \n{df.iloc[80:96]}"
        )

        return df

    """
    input_data: InputData
    model_cfgs: Dict
    """

    def run(self, input_data: Dict, model_cfgs: Dict):
        # 获取时间范围
        start_time, end_time = (
            model_cfgs["time_range"]["start_time"],
            model_cfgs["time_range"]["end_time"],
        )
        # 数据处理
        df = self.process_data(input_data, start_time, end_time)
        # 计算
        df_future = self.calculate(df, model_cfgs)
        return {"df_future": df_future}

    #! 本地数据读取
    def get_data(self, node: str, now: str, history_days: int, predict_days: int):
        # 历史数据
        import datetime
        import os

        now_time = now.strftime("%Y%m%d")
        ts_range_future = (now + datetime.timedelta(days=1)).strftime("%Y%m%d")
        data_dir = f"./dataset/{self.project}_dev_{now_time}_hist{history_days}days_pred{predict_days}days/{node}/stra/"
        df_weather = pd.read_csv(
            os.path.join(data_dir, f"df_weather_{ts_range_future}.csv"),
            encoding="utf  -  8",
        )
        df_date = pd.read_csv(
            os.path.join(data_dir, f"df_date_{ts_range_future}.csv"),
            encoding="utf  -  8",
        )
        df_load = pd.read_csv(
            os.path.join(data_dir, f"df_power_{ts_range_future}.csv"),
            encoding="utf  -  8",
        )
        df_price = pd.read_csv(
            os.path.join(data_dir, f"df_price_{ts_range_future}.csv"),
            encoding="utf  -  8",
        )
        # 输入数据以字典形式整理
        input_data = {}
        input_data["df_weather"] = df_weather
        input_data["df_date"] = df_date
        input_data["df_load"] = df_load
        input_data["df_price"] = df_price

        return input_data


# def main():
#     import datetime

#     # node_name  =  "阿石创新材料公司储能组2"
#     project  =  "ashichuang"
#     model  =  "power optimization"
#     node  =  "asc2"
#     history_days  =  30
#     predict_days  =  5
#     # 时间索引
#     now  =  datetime.datetime(2025,  2,  15,  23,  46,  0)
#     now_time  =  now.strftime("%Y%m%d")
#     start_time  =  (now  +  datetime.timedelta(days  =  1)).replace(hour  = 0,  minute  = 0,  second  = 0,  microsecond  = 0)
#     end_time  =  (now  +  datetime.timedelta(days  =  1)).replace(hour  =  23,  minute  =  48,  second  = 0,  microsecond  = 0)
#     logger.info(f"now_time: {now_time}")
#     logger.info(f"start_time: {start_time}")
#     logger.info(f"end_time: {end_time}")
#     # 模型实例
#     model_cls  =  ModelMainClass(project  =  project,  model  =  model,  node  =  node,  args={})
#     #! 获取数据
#     input_data =  model_cls.get_data(
#         node  =  node,
#         now  =  now,
#         history_days  =  history_days,
#         predict_days  =  predict_days,
#     )
#     #! 模型参数
#     model_cfgs  =  {
#         "time_range": {
#             "start_time": start_time,
#             "end_time": end_time,
#         },
#         "usableDepth": 0.97,
#         "systemEfficiency": 1,
#         "designPower": 14000,
#         "minPower": -14000,
#         "designCapacity": 28000,
#         "minCapacity": 0,
#         "lambda_valley": 0.0001,
#         "lambda_flat": 0.0001,
#         "lambda_peak": -1.5  * 0.0001,
#         "lambda_top": -1.5  * 0.0001,
#     }
#     # 模型运行
#     res  =  model_cls.run(input_data =  input_data,  model_cfgs  =  model_cfgs)
#     logger.info(f"res['df_future']: \n{res['df_future']}")
#     #! 模型结果保存
#     res["df_future"].to_csv(
#         f"./results/{project}_dev_{now_time}/df_future_stra_{node}.csv",
#         encoding="utf_8_sig",
#         index  =  False
#     )

# if __name__ == "__main__":
#     main()
