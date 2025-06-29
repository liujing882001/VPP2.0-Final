import os
from typing import Dict

import pandas as pd
from config import RECORD_PATH
from input.input_class.database_read import DataBaseLoader
from model.schemas.model_arg import ModelArg
from utils.log_util import logger

INPUT_PATH  =  f"{RECORD_PATH}/input_data"

input_type_dict  =  {
    "database": DataBaseLoader,
}


def input_loader(project,  node,  model,  model_cfg) -> Dict[str,  pd.DataFrame]:
    input_args  =  ModelArg(**model_cfg).input
    input_data =  {}
    for name,  input_arg in input_args.items():
        input_type  =  input_arg.type
        input_data[name]  =  input_type_dict[input_type](
            project,
            model,
            input_arg.args,
        ).get_data(model_config  =  model_cfg["model"])
    # input_process(project,  node,  model,  input_data,  input_args,  model_cfg)
    return input_data


def input_process(project,  node,  model,  input_data,  input_args,  model_config):
    save_input(project,  node,  model,  input_data,  input_args,  model_config)


def save_input(project,  node,  model,  input_data,  input_args,  model_config):
    now  =  model_config["model"]["time_range"]["now_time"]
    time  =  now.split("  ")[0]  +  "-"  +  now.split("  ")[1].replace(":",  "-")
    file_path  =  f"{INPUT_PATH}/{time}/{project}/{node}/{model}"
    if not os.path.exists(file_path):
        os.makedirs(file_path)
        logger.info(f"create {file_path}")
    for name,  input_arg in input_args.items():
        df: pd.DataFrame  =  input_data[name]
        df.to_csv(f"{file_path}/{name}.csv",  index  =  False)
        logger.info(f"save {file_path}/{name}.csv")


if __name__ == "__main__":
    from config import global_config

    for project,  project_config in global_config.items():
        for model,  model_configs in project_config.items():
            for node,  model_config in model_configs.items():
