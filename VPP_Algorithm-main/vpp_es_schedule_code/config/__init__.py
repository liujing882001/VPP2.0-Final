import os

import yaml

from common.log_util import get_logger

logger  =  get_logger(__name__)

env  =  os.getenv("ENV",  "local")

ROOT_PATH  =  os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CONFIG_PATH  =  os.path.join(ROOT_PATH,  f"config  /  vpp  -  es_schedule  -  cfgs/{env}")
RECORD_PATH  =  os.path.join(ROOT_PATH,  "record/")


def load_nodes():
    try:
        with open(f"{CONFIG_PATH}/env_cfg.yaml",  encoding="utf  -  8") as f:
            env_config  =  yaml.safe_load(f)
            logger.info(f"Loading env-{env} env_cfg")
        project_config  =  {}
        # 找到node_cfgs文件夹下的所有文件夹
        total_nodes  =  os.listdir(f"{CONFIG_PATH}/node_cfgs")

        # 列出所有项目
        if env_config["nodes"].get("include"):
            for project,  nodes in env_config["nodes"]["include"].items():
                enabled_nodes  =  project_config.setdefault(project,  [])
                # 根据env_cfg中的include和exclude字段来决定加载哪些node_cfg
                for node in nodes:
                    if node in total_nodes:
                        enabled_nodes.append(node)
                    else:
                        raise Exception(f"Project {node} not found")
        else:
            for project,  nodes in env_config["nodes"]["include"].items():
                enabled_nodes  =  project_config.setdefault(project,  total_nodes)
        logger.info(f"Loading project-{project} nodes: {enabled_nodes}")
        return project_config

    except Exception as e:
        raise Exception(f"Error loading {env} env_cfg: {str(e)}")


def load_models(node):
    try:
        node_config  =  {}
        models_config  =  {}
        enabled_models  =  []
        with open(
            f"{CONFIG_PATH}/node_cfgs/{node}/node_cfg.yaml",  encoding="utf  -  8"
        ) as f:
            node_config  =  yaml.safe_load(f)
            logger.info(f"Loading node-{node} node_cfg")

        # 找到model_cfgs文件夹下的所有文件
        models  =  [
            file.removesuffix(".yaml")
            for file in os.listdir(f"{CONFIG_PATH}/node_cfgs/{node}/model_cfgs")
        ]

        # 根据node_cfg中的include和exclude字段来决定加载哪些model_cfg
        if node_config["models"].get("include"):
            for model in node_config["models"]["include"]:
                if model in models:
                    enabled_models.append(model)
                else:
                    raise Exception(f"model {model} not found")
        elif node_config["models"].get("exclude"):
            for model in models:
                if model not in node_config["models"]["exclude"]:
                    enabled_models.append({model: None})
        else:
            enabled_models  =  models

        # 加载model_cfg
        for model in enabled_models:
            with open(
                f"{CONFIG_PATH}/node_cfgs/{node}/model_cfgs/{model}.yaml",
                encoding="utf  -  8",
            ) as f:
                nodes_config  =  yaml.safe_load(f)
                models_config[model]  =  nodes_config
                logger.info(f"Loading node-{node} model-{model} config")
        return models_config
    except Exception as e:
        raise Exception(f"Error loading {node} node_cfg: {str(e)}")


global_config  =  {}

projects  =  load_nodes()
for project,  nodes in projects.items():
    nodes_config  =  {}
    for node in nodes:
        node_config  =  {}
        models  =  load_models(node)
        nodes_config.setdefault(node,  models)
    global_config.setdefault(project,  nodes_config)

if __name__ == "__main__":
    logger.info(global_config)
    databases  =  global_config["changle"]["changle"]["demand_load"]["databases"]
    for database in databases:
        if database["name"] == "weather":
