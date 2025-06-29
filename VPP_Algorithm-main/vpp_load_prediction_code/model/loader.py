import importlib

from model import BaseModelMainClass
from utils.log_util import logger


def model_loader(project, model, node, model_cfg) -> BaseModelMainClass:
    model_name = model_cfg["model"]["name"]
    class_name = "ModelMainClass"

    # 导入相应LLM模块
    try:
        model_module = importlib.import_module(
            f"model.model_packages.{model_name}.main"
        )
    except Exception as e:
        logger.error(f"unknown model named {model_name}! {e}")
        return None
    try:
        model_instance = getattr(model_module, class_name)(
            project, model, node, model_cfg
        )
    except Exception as e:
        logger.error(f"unknown model instance named {class_name}! {e}")
        return None

    return model_instance
