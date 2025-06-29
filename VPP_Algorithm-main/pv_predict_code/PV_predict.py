import datetime
import math
import random
import traceback

import numpy as np
import pandas as pd
import psycopg2
import pytz

# import lightgbm as lgb
import xgboost as xgb
from Controler.RunningControlTimer import RunningControl
from log.Log import logger
from scipy.stats import pearsonr
from sklearn.metrics import mean_absolute_error, mean_squared_error, r2_score

# 气象数据库连接参数
conn_params_1 = {
    "dbname": "sensor_data_raw",
    "user": "postgres",
    "password": "QTparking123456@",
    "host": "47.122.59.9",  # 演示环境
    # "host": "host.docker.internal"
}

# SQL查询表名
table_name_1 = "szx_weather"

# 光伏数据库连接参数
conn_params_2 = {
    # "dbname": "damao_vpp_01_00_00_resource_slice_test",  # 测试环境数据库
    # "dbname": "vpp_01_00_00_resourceType",  # 演示环境数据库
    "dbname": "vpp_01_04_00_resourceType",  # 现场环境数据库
    "user": "postgres",
    "password": "QTparking123456@",
    # "host": "47.100.89.197" # 测试环境
    # "host": "47.122.59.9" # 演示环境
    "host": "localhost",
}

# SQL查询表名
table_name_2 = "iot_ts_kv_metering_device_96"
node_id = "bb05b2b6d467846b9ea2b68de14c6f70"  # 测试环境、现场环境
# node_id  =  '5cfd76f998dbcf8d7e214187d0e30ac5' # 演示环境
system_id = "nengyuanzongbiao"
point_desc = "load"

insert_table_name = "ai_load_forecasting"


def task_run():
    logger.info("changle test env:: task start.")
    try:
        # 控制任务定时启动
        control = RunningControl(interval_minute=15)
        while True:
            try:
                current_time = control.get_runtime()
                load_predict(current_time)
            except Exception:
                logger.error("changle test env:: task run error.")
                traceback.print_exc()
    except Exception:
        logger.error("changle test env:: task interrupt.")
        traceback.print_exc()


