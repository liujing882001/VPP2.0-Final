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
        df_load = self.preprocess_data(
            input_data["df_load"], "count_data_time", "timeStamp"
        )
        df_price = self.preprocess_data(input_data["df_price"], "time", "timeStamp")
        # 校验电价表长度
        if len(df_price) == 96 and len(df_load) == 96:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::df_price length: {len(df_price)},  df_load length: {len(df_load)}"
            )
        else:
            logger.error(
                f"project: {self.project},  model: {self.model},  node: {self.node}::wrong df_price length: {len(df_price)},  df_load length: {len(df_load)}"
            )

        # 生成以15分钟间隔的时间序列
        times_15min = pd.date_range(start_time, end_time, freq="15min")
        # 创建DataFrame并添加timeStamp列
        df = pd.DataFrame({"timeStamp": times_15min})

        # 将原始数据映射到时间戳完整的df中
        df["demandLoad"] = df["timeStamp"].map(
            df_load.set_index("timeStamp")["predict_value"]
        )
        df["demandLoad"] = df["demandLoad"].apply(lambda x: float(x))
        df["price"] = df["timeStamp"].map(df_price.set_index("timeStamp")["price_hour"])
        df["price"] = df["price"].apply(lambda x: float(x))
        df["property"] = df["timeStamp"].map(
            df_price.set_index("timeStamp")["property"]
        )

        # 填充缺失值
        df = df.ffill()
        df = df.bfill()
        # 填充负值
        df["demandLoad"] = df["demandLoad"].apply(lambda x: np.nan if x < 0 else x)
        df = df.ffill()
        df = df.bfill()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::df length: {len(df)}"
        )
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

    def calculate(self, df, model_cfgs, month):
        """
        demandLoad: 需量功率
        power：储能充放功率（正为放电，负为充电)
        price：电价
        lastSoc：前一天剩余电量
        e_l：每个时段损耗电量
        usableDepth：可用深度
        systemEfficiency: 系统效率：单边充放电功率损失
        designPower：最大功率：充放电功率上限
        minPower：充放电功率下限
        designCapacity: 储能容量：储能器soc上限
        minCapacity：储能器soc下限
        lambda_valley: 谷电时段矫正系数
        lambda_flat: 平电时段矫正系数
        lambda_peak: 峰电时段矫正系数
        lambda_top: 尖电时段矫正系数
        """

        # 定义常量
        demandLoad = np.array(df["demandLoad"])
        price = np.array(df["price"])
        lastSoc = 0
        # e_l  =  15  / 96
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

        # 定义变量
        power = cp.Variable(len(df))
        soc = cp.Variable(len(df))

        # 目标函数
        profit = power @ price

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
            f"project: {self.project},  model: {self.model},  node: {self.node}::valley_index: {valley_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::flat_index: {flat_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::peak_index: {peak_index}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::top_index: {top_index}"
        )

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
            # constraints += [soc[i  -  1] >= 15  -  i  *  e_l]

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
        # 按月份分类，1、7、8、12月为一类，其他月份为另一类
        if month == 1 or month == 7 or month == 8 or month == 12:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: month in 1  /  7/8  /  12 month: {month}"
            )
            constraints += [power[52:60] <= 0]
            constraints += [power[92:96] == 0]
        else:
            logger.info(
                f"project: {self.project},  model: {self.model},  node: {self.node}::profit simulation:: month not in 1  /  7/8  /  12 month: {month}"
            )
            if len(flat_index) > 0:
                for j in flat_index:
                    constraints += [power[j] == 0]
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
        power_opt = np.around(power_opt, decimals=3)
        for i in range(len(power_opt)):
            if abs(power_opt[i]) < 0.1:
                power_opt[i] = 0
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::power_opt: {power_opt}"
        )

        df["power_opt"] = power_opt
        df["power_final"] = df["power_opt"].where(
            df["power_opt"] >= 0, df["power_opt"] / systemEfficiency
        )
        df["power_final"] = np.around(df["power_final"], decimals=3)
        power_fast = df["power_final"].to_list()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::power_fast: {power_fast}"
        )

        # 工作日0点到8点谷时慢充
        slowChargeCount = int(
            df.loc[:31, "power_final"].sum() // (model_cfgs["slowChargePower"])
        )
        slowChargeRemainder = np.around(
            df.loc[:31, "power_final"].sum() % (model_cfgs["slowChargePower"]),
            decimals=3,
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::slowChargeCount: {slowChargeCount}"
        )
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::slowChargeRemainder: {slowChargeRemainder}"
        )
        if slowChargeCount < 31:
            df.loc[: slowChargeCount - 1, "power_final"] = model_cfgs["slowChargePower"]
            df.loc[slowChargeCount, "power_final"] = slowChargeRemainder

        # 放电满放修正
        # 按月份分类，1、7、8、12月为一类，其他月份为另一类
        if month == 1 or month == 7 or month == 8 or month == 12:
            # 如果8~11点负荷低，放不完电池的电，则满功率放电，最后15分钟87%放电
            if df.loc[32:43, "power_final"].gt(0).all():
                df.loc[32:42, "power_final"] = designPower
                df.loc[43, "power_final"] = designPower * 0.87
            else:
                count = int(
                    (designCapacity * usableDepth - designPower * 0.25 * 0.87)
                    // (designPower * 0.25)
                )
                remainder = np.around(
                    (designCapacity * usableDepth - designPower * 0.25 * 0.87)
                    % (designPower * 0.25)
                    / 0.25,
                    decimals=3,
                )
                df.loc[43, "power_final"] = designPower * 0.87
                df.loc[42 - count : 42, "power_final"] = designPower
                df.loc[42 - count - 1, "power_final"] = remainder
                df.loc[32 : 42 - count - 2, "power_final"] = 0
            # 15~23点满放，最后15分钟76%放电
            df.loc[60:90, "power_final"] = designPower
            df.loc[91, "power_final"] = designPower * 0.76
        else:
            # 其他月份8~11点满放，最后15分钟87%放电；13~17点满放
            df.loc[32:42, "power_final"] = designPower
            df.loc[43, "power_final"] = designPower * 0.87
            df.loc[52:67, "power_final"] = designPower

        df_future = pd.DataFrame()
        df_future["timeStamp"] = df["timeStamp"]
        df_future["power"] = df["power_final"]
        power_list = df_future["power"].to_list()
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::power_list: {power_list}"
        )

        return df_future

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
        month = int(model_cfgs["time_range"]["month"][-2:])
        logger.info(
            f"project: {self.project},  model: {self.model},  node: {self.node}::month: {month},  type: {type(month)}"
        )
        # 数据处理
        df = self.process_data(input_data, start_time, end_time)
        # 计算
        df_future = self.calculate(df, model_cfgs, month)

        return {"df_future": df_future}
