import datetime
import json
import time

import cvxpy as cp
import numpy as np
import pandas as pd
import psycopg2

# import schedule
import requests
from flask import Flask, jsonify, request
from flask_executor import Executor
from log.Log import logger

# 气象数据库连接参数
conn_params_1 = {
    "dbname": "sensor_data_raw",
    "user": "postgres",
    "password": "QTparking123456@",
    "host": "47.122.59.9",
    # "host": "host.docker.internal"
}

# SQL查询表名
table_name_w = "szx_weather"

# 数据库连接参数
conn_params = {
    # "dbname": "damao_vpp_01_00_00_resource_slice_test",  # 测试环境数据库
    # "dbname": "vpp_01_00_00_resourceType_shanghai_fujiansuanlierqi",  # 1443环境
    # "dbname": "vpp_01_00_00_resourceType",  # 演示环境数据库
    "dbname": "vpp_01_04_00_resourceType",  # 现场环境数据库
    "user": "postgres",
    "password": "QTparking123456@",
    # "host": "47.100.89.197" # 测试环境
    # "host": "47.122.59.9"
    "host": "localhost",
}

# SQL查询表名与字段
calendar_table = "demand_calendar"  # 工作日历
table_name_1 = "ai_load_forecasting"
table_name_2 = "cfg_storage_energy_strategy"
# node_id  =  ['e238bb37143b82082f695bb5c9cb438f',  'e4653aad857c96f4c2ea4fd044bffbea',  '07c3c82df1dd93e9c303644eb79985cb'] # 测试环境：node_name  =  '长乐产投大楼',  '产投储能001',  '产投储能002'
node_id = [
    "e238bb37143b82082f695bb5c9cb438f",
    "c20a1ecb5d33539e5334ad85af822252",
    "96a1a8c51194b433025bc8fb677de785",
]  # 演示环境、现场环境：node_name  =  '长乐产投大楼',  '产投储能001',  '产投储能002'
system_id = ["nengyuanzongbiao", "chuneng", "chuneng"]

# def job_1(args):
#     """
#     储能调度策略：通过调接口方式启动，生成明天的策略
#     """
#     # 本地单次测试
#     now  =  datetime.datetime.now()
#     # 测试环境：将UTC时间转换为北京时间
#     # 设置北京时区
#     # beijing_tz =  pytz.timezone('Asia /  Shanghai')
#     # now  =  now.replace(tzinfo  =  pytz.utc).astimezone(beijing_tz).replace(tzinfo  =  None)
#     logger.info(f"changle ai strategy:: now is {now}")
#     start_time  =  (now  +  datetime.timedelta(days  =  1)).replace(hour  = 0,  minute  = 0,  second  = 0,  microsecond  = 0)
#     end_time  =  (now  +  datetime.timedelta(days  =  1)).replace(hour  =  23,  minute  =  48,  second  = 0,  microsecond  = 0)

#     type  =  "changle ai strategy"
#     date,  e_c_opt_1_lst,  e_c_opt_2_lst  =  one_day_strategy(start_time,  end_time,  type)

#     # 调用后端接口输出策略
#     url  =  "http://192.168.110.39:59090 /  system_management  /  energy_model  /  energy_storage_model  /  energyStoragePrediction" # 本地测试环境
#     # url  =  "http://host.docker.internal:39090 /  system_management  /  energy_model  /  energy_storage_model  /  energyStoragePrediction" # docker测试环境
#     # url  =  "http://host.docker.internal:59090 /  system_management  /  energy_model  /  energy_storage_model  /  energyStoragePrediction" # 演示环境
#     # url  =  "http://host.docker.internal:59091  /  system_management  /  energy_model  /  energy_storage_model  /  energyStoragePrediction" # 现场环境
#     # 测试环境
#     data =  {
#         node_id[1]: e_c_opt_1_lst,
#         node_id[2]: e_c_opt_2_lst
#     }
#     headers  =  {'Content  -  Type': 'application  /  json'}
#     response  =  requests.post(url  =  url,  data =  json.dumps(data),  headers  =  headers)
#     logger.info(f"changle ai strategy:: response status_code={response.status_code} text={response.text}")


