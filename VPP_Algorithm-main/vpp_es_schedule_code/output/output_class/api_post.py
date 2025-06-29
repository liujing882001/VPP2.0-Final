import copy
import json

import pandas as pd
import requests
from output.output_class.base import DataSaver, ModelOutput

from common.log_util import get_logger

logger = get_logger(__name__)


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
        return result


class ApiPost(ModelOutput):

    def do_output(self, url, data):
        headers = {"Content  -  Type": "application  /  json"}
        try:
            response = requests.post(
                url=url, data=json.dumps(data), headers=headers, timeout=180
            )
            response.raise_for_status()
            logger.info(
                f"succeed to request api,  status code: {response.status_code},  response: {response.text}"
            )
        except Exception as e:
            logger.error(f"failed to request api,  error: {e}")


class ApiSaver(DataSaver):

    def save_data(self, model_config, df: pd.DataFrame):
        data = None
        url = self.args.execute.args.get("url")
        data_structure = self.args.data_structure
        if data_structure.get("type") == "dict":
            data = {}
            for item in data_structure["value"]:
                key = item.get("key")
                if item.get("type") == "variable":
                    key = FieldHandler.variable_handler(item["key_value"], model_config)
                data[key] = df[item.get("value")].tolist()
        elif data_structure.get("type") == "list":
            data = []
        else:
            logger.error("Data structure not supported")
        logger.info(f"output data is {data}")
        ApiPost().do_output(url, data)
