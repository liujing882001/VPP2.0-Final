import os
from typing import Callable, Dict

import pandas as pd
from config import RECORD_PATH
from model.schemas.model_arg import ModelArg
from output.output_class.database_write import DataBaseSaver
from utils.log_util import logger

OUTPUT_PATH  =  f"{RECORD_PATH}/output_data"

output_type_dict  =  {
    "database": DataBaseSaver,
}


def output_loader(project,  node,  model,  model_cfg) -> Dict[str,  Callable]:
    output_args  =  ModelArg(**model_cfg).output
    output_func_dict  =  {}
    for name,  output_arg in output_args.items():
        output_type  =  output_arg.type
        output_func_dict[name]  =  output_type_dict[output_type](
            project,  model,  output_arg.args
        ).save_data
    return output_func_dict


def output_process(project,  node,  model,  result,  output_func_dict,  model_config):
    save_result(project,  node,  model,  result,  output_func_dict,  model_config)


def save_result(project,  node,  model,  result,  output_func_dict,  model_config):
    now  =  model_config["model"]["time_range"]["now_time"]
    time  =  now.split("  ")[0]  +  "-"  +  now.split("  ")[1].replace(":",  "-")
    file_path  =  f"{OUTPUT_PATH}/{time}/{project}/{node}/{model}"
    if not os.path.exists(file_path):
        os.makedirs(file_path)
        logger.info(f"create {file_path}")
    for name in output_func_dict.keys():
        df: pd.DataFrame  =  result[name]
        df.to_csv(f"{file_path}/{name}.csv",  index  =  False)
        logger.info(f"save {file_path}/{name}.csv")


def output_callback(func):
    def wrapper(*func_args,  **func_kwargs):
        output_func_dict  =  func_kwargs["output_func"]
        result  =  func(*func_args,  **func_kwargs)
        logger.info(f"result is {result}")
        for name,  output_func in output_func_dict.items():
            output_func(result[name])
            logger.info(f"output {name} done")
        return result

    return wrapper


if __name__ == "__main__":
    from config import global_config

    for project,  project_config in global_config.items():
        for model,  model_configs in project_config.items():
            for node,  model_config in model_configs.items():
