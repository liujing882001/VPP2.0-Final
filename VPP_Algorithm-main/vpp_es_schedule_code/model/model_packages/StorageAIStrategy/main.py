import copy
from typing import Dict

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
        df_weather = df_weather[["timeStamp", "pred_tt2"]]
        # 删除含空值的行
        df_weather.dropna(inplace=True, ignore_index=True)
        # 将除了timeStamp的列转为float类型
        for col in ["pred_tt2"]:
            df_weather[col] = df_weather[col].apply(lambda x: float(x))
        predTT2 = df_weather["pred_tt2"].mean()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::predTT2: {predTT2}"
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

        # 生成每15min电价数组
        price_hour = df_price["price_hour"]
        ele_price = np.array([x for element in price_hour for x in [element] * 4])
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::price_hour is \n{price_hour}"
        )

        # 生成每15min峰谷平数组
        property = df_price["property"]
        property_arr = np.array([x for element in property for x in [element] * 4])
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::property is \n{property}"
        )

        # 生成以15分钟间隔的时间序列
        times_15min = pd.date_range(start_time, end_time, freq="15min")
        # 创建DataFrame并添加timeStamp列
        df = pd.DataFrame({"timeStamp": times_15min})

        # 增加电价和峰谷平列
        df["ele_price"] = ele_price
        df["property"] = property_arr

        # 将原始数据映射到时间戳完整的df中
        df["demandLoad"] = df["timeStamp"].map(
            df_load.set_index("timeStamp")["predict_value"]
        )
        df["demandLoad"] = df["demandLoad"].apply(lambda x: float(x))

        # 填充缺失值
        df = df.ffill()
        df = df.bfill()
        # 填充负值
        df["demandLoad"] = df["demandLoad"].apply(lambda x: np.nan if x < 0 else x)
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
        return df, df_date, predTT2, ele_price

    def calculate(self, df, df_date, ele_price, predTT2, model_cfgs):
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
        price = ele_price
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
        valley_index = []
        flat_index = []
        peak_index = []
        top_index = []
        for i in range(len(df)):
            if df.loc[i, "property"] == "谷":
                valley_index.append(i)
            elif df.loc[i, "property"] == "平":
                flat_index.append(i)
            elif df.loc[i, "property"] == "峰":
                peak_index.append(i)
            elif df.loc[i, "property"] == "尖":
                top_index.append(i)
            else:
                logger.error(
                    f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: price property error"
                )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: valley_index: {valley_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: flat_index: {flat_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: peak_index: {peak_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: top_index: {top_index}"
        )

        # 定义变量
        power = cp.Variable(len(df))
        soc = cp.Variable(len(df))

        # 目标函数
        # 每日收益
        profit = power @ price
        # 谷电矫正项
        if len(valley_index) > 0:
            for j in valley_index:
                profit = profit + lambda_valley * soc[j]
        # 平电矫正项
        if len(flat_index) > 0:
            for j in flat_index:
                profit = profit + lambda_flat * soc[j]
        # 峰电矫正项
        if len(peak_index) > 0:
            for j in peak_index:
                profit = profit + lambda_peak * soc[j]
        # 尖电矫正项
        if len(top_index) > 0:
            for j in top_index:
                profit = profit + lambda_top * soc[j]
        # 最终目标函数
        obj = cp.Maximize(profit)

        # 设置约束条件
        constraints = []
        # 放电功率小于需量
        constraints += [power * systemEfficiency <= demandLoad]
        # 储能系统每个时段的充放电功率限制
        constraints += [power <= designPower]
        constraints += [power >= minPower * systemEfficiency]

        for i in range(1, len(df) + 1):
            # 充电功率和实时电量匹配
            constraints += [soc[i - 1] == lastSoc - cp.sum(power[:i]) * 0.25]
            # 对电量损耗的保底电量限制
            constraints += [soc[i - 1] >= 15 - i * e_l]

        # 储能器容量限制
        constraints += [soc >= minCapacity]
        constraints += [soc <= designCapacity * usableDepth]

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

        prob = cp.Problem(obj, constraints)
        # result  =  prob.solve(verbose  =  False,  solver  =  cp.ECOS)
        result = prob.solve(verbose=False, solver=cp.CLARABEL)

        power_opt = power.value

        power_opt_1 = np.around(power_opt / 2, decimals=3)
        power_opt_2 = np.around(power_opt / 2, decimals=3)

        # 去掉零前面的负号
        for i in range(len(power_opt_1)):
            if abs(power_opt_1[i]) < 0.1:
                power_opt_1[i] = 0
        for i in range(len(power_opt_2)):
            if abs(power_opt_2[i]) < 0.1:
                power_opt_2[i] = 0

        df["power_opt_1"] = power_opt_1
        df["power_opt_2"] = power_opt_2
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::before peak time process power_opt_1  =  \n{power_opt_1}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::before peak time process power_opt_2  =  \n{power_opt_2}"
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

        # 工作日谷时慢充，平时12~15点按80充
        count_40 = int(df.loc[:31, "power_opt_1"].sum() // (-40))
        remainder_40 = np.around(df.loc[:31, "power_opt_1"].sum() % (-40), decimals=3)
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::count_40: {count_40}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::remainder_40: {remainder_40}"
        )
        if df_date.loc[0, "date_type"] < 2 and count_40 < 31:
            df.loc[:count_40, "power_opt_1"] = -40
            df.loc[count_40 + 1, "power_opt_1"] = remainder_40
            df.loc[:count_40, "power_opt_2"] = -40
            df.loc[count_40 + 1, "power_opt_2"] = remainder_40
        count_80 = int(
            (df.loc[48:59, "power_opt_1"].sum() + df.loc[80:83, "power_opt_1"].sum())
            // (-80)
        )
        remainder_80 = np.around(
            (df.loc[48:59, "power_opt_1"].sum() + df.loc[80:83, "power_opt_1"].sum())
            % (-80),
            decimals=3,
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::count_80: {count_80}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::remainder_80: {remainder_80}"
        )
        if df_date.loc[0, "date_type"] < 2 and count_80 < 11 and count_80 >= 1:
            df.loc[48 : 48 + count_80, "power_opt_1"] = -80
            df.loc[48 + count_80 + 1, "power_opt_1"] = remainder_80
            df.loc[48 : 48 + count_80, "power_opt_2"] = -80
            df.loc[48 + count_80 + 1, "power_opt_2"] = remainder_80
        # 工作日且预测平均2m气温低于301K，晚上20点到21点待机
        if df_date.loc[0, "date_type"] < 2 and predTT2 < 301:
            df.loc[80:83, "power_opt_1"] = 0
            df.loc[80:83, "power_opt_2"] = 0
        # 如果当前是峰时，强制充放电功率+100kw
        df.loc[df["property"] == "峰", "power_opt_1"] = 100
        df.loc[df["property"] == "峰", "power_opt_2"] = 100
        # 如果当前是尖时，强制充放电功率+100kw
        df.loc[df["property"] == "尖", "power_opt_1"] = 100
        df.loc[df["property"] == "尖", "power_opt_2"] = 100
        # 如果是7、8、9月的工作日，且预测平均2m气温高于301K，下午15~16点待机
        if (
            df_date.loc[0, "month"] >= 7
            and df_date.loc[0, "month"] <= 9
            and df_date.loc[0, "date_type"] < 2
            and predTT2 > 301
        ):
            logger.info("do peak time processing\n")
            df.loc[60:63, "power_opt_1"] = 0
            df.loc[60:63, "power_opt_2"] = 0
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after peak time process\n"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after process strategy \n{df.iloc[:20]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after process strategy \n{df.iloc[20:40]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after process strategy \n{df.iloc[40:60]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after process strategy \n{df.iloc[60:80]}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::after process strategy \n{df.iloc[80:96]}"
        )
        power_list_1 = df["power_opt_1"].to_list()
        power_list_2 = df["power_opt_2"].to_list()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::power_list_1: {power_list_1}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::power_list_2: {power_list_2}"
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
        df, df_date, predTT2, ele_price = self.process_data(
            input_data, start_time, end_time
        )
        # 计算
        df_future = self.calculate(df, df_date, ele_price, predTT2, model_cfgs)

        return {"df_future": df_future}