def load_predict(now: datetime.datetime):
    logger.info(f"changle test env:: task start time now is {now}")
    # 本地单次、定时任务测试
    # beijing_now  =  now
    # 测试环境：将UTC时间转换为北京时间
    beijing_now = now.replace(tzinfo=pytz.utc).astimezone(
        pytz.timezone("Asia /  Shanghai")
    )
    now_time = beijing_now.replace(tzinfo=None, minute=0, second=0, microsecond=0)
    logger.info(
        f"changle test env:: task start time at beijing timezone is {beijing_now}"
    )
    logger.info(f"changle test env:: task start time without timezone is {now_time}")
    # 取历史22天数据，预测未来3天数据
    start_time = now_time.replace(hour=0) - datetime.timedelta(days=23)
    future_time = now_time + datetime.timedelta(days=5)
    start_time_str = start_time.strftime("%Y/%m/%d  %H:%M:%S")
    end_time_str = now_time.strftime("%Y/%m/%d  %H:%M:%S")
    future_time_str = future_time.strftime("%Y/%m/%d  %H:%M:%S")
    logger.info(f"changle test env:: start_time_str is {start_time_str}")
    logger.info(f"changle test env:: end_time_str is {end_time_str}")
    logger.info(f"changle test env:: future_time_str is {future_time_str}")

    # 查询光伏数据
    # 创建数据库连接
    conn = psycopg2.connect(**conn_params_2)
    # 创建SQL cursor对象
    cur = conn.cursor()
    select_query = """
        SELECT  *  FROM %s WHERE node_id  =  '%s' AND system_id  =  '%s' AND point_desc  =  '%s' AND count_data_time BETWEEN '%s' AND '%s' ORDER BY count_data_time;
        """ % (
        table_name_2,
        node_id,
        system_id,
        point_desc,
        start_time_str,
        end_time_str,
    )
    cur.execute(select_query)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df = pd.DataFrame(rows, columns=columns)
    # 关闭游标和连接
    cur.close()
    conn.close()
    # 转换时间戳类型
    df["count_data_time"] = pd.to_datetime(df["count_data_time"])
    # 去除重复时间戳
    df.drop_duplicates(
        subset="count_data_time", keep="last", inplace=True, ignore_index=True
    )

    # 打印查询到的光伏数据最后一条的时刻
    last_df_time = df.loc[len(df) - 1, "count_data_time"]
    logger.info(f"changle test env:: last df count_data_time: {last_df_time}")
    # 计算每天的日出日落时刻
    sunriseTime = []
    sunsetTime = []
    sunriseTime.append(df.loc[0, "count_data_time"])
    for i in range(1, len(df)):
        if (
            df.loc[i, "count_data_time"].year > df.loc[i - 1, "count_data_time"].year
            or df.loc[i, "count_data_time"].month
            > df.loc[i - 1, "count_data_time"].month
            or df.loc[i, "count_data_time"].day > df.loc[i - 1, "count_data_time"].day
        ):
            sunriseTime.append(df.loc[i, "count_data_time"])
            sunsetTime.append(df.loc[i - 1, "count_data_time"])
    # 如果df的最后一条数据的时间戳小于当前时刻减15分钟，说明最后一条数据是日落时刻的
    if df.loc[len(df) - 1, "count_data_time"] < now_time - datetime.timedelta(
        minutes=16
    ):
        sunsetTime.append(df.loc[len(df) - 1, "count_data_time"])
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
        df.set_index("count_data_time")["h_total_use"]
    )
    df_load["PVLoad"] = df_load["PVLoad"].apply(lambda x: float(x))
    logger.info(f"changle test env:: df_load length: {len(df_load)}")

    # 处理日落到日出之间的空值，填充为0
    # 第一个日出之前的光伏功率填充为0
    df_load.loc[df_load["timeStamp"] < sunriseTime[0], "PVLoad"] = 0
    # 循环处理前一天日落到后一天日出之前的数据填充为0
    for i in range(len(sunriseTime) - 1):
        df_load.loc[
            df_load["timeStamp"].between(sunsetTime[i], sunriseTime[i + 1]), "PVLoad"
        ] = 0
    # 最后一个日落时刻到当前时刻填充为0
    if len(sunsetTime) == len(sunriseTime):
        df_load.loc[df_load["timeStamp"] > sunsetTime[-1], "PVLoad"] = 0

    # 删除含空值的行
    df_load.dropna(inplace=True, ignore_index=True)
    logger.info(
        f"changle test env:: df_load length after PV map and drop: {len(df_load)}"
    )

    # 查询历史天气数据
    # 创建数据库连接
    conn = psycopg2.connect(**conn_params_1)
    # 创建SQL cursor对象
    cur = conn.cursor()
    select_query = """
        SELECT  *  FROM %s WHERE ts BETWEEN '%s' AND '%s' ORDER BY ts;
        """ % (
        table_name_1,
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

    # 合并光伏数据和天气数据
    df_load = pd.merge(df_load, df_weather, on="timeStamp", how="left")
    # 插值填充缺失值
    df_load = df_load.interpolate()
    df_load.dropna(inplace=True, ignore_index=True)
    logger.info(
        f"changle test env:: df_load length after merge PV and weather then drop: {len(df_load)}"
    )

    # 选取训练用的天气特征
    data_X = df_load[["rt_ssr", "rt_ws10", "rt_tt2", "cal_rh"]]
    data_Y = df_load["PVLoad"]

    # 训练集、测试集划分
    split_length = 96
    X_train = data_X.iloc[:-split_length]
    Y_train = data_Y.iloc[:-split_length]
    X_test = data_X.iloc[-split_length:]
    Y_test = data_Y.iloc[-split_length:]

    train_xgb = xgb.DMatrix(X_train, label=Y_train)
    test_xgb = xgb.DMatrix(X_test, label=Y_test)

    """定义xgb模型参数"""
    xgb_params = {
        "booster": "gbtree",  # 弱评估器
        "objective": "reg:squarederror",
        "verbosity": 2,  # 打印消息的详细程度
        "tree_method": "auto",  # 这是xgb框架自带的训练方法，可选参数为[auto,  exact,  approx,  hist,  gpu_hist]
        "eval_metric": "rmse",  # 评价指标
        "max_depth": 6,  # 树的深度，默认为6，一般取值[3,  10] 越大偏差越小，方差越大，需综合考虑时间及拟合性。
        "min_child_weight": 3,  # 分裂叶子节点中样本权重和的最小值，如果新分裂的节点的样本权重和小于min_child_weight则停止分裂，默认为1，取值范围[0,],当值越大时，越容易欠拟合，当值越小时，越容易过拟合。
        "gamma": 0,  # 别名min_split_loss 制定节点分裂所需的最小损失很熟下降值,节点分裂时，只有损失函数的减小值大于等于gamma，节点才会分裂，gamma越大，算法越保守，取值范围为[0,] 【0,  1,  5】
        "subsample": 0.8,  # 训练每棵树时子采样比例，默认为1，一般在[0.5,  1]之间，调节该参数可以防止过拟合。
        "colsample_bytree": 0.7,  # 训练每棵树时，使用特征占全部特征的比例，默认为1，典型值为[0.5,  1]，调节该参数可以防止过拟合
        "alpha": 1,  # 别名reg_alpha，L1正则化，在高维度的情况下，调节该参数可以加快算法的速度，增大该值将是模型更保守，一般我们做特征选择的时候会用L1正则项，
        "lambda": 2,  # L2正则化，调节、、增大该参数可以减少过拟合，默认值为1
        "eta": 0.3,  # 别名learning_rate 学习率一般越小越好，只是耗时会更长
        # 'n_estimators':500,  #基学习器的个数，越大越好，偏差越小，但耗时会增加
        # 'max_delat_step':2,  #限制每棵树权重改变的最大步长，默认值为0，及没有约束，如果为正值，则这个算法更加保守，通常不需要设置该参数，但是当样本十分不平衡时，对逻辑回归有帮助。
        "nthread": -1,  # 有多少处理器可以使用，默认为1，-1表示没有限制。
        # 'silent': 1,  #默认为0，不输出中间过程，=1，输出中间过程
        "seed": 2023,  # 随机种子
        # 'is_unbalance':True
    }

    """训练模型"""
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

    logger.info(f"PV predict:: r2:{r2_score(Y_test,  Y_predicted)}")
    logger.info(f"PV predict:: mse:{mean_squared_error(Y_test,  Y_predicted)}")
    logger.info(f"PV predict:: mae:{mean_absolute_error(Y_test,  Y_predicted)}")
    correlation, p_value = pearsonr(Y_test, Y_predicted)
    logger.info(f"PV predict:: correlation:{correlation}")

    # 查询未来天气数据
    # 创建数据库连接
    conn = psycopg2.connect(**conn_params_1)
    # 创建SQL cursor对象
    cur = conn.cursor()
    select_query = """
        SELECT  *  FROM %s WHERE ts BETWEEN '%s' AND '%s' ORDER BY ts;
        """ % (
        table_name_1,
        end_time_str,
        future_time_str,
    )
    cur.execute(select_query)
    # 获取查询结果
    rows = cur.fetchall()
    # 列名称
    columns = [desc[0] for desc in cur.description]
    # 将数据转换为DataFrame
    df_weather_future = pd.DataFrame(rows, columns=columns)
    # 关闭游标和连接
    cur.close()
    conn.close()
    df_weather_future["timeStamp"] = pd.to_datetime(df_weather_future["ts"])
    df_weather_future.drop_duplicates(
        subset="timeStamp", keep="last", inplace=True, ignore_index=True
    )

    # 生成未来3天以15分钟间隔的时间序列
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

    df_future = df_future.interpolate()
    df_future.dropna(inplace=True, ignore_index=True)

    data_X_future = df_future[["rt_ssr", "rt_ws10", "rt_tt2", "cal_rh"]]
    future_xgb = xgb.DMatrix(data_X_future)
    Y_future = xgb_model.predict(future_xgb)
    df_future["load"] = Y_future
    # 如果预测光照强度小于1，将光伏发电预测值置为0
    df_future.loc[df_future["rt_ssr"] < 1, "load"] = 0
    df_future.loc[df_future["load"] < 0.5, "load"] = 0

    # 更新光伏预测数据
    # 创建数据库连接
    conn = psycopg2.connect(**conn_params_2)
    # 创建SQL cursor对象
    cur = conn.cursor()
    for i in range(len(df_future)):
        id = (
            node_id
            + "_"
            + system_id
            + "_"
            + df_future.iloc[i]["timeStamp"].strftime("%Y%m%d%H%M%S")
        )
        predict_value = str(df_future.iloc[i]["load"])
        predict_adjustable_amount = str(
            df_future.iloc[i]["load"] * random.uniform(0.05, 0.1)
        )
        count_data_time = df_future.iloc[i]["timeStamp"].strftime(
            "%Y-%m-%d %H:%M:%S.%f"
        )[
            :-3
        ]  # 保留毫秒并精确到前3位

        insert_query = """
            INSERT INTO %s (id,  node_id,  system_id,  predict_value,  predict_adjustable_amount,  count_data_time)
            VALUES ('%s',  '%s',  '%s',  '%s',  '%s',  to_timestamp('%s','YYYY  -  MM  -  DD HH24:MI:SS.MS'))
            ON CONFLICT (id) DO UPDATE
            SET
            predict_value  =  EXCLUDED.predict_value,
            predict_adjustable_amount  =  EXCLUDED.predict_adjustable_amount;
            """ % (
            insert_table_name,
            id,
            node_id,
            system_id,
            predict_value,
            predict_adjustable_amount,
            count_data_time,
        )
        cur.execute(insert_query)
        # logger.info(f"changle test env:: The {i} th successful execution of SQL update data")
    # 提交修改
    conn.commit()
    # 关闭游标和连接
    cur.close()
    conn.close()


if __name__ == "__main__":
    # 定时任务
    task_run()
    # 本地单次测试
    # test_time  =  datetime.datetime.now(pytz.timezone('Asia /  Shanghai'))
    # load_predict(test_time)
