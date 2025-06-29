import copy
import datetime

import cvxpy as cp
import numpy as np
import pandas as pd

# from utils.log_util import logger
from log.Log import logger

# 历史数据
df_weather = pd.read_csv(
    "D:\\200 coding repository\\VPP  -  es_schedule\\test_data\\df_weather_20241105.csv",
    encoding="utf  -  8",
)
df_date = pd.read_csv(
    "D:\\200 coding repository\\VPP  -  es_schedule\\test_data\\df_date_20241105.csv",
    encoding="utf  -  8",
)
df_load = pd.read_csv(
    "D:\\200 coding repository\\VPP  -  es_schedule\\test_data\\df_load_20241105.csv",
    encoding="utf  -  8",
)
df_price = pd.read_csv(
    "D:\\200 coding repository\\VPP  -  es_schedule\\test_data\\df_price_20241105.csv",
    encoding="utf  -  8",
)
# 输入数据以字典形式整理
input_data = {}
input_data["df_weather"] = df_weather
input_data["df_date"] = df_date
input_data["df_load"] = df_load
input_data["df_price"] = df_price


def preprocess_data(raw_df: pd.DataFrame, column_name: str, new_column_name: str):
    df = copy.deepcopy(raw_df)
    # 转换时间戳类型
    df[new_column_name] = pd.to_datetime(df[column_name])
    # 去除重复时间戳
    df.drop_duplicates(
        subset=new_column_name, keep="last", inplace=True, ignore_index=True
    )

    return df


def process_data(input_data, start_time, end_time):
    # 数据预处理
    df_weather = preprocess_data(input_data["df_weather"], "ts", "timeStamp")
    df_date = preprocess_data(input_data["df_date"], "date", "date")
    df_load = preprocess_data(input_data["df_load"], "count_data_time", "timeStamp")
    df_price = input_data["df_price"]

    # 整理天气特征
    df_weather = df_weather[["timeStamp", "pred_tt2"]]
    # 删除含空值的行
    df_weather.dropna(inplace=True, ignore_index=True)
    # 将除了timeStamp的列转为float类型
    for col in ["pred_tt2"]:
        df_weather[col] = df_weather[col].apply(lambda x: float(x))
    predTT2 = df_weather["pred_tt2"].mean()
    logger.info(f"predTT2: {predTT2}")

    # 校验电价表长度
    if len(df_price) == 24:
        logger.info(f"df_price length is {len(df_price)}")
    else:
        logger.error(f"wrong df_price length is {len(df_price)}")

    # 生成每15min电价数组
    price_hour = df_price["price_hour"]
    ele_price = np.array([x for element in price_hour for x in [element] * 4])
    logger.info(f"price_hour is \n{price_hour}")

    # 生成每15min峰谷平数组
    property = df_price["property"]
    property_arr = np.array([x for element in property for x in [element] * 4])
    logger.info(f"property is \n{property}")

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
    logger.info(f"df has nan or not\n{df.isna().any()}")
    logger.info(f"first df after fill \n{df.iloc[: 10]}")
    logger.info(f"last df after fill \n{df.iloc[-10:]}")
    return df, df_date, predTT2, ele_price