def job_2(args):
    """
    电力交易策略：通过调接口方式启动，生成后天开始的三天的策略
    """
    task_code = args["task_code"]
    logger.info(f"power trading strategy:: task_code is {task_code}")
    # 本地单次测试
    now = datetime.datetime.now()
    # 测试环境：将UTC时间转换为北京时间
    # 设置北京时区
    # beijing_tz =  pytz.timezone('Asia /  Shanghai')
    # now  =  now.replace(tzinfo  =  pytz.utc).astimezone(beijing_tz).replace(tzinfo  =  None)
    logger.info(f"power trading strategy:: now is {now}")
    start_time_1 = (now + datetime.timedelta(days=2)).replace(
        hour=0, minute=0, second=0, microsecond=0
    )
    end_time_1 = (now + datetime.timedelta(days=2)).replace(
        hour=23, minute=48, second=0, microsecond=0
    )
    start_time_2 = (now + datetime.timedelta(days=3)).replace(
        hour=0, minute=0, second=0, microsecond=0
    )
    end_time_2 = (now + datetime.timedelta(days=3)).replace(
        hour=23, minute=48, second=0, microsecond=0
    )
    start_time_3 = (now + datetime.timedelta(days=4)).replace(
        hour=0, minute=0, second=0, microsecond=0
    )
    end_time_3 = (now + datetime.timedelta(days=4)).replace(
        hour=23, minute=48, second=0, microsecond=0
    )

    type = "power trading strategy"
    date_1, opt_list_1_1, opt_list_1_2 = one_day_strategy(
        start_time_1, end_time_1, type
    )
    date_2, opt_list_2_1, opt_list_2_2 = one_day_strategy(
        start_time_2, end_time_2, type
    )
    date_3, opt_list_3_1, opt_list_3_2 = one_day_strategy(
        start_time_3, end_time_3, type
    )

    # 调用后端接口输出策略
    # url  =  "http://192.168.110.39:59092  /  tradePower  /  energyStrategyPrediction" # 本地测试环境
    # url  =  "http://host.docker.internal:39090 /  tradePower  /  energyStrategyPrediction" # docker测试环境
    # url  =  "http://host.docker.internal:59090 /  tradePower  /  energyStrategyPrediction" # 演示环境
    url = "http://host.docker.internal:59092  /  tradePower  /  energyStrategyPrediction"  # 现场环境
    # 测试环境
    data = {
        "task_code": task_code,
        "strategies": [
            {
                "date": date_1,
                "strategy": [
                    {"nodeId": node_id[1], "list": opt_list_1_1},
                    {"nodeId": node_id[2], "list": opt_list_1_2},
                ],
            },
            {
                "date": date_2,
                "strategy": [
                    {"nodeId": node_id[1], "list": opt_list_2_1},
                    {"nodeId": node_id[2], "list": opt_list_2_2},
                ],
            },
            {
                "date": date_3,
                "strategy": [
                    {"nodeId": node_id[1], "list": opt_list_3_1},
                    {"nodeId": node_id[2], "list": opt_list_3_2},
                ],
            },
        ],
    }
    # logger.info(f"power trading strategy:: response data={data}")
    headers = {"Content  -  Type": "application  /  json"}
    response = requests.post(url=url, data=json.dumps(data), headers=headers)
    logger.info(
        f"power trading strategy:: response status_code={response.status_code} text={response.text}"
    )


