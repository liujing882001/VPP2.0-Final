import copy
from typing import List

import pandas as pd

from common.log_util import get_logger

logger = get_logger(__name__)
# from utils.database.core import get_session

from .base import DataLoader, ModelInput


class DatabaseRead(ModelInput):

    def get_input(self, project, model, database, sql):
        # with get_session(project,  model,  database) as session:
        #     rows  =  session.execute(text(sql)).fetchall()
        #     if len(rows) == 0:
        #         raise ValueError("No data found")
        #     colunms  =  rows[0]._mapping.keys()
        # return rows,  colunms
        # 临时代码：返回空结果，保证主程序可启动
        return [], []


class DatabaseLoader(DataLoader):

    def get_data(self, model_config) -> pd.DataFrame:
        # database  =  self.args["database"]
        # table  =  self.args["table"]
        # sql_arg  =  self.args["sql"]
        # sql  =  SqlHandler.sql_handler(table,  sql_arg,  model_config  =  model_config)
        # logger.debug(f"Read data from table: {table},  sql: {sql}")
        # rows,  columns  =  DatabaseRead().get_input(self.project,  self.model,  database,  sql)
        # if not rows or not columns:
        #     return pd.DataFrame()
        # else:
        #     return pd.DataFrame(rows,  columns  =  list(columns))
        # 临时代码：直接返回空DataFrame，保证主程序可启动
        return pd.DataFrame()


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
        order = cls._executions["order"](sql_args["order"])
        return f"{select} {where} {order}"
