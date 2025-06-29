import os
from typing import Callable, Dict

import pandas as pd
from config import RECORD_PATH
from model.schemas.model_arg import ModelArg
from output.output_class.api_post import ApiSaver
from output.output_class.database_write import DataBaseSaver

from common.log_util import get_logger

logger  =  get_logger(__name__)

OUTPUT_PATH  =  f"{RECORD_PATH}/output_data"

output_type_dict  =  {
    "database": DataBaseSaver,
    "api": ApiSaver,
}


def output_loader(project,  node,  model,  model_cfg) -> Dict[str,  Callable]:
    output_args  =  ModelArg(**model_cfg).output
    output_func_dict  =  {}
    for name,  output_arg in output_args.items():
        if output_arg.execute:
            if output_arg.execute.distribute:
                output_type  =  output_arg.execute.type
                output_func_dict[name]  =  output_type_dict[output_type](
                    project,  model,  output_arg
                ).save_data
            else:
                output_func_dict[name]  =  None
    return output_func_dict


def output_callback(func):
    def wrapper(*func_args,  **func_kwargs):
        output_func_dict  =  func_kwargs["output_func"]
        result  =  func(*func_args,  **func_kwargs)
        logger.info(f"result is {result}")
        model_config  =  func_kwargs["model_cfgs"]
        for name,  output_func in output_func_dict.items():
            output_func(model_config,  result[name])
            logger.info(f"output {name} done")
        return result

    return wrapper


def output_process(project,  node,  model,  result,  output_func_dict,  model_config):
    save_result(project,  node,  model,  result,  output_func_dict,  model_config)


def save_result(project,  node,  model,  result,  output_func_dict,  model_config):
    today  =  model_config["model"]["time_range"]["now_time"]
    today  =  today[:10]
    file_path  =  f"{OUTPUT_PATH}/{today}/{project}/{node}/{model}"
    if not os.path.exists(file_path):
        os.makedirs(file_path)
        logger.info(f"create {file_path}")
    for name in output_func_dict.keys():
        df: pd.DataFrame  =  result[name]
        df.to_csv(f"{file_path}/{name}.csv",  index  =  False)
        logger.info(f"save {file_path}/{name}.csv")


if __name__ == "__main__":
    from config import global_config

    for project,  project_config in global_config.items():
        for model,  model_configs in project_config.items():
            for node,  model_config in model_configs.items():
