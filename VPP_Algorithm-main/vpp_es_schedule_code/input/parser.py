import datetime
from typing import Dict

import pandas as pd


class ElePriceHanlder:

    @classmethod
    def ele_price_handler(cls, model_config: Dict, df: pd.DataFrame):
        data_list = []
        date_format = "%Y-%m-%d %H:%M:%S"
        start_time_str = model_config["model"]["time_range"]["start_time"]
        end_time_str = model_config["model"]["time_range"]["end_time"]
        start_time = datetime.datetime.strptime(start_time_str, date_format)
        end_time = datetime.datetime.strptime(end_time_str, date_format)
        # 从start_time到end_time,  15min一个间隔遍历
        while start_time < end_time:
            # 整点的数据
            df_hour = df[
                df["s_time"] == datetime.datetime.strftime(start_time, "%H:00")
            ]
            df_hour_dict = df_hour.to_dict(orient="records")[0]
            data_list.append(
                {
                    "time": start_time,
                    "property": df_hour_dict["property"],
                    "price": (
                        df_hour_dict.get("price")
                        if df_hour_dict.get("price")
                        else df_hour_dict.get("price_hour")
                    ),
                    "price_hour": (
                        df_hour_dict.get("price")
                        if df_hour_dict.get("price")
                        else df_hour_dict.get("price_hour")
                    ),
                }
            )
            start_time += datetime.timedelta(minutes=15)
        return pd.DataFrame(data_list)


class DataCombineHandler:

    @classmethod
    def data_combine_handler(self, config: Dict, data: Dict[str, pd.DataFrame]):
        df_name_list = config["dataframes"]
        dfs = [data[name] for name in df_name_list]
        on_field = config["on_field"]
        op_field = config["op_field"]
        merged_df = pd.merge(
            dfs[0], dfs[1], on=on_field, how="left", suffixes=("_df1", "_df2")
        )
        # 将字符串列转换为float
        merged_df[op_field + "_df1"] = pd.to_numeric(
            merged_df[op_field + "_df1"], errors="coerce"
        )
        merged_df[op_field + "_df2"] = pd.to_numeric(
            merged_df[op_field + "_df2"], errors="coerce"
        )

        new_field = f"{op_field}_sum"
        merged_df[new_field] = (
            merged_df[op_field + "_df1"] + merged_df[op_field + "_df2"]
        )
        df = pd.merge(
            dfs[0], merged_df[[new_field, on_field]], on=[on_field], how="left"
        )
        df.drop(columns=[op_field], inplace=True)
        df.rename(columns={new_field: op_field}, inplace=True)
        return df
