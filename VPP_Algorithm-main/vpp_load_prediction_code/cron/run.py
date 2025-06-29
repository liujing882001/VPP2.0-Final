from config.parser import ConfigParser
from input.loader import input_loader
from model.loader import model_loader
from output.loader import output_loader


def run_job(project, model, node, model_config):
    model_instance = model_loader(
        project=project, model=model, node=node, model_cfg=model_config
    )
    model_config = ConfigParser(project, model, model_config).parse_config()
    input_data = input_loader(
        project=project, node=node, model=model, model_cfg=model_config
    )
    output_func = output_loader(project=project, model=model, model_cfg=model_config)
    model_instance.run(
        input_data=input_data, model_cfgs=model_config["model"], output_func=output_func
    )