def calculate(df, df_date, ele_price, predTT2):
    """
    d: 需量功率
    e_c：储能充放功率（正为放电，负为充电)
    p：电价
    e_r：前一天剩余电量
    e_l：每个时段损耗电量
    c_l: 充放电功率损失
    e_c_max：充放电功率上限
    e_c_min：充放电功率下限
    e_s_max: 储能器soc上限
    e_s_min：储能器soc下限
    lambda_valley: 谷电时段矫正系数
    lambda_flat: 平电时段矫正系数
    lambda_peak: 峰电时段矫正系数
    lambda_top: 尖电时段矫正系数
    """

    # 定义常量
    d = np.array(df["demandLoad"])
    p = ele_price
    e_r = 0
    e_l = 15 / 96
    c_l = 1  # 为防止逆流，暂设充放电不损失
    e_c_max = 200
    e_c_min = -200
    e_s_max = 430
    e_s_min = 0
    lambda_valley = 0.0001
    lambda_flat = 0.0001
    lambda_peak = -1.5 * lambda_valley
    lambda_top = -1.5 * lambda_valley

    # 定义变量
    e_c = cp.Variable(96)
    soc = cp.Variable(96)

    # 目标函数
    profit = e_c @ p * c_l

    # 谷电矫正项
    profit = profit + lambda_valley * cp.sum(soc[0:32])

    # 平电矫正项
    profit = profit + lambda_flat * cp.sum(soc[32:40])
    profit = profit + lambda_flat * cp.sum(soc[48:60])
    profit = profit + lambda_flat * cp.sum(soc[80:84])
    profit = profit + lambda_flat * cp.sum(soc[88:96])

    # 峰电矫正项
    profit = profit + lambda_peak * cp.sum(soc[40:44])
    profit = profit + lambda_peak * cp.sum(soc[60:68])
    profit = profit + lambda_peak * cp.sum(soc[72:80])
    profit = profit + lambda_peak * cp.sum(soc[84:88])

    # 尖电矫正项
    profit = profit + lambda_top * cp.sum(soc[44:48])
    profit = profit + lambda_top * cp.sum(soc[68:72])

    obj = cp.Maximize(profit)

    # 设置约束条件
    constraints = []
    # 放电功率小于需量
    constraints += [e_c * c_l <= d]
    # 储能系统每个时段的充放电功率限制
    constraints += [e_c <= e_c_max]
    constraints += [e_c >= e_c_min]

    for i in range(1, 97):
        # 充电功率和实时电量匹配
        constraints += [soc[i - 1] == e_r - cp.sum(e_c[:i]) * 0.25 * c_l]
        # 对电量损耗的保底电量限制
        constraints += [soc[i - 1] >= 15 - i * e_l]

    # 储能器容量限制
    constraints += [soc >= e_s_min]
    constraints += [soc <= e_s_max]

    # 丑陋的东西
    constraints += [e_c[0:32] <= 0]
    constraints += [e_c[32:40] <= 0]
    constraints += [e_c[48:60] <= 0]
    constraints += [e_c[80:84] <= 0]
    constraints += [e_c[88:96] <= 0]
    constraints += [e_c[60:80] >= 0]

    prob = cp.Problem(obj, constraints)
    # result  =  prob.solve(verbose  =  False,  solver  =  cp.ECOS)
    result = prob.solve(verbose=False, solver=cp.CLARABEL)

    e_c_opt = e_c.value

    e_c_opt_1 = np.around(e_c_opt / 2, decimals=3)
    e_c_opt_2 = np.around(e_c_opt / 2, decimals=3)

    # 去掉零前面的负号
    for i in range(len(e_c_opt_1)):
        if abs(e_c_opt_1[i]) < 0.0001:
            e_c_opt_1[i] = 0
    for i in range(len(e_c_opt_2)):
        if abs(e_c_opt_2[i]) < 0.0001:
            e_c_opt_2[i] = 0

    df["e_c_opt_1"] = e_c_opt_1
    df["e_c_opt_2"] = e_c_opt_2
    logger.info(f"before peak time process e_c_opt_1  =  \n{e_c_opt_1}")
    logger.info(f"before peak time process e_c_opt_2  =  \n{e_c_opt_2}")
    logger.info(f"strategy \n{df.iloc[:20]}")
    logger.info(f"strategy \n{df.iloc[20:40]}")
    logger.info(f"strategy \n{df.iloc[40:60]}")
    logger.info(f"strategy \n{df.iloc[60:80]}")
    logger.info(f"strategy \n{df.iloc[80:96]}")

    # 工作日谷时慢充，平时12~15点按80充
    count_40 = int(df.loc[:31, "e_c_opt_1"].sum() // (-40))
    remainder_40 = np.around(df.loc[:31, "e_c_opt_1"].sum() % (-40), decimals=3)
    logger.info(f"count_40: {count_40}")
    logger.info(f"remainder_40: {remainder_40}")
    if df_date.loc[0, "date_type"] < 2 and count_40 < 31:
        df.loc[:count_40, "e_c_opt_1"] = -40
        df.loc[count_40 + 1, "e_c_opt_1"] = remainder_40
        df.loc[:count_40, "e_c_opt_2"] = -40
        df.loc[count_40 + 1, "e_c_opt_2"] = remainder_40
    count_80 = int(
        (df.loc[48:59, "e_c_opt_1"].sum() + df.loc[80:83, "e_c_opt_1"].sum()) // (-80)
    )
    remainder_80 = np.around(
        (df.loc[48:59, "e_c_opt_1"].sum() + df.loc[80:83, "e_c_opt_1"].sum()) % (-80),
        decimals=3,
    )
    logger.info(f"count_80: {count_80}")
    logger.info(f"remainder_80: {remainder_80}")
    if df_date.loc[0, "date_type"] < 2 and count_80 < 11:
        df.loc[48 : 48 + count_80, "e_c_opt_1"] = -80
        df.loc[48 + count_80 + 1, "e_c_opt_1"] = remainder_80
        df.loc[48 : 48 + count_80, "e_c_opt_2"] = -80
        df.loc[48 + count_80 + 1, "e_c_opt_2"] = remainder_80
    # 工作日且预测平均2m气温低于301K，晚上20点到21点待机
    if df_date.loc[0, "date_type"] < 2 and predTT2 < 301:
        df.loc[80:83, "e_c_opt_1"] = 0
        df.loc[80:83, "e_c_opt_2"] = 0
    # 如果当前是峰时，强制充放电功率+100kw
    df.loc[df["property"] == "峰", "e_c_opt_1"] = 100
    df.loc[df["property"] == "峰", "e_c_opt_2"] = 100
    # 如果当前是尖时，强制充放电功率+100kw
    df.loc[df["property"] == "尖", "e_c_opt_1"] = 100
    df.loc[df["property"] == "尖", "e_c_opt_2"] = 100
    # 如果是7、8、9月的工作日，且预测平均2m气温高于301K，下午15~16点待机
    if (
        df_date.loc[0, "month"] >= 7
        and df_date.loc[0, "month"] <= 9
        and df_date.loc[0, "date_type"] < 2
        and predTT2 > 301
    ):
        logger.info(f"do peak time processing\n")
        df.loc[60:63, "e_c_opt_1"] = 0
        df.loc[60:63, "e_c_opt_2"] = 0
    logger.info(f"after peak time process\n")
    logger.info(f"after process strategy \n{df.iloc[:20]}")
    logger.info(f"after process strategy \n{df.iloc[20:40]}")
    logger.info(f"after process strategy \n{df.iloc[40:60]}")
    logger.info(f"after process strategy \n{df.iloc[60:80]}")
    logger.info(f"after process strategy \n{df.iloc[80:96]}")

    return df


def run():
    # 获取时间范围
    now = datetime.datetime.now()
    start_time = (now + datetime.timedelta(days=1)).replace(
        hour=0, minute=0, second=0, microsecond=0
    )
    end_time = (now + datetime.timedelta(days=1)).replace(
        hour=23, minute=48, second=0, microsecond=0
    )
    # 数据处理
    df, df_date, predTT2, ele_price = process_data(input_data, start_time, end_time)
    # 计算
    df_future = calculate(df, df_date, ele_price, predTT2)

    return df_future


if __name__ == "__main__":
    df_future = run()
    df_future.to_csv(
        "D:\\200 coding repository\\VPP  -  es_schedule\\test_data\\df_future.csv",
        encoding="utf_8_sig",
        index=False,
    )
