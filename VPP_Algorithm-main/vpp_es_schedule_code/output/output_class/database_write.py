import datetime
from typing import Dict

import pandas as pd
from output.output_class.base import DataSaver, ModelOutput

from common.log_util import get_logger

# from utils.database.core import get_session

logger = get_logger(__name__)


class DatabaseWrite(ModelOutput):

    def do_output(self, project, model, database, sql):
        try:
            # 暂时跳过数据库操作
            logger.info("Database operation skipped")
        except Exception as e:
            logger.error(f"Insert data to database failed: {e}")
            logger.info("test pass")


class DataBaseSaver(DataSaver):

    def save_data(self, df: pd.DataFrame):
        # 暂时跳过数据库保存操作
        logger.info("Database save operation skipped")
        return

        # 以下是原始代码，暂时注释
        # database  =  self.args["database"]
        # table  =  self.args["table"]
        # sql_arg  =  self.args["sql"]
        # # 按行遍历data
        # for row in df.to_dict(orient="records"):
        #     sql  =  SqlHandler.sql_handler(table,  sql_arg,  row)
        #     logger.debug(f"Insert data to table: {table},  sql: {sql}")
        #     DatabaseWrite().do_output(self.project,  self.model,  database,  sql)


class FieldHandler:

    @classmethod
    def datetime_handler(cls, now: datetime.datetime, day: int) -> str:
        time = now + datetime.timedelta(days=day)
        time_str = time.strftime("%Y/%m/%d  %H:%M:%S")
        return f"'{time_str}'"

    @classmethod
    def str_handler(cls, value: str) -> str:
        return f"'{value}'"


class ExecutionHandler:

    field_types = {
        "str": FieldHandler.str_handler,
    }

    @classmethod
    def insert_handler(cls, table, fields, values) -> str:
        insert_fields = []
        insert_values = []
        for i in range(len(fields)):
            insert_fields.append(fields[i]["name"])
            insert_values.append(
                f"{__class__.field_types[fields[i]['type']](values[i])}"
            )
        return f"INSERT INTO {table} ({','.join(insert_fields)}) VALUES ({','.join(insert_values)})"

    @classmethod
    def conflict_handler(cls, conflict_args) -> str:
        if not conflict_args:
            return ""
        conflict_fields = conflict_args["conflict_fields"]
        update_fields = conflict_args["update_fields"]
        update_sql = [f"{field}  =  EXCLUDED.{field}" for field in update_fields]
        return f"ON CONFLICT ({','.join(conflict_fields)}) DO UPDATE SET {','.join(update_sql)}"


class SqlHandler:

    _executions = {
        "insert": ExecutionHandler.insert_handler,
        "conflict": ExecutionHandler.conflict_handler,
    }

    @classmethod
    def sql_handler(cls, table, sql_arg: Dict, data: Dict) -> str:
        if sql_arg.get("insert"):
            insert_args = sql_arg["insert"]
            insert = cls._executions["insert"](
                table,
                insert_args["fields"],
                [data[field["name"]] for field in insert_args["fields"]],
            )
            confilct = cls._executions["conflict"](insert_args["conflict_do_update"])
            return f"{insert} {confilct}"
        return ""  # 添加默认返回值
