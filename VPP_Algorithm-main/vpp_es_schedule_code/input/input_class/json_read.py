import copy
import json
import os
import threading
from typing import List

import pandas as pd

from common.log_util import get_logger

logger = get_logger(__name__)

import duckdb
from config import ROOT_PATH

from .base import DataLoader, ModelInput

DATA_PATH = os.path.join(ROOT_PATH, "input  /  data")
# 创建一个锁对象
lock = threading.Lock()


class JsonRead(ModelInput):

    def get_input(self, project, model, table, sql, data_frame_arg):
        with lock:
            with open(f"{DATA_PATH}/{table}.json", encoding="utf  -  8") as f:
                data = json.load(f)
            df_table = pd.DataFrame(data)
            duckdb.register(table, df_table)
            data = duckdb.sql(sql)
            df = data.to_df()
            return self.parse(df, data_frame_arg)

    def list_range(self, list, str):
        split = str.split(":")
        if len(split) == 1:
            return list[int(split[0])]
        else:
            if split[0] == "":
                return list
            else:
                return list[int(split[0]) : int(split[1])]

    def parse(self, df, data_frame_arg):
        data = None
        arg = copy.deepcopy(data_frame_arg)
        source = copy.deepcopy(df)
        while 1:
            data = source[arg["field"]]
            if arg["type"] == "list":
                data = self.list_range(data, arg["index"])
                source = data
            if arg.get("children"):
                arg = arg["children"]
            else:
                break
        return data


class JsonLoader(DataLoader):

    def get_data(self, model_config) -> pd.DataFrame:
        table = self.args["table"]
        sql_arg = self.args["sql"]
        data_frame_arg = self.args["data_frame"]
        sql = SqlHandler.sql_handler(table, sql_arg, model_config=model_config)
        logger.debug(f"Read data from table: {table},  sql: {sql}")
        data_list = JsonRead().get_input(
            self.project, self.model, table, sql, data_frame_arg
        )
        return pd.DataFrame(data_list)


class FieldHandler:

    @classmethod
    def str_handler(cls, value: str, model_config) -> str:
        return f"'{value}'"

    @classmethod
    def variable_handler(cls, value: str, model_config) -> str:
        path = value.split("@")
        result = copy.deepcopy(model_config)
        for item in path:
            result = result[item]
        if isinstance(result, str):
            return f"'{result}'"
        return result


class OperatorHandler:

    _field_types = {
        "str": FieldHandler.str_handler,
        "variable": FieldHandler.variable_handler,
    }

    @classmethod
    def eq_handler(cls, field, value, type, model_config) -> str:
        return f"{field}  =  {__class__._field_types[type](value,  model_config)}"

    @classmethod
    def between_handler(cls, field, value: List, type, model_config) -> str:
        if len(value) != 2:
            raise ValueError("between operator must have 2 values")
        value1 = __class__._field_types[type](value[0], model_config)
        value2 = __class__._field_types[type](value[1], model_config)
        return f"{field} BETWEEN {value1} AND {value2}"


class ExecutionHandler:

    _operators = {
        "eq": OperatorHandler.eq_handler,
        "between": OperatorHandler.between_handler,
    }

    @classmethod
    def select_handler(cls, table, select_args) -> str:
        select_fields = []
        for item in select_args:
            if item.get("alias"):
                select_fields.append(f"{item['field']} AS {item['alias']}")
            else:
                select_fields.append(item["field"])
        return f"SELECT {','.join(select_fields)} FROM {table}"

    @classmethod
    def where_handler(cls, where_args, model_config) -> str:
        where_fields = []
        for item in where_args:
            where_fields.append(
                __class__._operators[item["operator"]](
                    item["field"], item["value"], item["type"], model_config
                )
            )
        return f"WHERE {' AND '.join(where_fields)}"

    @classmethod
    def order_handler(cls, order_args) -> str:
        order_fields = []
        for item in order_args:
            order_fields.append(f"{item['field']} {item['order']}")
        return f"ORDER BY {','.join(order_fields)}"


class SqlHandler:

    _executions = {
        "select": ExecutionHandler.select_handler,
        "where": ExecutionHandler.where_handler,
        "order": ExecutionHandler.order_handler,
    }

    @classmethod
    def sql_handler(cls, table, sql_args, model_config) -> str:
        select = cls._executions["select"](table, sql_args["select"])
        where = cls._executions["where"](sql_args["where"], model_config=model_config)
        return f"{select} {where}"