def one_day_strategy(start_time, end_time, type):
    date = start_time.strftime("%Y-%m-%d")
    logger.info(f"{type}:: date is {date}")
    start_time_str = start_time.strftime("%Y-%m-%d %H:%M:%S")
    end_time_str = end_time.strftime("%Y-%m-%d %H:%M:%S")
    start_month = start_time.replace(day=1)
    start_month_str = start_month.strftime("%Y-%m-%d")
    logger.info(f"{type}:: start_time is {start_time}")
    logger.info(f"{type}:: start_month is {start_month}")

    # 查询历史天气数据
    # 创建数据库连接
    conn = psycopg2.connect(**conn_params_1)
    # 创建SQL cursor对象
    cur = conn.cursor()

    select_query = """
        SELECT  *  FROM %s WHERE ts BETWEEN '%s' AND '%s' ORDER BY ts;
        """ % (
        table_name_w,
        start_time_str,
        end_time_str,
    )
    cur.execute(select_query)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df_weather = pd.DataFrame(rows, columns=columns)

    # 关闭游标和连接
    cur.close()
    conn.close()
    df_weather["timeStamp"] = pd.to_datetime(df_weather["ts"])
    df_weather.drop_duplicates(
        subset="timeStamp", keep="last", inplace=True, ignore_index=True
    )

    # 整理天气特征
    df_weather = df_weather[["timeStamp", "pred_tt2"]]
    # 删除含空值的行
    df_weather.dropna(inplace=True, ignore_index=True)
    # 将除了timeStamp的列转为float类型
    for col in ["pred_tt2"]:
        df_weather[col] = df_weather[col].apply(lambda x: float(x))
    predTT2 = df_weather["pred_tt2"].mean()
    logger.info(f"{type}:: predTT2: {predTT2}")

    # 创建数据库连接
    conn = psycopg2.connect(**conn_params)
    # 创建SQL cursor对象
    cur = conn.cursor()

    # 查询历史到未来的工作日历
    select_query = """
        SELECT  *  FROM %s WHERE date BETWEEN '%s' AND '%s' ORDER BY date;
        """ % (
        calendar_table,
        start_time_str,
        end_time_str,
    )
    cur.execute(select_query)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df_date = pd.DataFrame(rows, columns=columns)
    # 转换时间戳类型
    df_date["date"] = pd.to_datetime(df_date["date"])
    # 去除重复时间戳
    df_date.drop_duplicates(subset="date", keep="last", inplace=True, ignore_index=True)
    logger.info(f"{type}:: raw df_date \n{df_date}")

    # 查询预测的需求负荷
    select_query_1 = """
        SELECT  *  FROM %s WHERE node_id  =  '%s' AND system_id  =  '%s' AND count_data_time BETWEEN '%s' AND '%s' ORDER BY count_data_time;
        """ % (
        table_name_1,
        node_id[0],
        system_id[0],
        start_time_str,
        end_time_str,
    )
    cur.execute(select_query_1)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df_load = pd.DataFrame(rows, columns=columns)
    # 转换时间戳类型
    df_load["timeStamp"] = pd.to_datetime(df_load["count_data_time"])
    # 去除重复时间戳
    df_load.drop_duplicates(
        subset="timeStamp", keep="last", inplace=True, ignore_index=True
    )

    # 查询当月电价
    select_query_2 = """
        SELECT  *  FROM %s WHERE node_id  =  '%s' AND system_id  =  '%s' AND effective_date BETWEEN '%s' AND '%s' ORDER BY s_time;
        """ % (
        table_name_2,
        node_id[1],
        system_id[0],
        start_month_str,
        start_month_str,
    )
    cur.execute(select_query_2)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df_price = pd.DataFrame(rows, columns=columns)
    # 去除重复时间戳
    df_price.drop_duplicates(
        subset="s_time", keep="last", inplace=True, ignore_index=True
    )
    # 校验电价表长度
    if len(df_price) == 24:
        logger.info(f"{type}:: df_price length is {len(df_price)}")
    else:
        logger.error(f"{type}:: wrong df_price length is {len(df_price)}")

    # 生成每15min电价数组
    price_hour = df_price["price_hour"]
    ele_price = np.array([x for element in price_hour for x in [element] * 4])
    logger.info(f"{type}:: price_hour is \n{price_hour}")

    # 生成每15min峰谷平数组
    property = df_price["property"]
    property_arr = np.array([x for element in property for x in [element] * 4])
    logger.info(f"{type}:: property is \n{property}")

    # 关闭游标和连接
    cur.close()
    conn.close()

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
    logger.info(f"{type}:: df has nan or not\n{df.isna().any()}")
    logger.info(f"{type}:: first df after fill \n{df.iloc[: 10]}")
    logger.info(f"{type}:: last df after fill \n{df.iloc[-10:]}")

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
    logger.info(f"{type}:: before peak time process e_c_opt_1  =  \n{e_c_opt_1}")
    logger.info(f"{type}:: before peak time process e_c_opt_2  =  \n{e_c_opt_2}")
    logger.info(f"{type}:: strategy \n{df.iloc[:20]}")
    logger.info(f"{type}:: strategy \n{df.iloc[20:40]}")
    logger.info(f"{type}:: strategy \n{df.iloc[40:60]}")
    logger.info(f"{type}:: strategy \n{df.iloc[60:80]}")
    logger.info(f"{type}:: strategy \n{df.iloc[80:96]}")

    # 工作日谷时慢充，平时12~15点按80充
    count_40 = int(df.loc[:31, "e_c_opt_1"].sum() // (-40))
    remainder_40 = np.around(df.loc[:31, "e_c_opt_1"].sum() % (-40), decimals=3)
    logger.info(f"{type}:: count_40: {count_40}")
    logger.info(f"{type}:: remainder_40: {remainder_40}")
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
    logger.info(f"{type}:: count_80: {count_80}")
    logger.info(f"{type}:: remainder_80: {remainder_80}")
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
        logger.info(f"{type}:: do peak time processing\n")
        df.loc[60:63, "e_c_opt_1"] = 0
        df.loc[60:63, "e_c_opt_2"] = 0
    logger.info(f"{type}:: after peak time process\n")
    logger.info(f"{type}:: after process strategy \n{df.iloc[:20]}")
    logger.info(f"{type}:: after process strategy \n{df.iloc[20:40]}")
    logger.info(f"{type}:: after process strategy \n{df.iloc[40:60]}")
    logger.info(f"{type}:: after process strategy \n{df.iloc[60:80]}")
    logger.info(f"{type}:: after process strategy \n{df.iloc[80:96]}")

    e_c_opt_1_lst = df["e_c_opt_1"].tolist()
    e_c_opt_2_lst = df["e_c_opt_2"].tolist()
    logger.info(f"{type}:: e_c_opt_1  =  \n{e_c_opt_1_lst}")
    logger.info(f"{type}:: e_c_opt_2  =  \n{e_c_opt_2_lst}")
    return date, e_c_opt_1_lst, e_c_opt_2_lst


app = Flask("powerTradingStrategy")
executor = Executor(app)

# @app.route("/storageAIStrategy",  methods=["POST"])
# def request_process_1():
# input_dict  =  request.get_json()

# current_time  =  time.strftime("%Y-%m-%d %H:%M:%S",  time.localtime())
# logger.info("task accept at "  +  current_time)

# job_param_1  =  {}

# if "node_id" in input_dict.keys():
#     # 测试环境
#     if input_dict["node_id"] == node_id[1] or input_dict["node_id"] == node_id[2]:
#         job_param_1["node_id"]  =  input_dict["node_id"]
#         logger.info(f"job_param_1: {job_param_1}")

#         executor.submit(job_1,  job_param_1)

#         resp  =  {"code": 200,  "msg": "成功"}
#     else:
#         resp  =  {"code": 400,  "msg": "找不到该node_id"}
# else:
#     resp  =  {"code": -1,  "msg": "请输入node_id"}

# logger.info(f"task code and msg is {resp}")

# return jsonify(resp)


@app.route("/powerTradingStrategy", methods=["POST"])
def request_process_2():
    input_dict = request.get_json()
    logger.info(f"input_dict: {input_dict}")

    current_time = time.strftime("%Y-%m-%d %H:%M:%S", time.localtime())
    logger.info("task accept at " + current_time)

    job_param_2 = {}

    if "node_id" in input_dict.keys():
        # 测试环境
        if input_dict["node_id"] == node_id[1] or input_dict["node_id"] == node_id[2]:
            job_param_2["node_id"] = input_dict["node_id"]
            job_param_2["task_code"] = input_dict["task_code"]
            logger.info(f"job_param_2: {job_param_2}")

            executor.submit(job_2, job_param_2)

            resp = {"code": 200, "msg": "成功"}
        else:
            resp = {"code": 400, "msg": "找不到该node_id"}
    else:
        resp = {"code": -1, "msg": "请输入node_id"}

    logger.info(f"task code and msg is {resp}")

    return jsonify(resp)


if __name__ == "__main__":

    # # 定时任务：每天23：50运行程序
    # # logger.info(f"changle ai strategy:: now is {datetime.datetime.now()}")
    # schedule.every().day.at("16:03").do(job_1) # 北京时间23:50，UTC时间15:50

    # while True:
    #     schedule.run_pending()
    #     time.sleep(1)

    # 开启算法接口：电力交易用
    app.run(port=13360, host="0.0.0.0")
