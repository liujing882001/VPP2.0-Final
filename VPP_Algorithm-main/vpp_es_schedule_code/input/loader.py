import os
from typing import Dict

import pandas as pd

# from utils.tools import retry
from config import RECORD_PATH
from input.input_class.database_read import DatabaseLoader
from input.input_class.json_read import JsonLoader
from input.parser import DataCombineHandler, ElePriceHanlder
from model.schemas.model_arg import ModelArg

from common.log_util import get_logger

logger  =  get_logger(__name__)

INPUT_PATH  =  f"{RECORD_PATH}/input_data"


input_type_dict  =  {
    "database": DatabaseLoader,
    "json": JsonLoader,
}


def input_loader(project,  node,  model,  model_cfg) -> Dict[str,  pd.DataFrame]:
    input_args  =  ModelArg(**model_cfg).input
    input_data =  {}
    for name,  input_arg in input_args.items():
        input_type  =  input_arg.type
        data =  get_data(project,  model,  input_type,  input_arg,  model_cfg)
        if data is None:
            data =  pd.DataFrame()
        if input_arg.handler:
            if input_type == "combine":
                data =  DataCombineHandler.data_combine_handler(
                    input_arg.args,  input_data if input_data is not None else {}
                )
            else:
                data =  ElePriceHanlder.ele_price_handler(model_cfg,  data)
            logger.info(f"Data processed: {data}")
        input_data[name]  =  data
    input_process(project,  node,  model,  input_data,  input_args)
    return input_data


def input_process(project,  node,  model,  input_data,  input_args):
    save_input(project,  node,  model,  input_data,  input_args)


def save_input(project,  node,  model,  input_data,  input_args):
    today  =  pd.Timestamp.now().strftime("%Y%m%d")
    file_path  =  f"{INPUT_PATH}/{today}/{project}/{node}/{model}"
    if not os.path.exists(file_path):
        os.makedirs(file_path)
        logger.info(f"create {file_path}")
    for name,  input_arg in input_args.items():
        df: pd.DataFrame  =  input_data[name]
        df.to_csv(f"{file_path}/{name}.csv",  index  =  False)
        logger.info(f"save {file_path}/{name}.csv")


# @retry(3,  10)
def get_data(project,  model,  input_type,  input_arg,  model_cfg):
    if input_type not in input_type_dict.keys():
        logger.info(f"unknown input type named {input_type}!")
        return None
    data =  input_type_dict[input_type](
        project,
        model,
        input_arg.args,
    ).get_data(model_config  =  model_cfg["model"])
    return data


if __name__ == "__main__":
    from config import global_config

    for project,  project_config in global_config.items():
        for model,  model_configs in project_config.items():
            for node,  model_config in model_configs.items():
