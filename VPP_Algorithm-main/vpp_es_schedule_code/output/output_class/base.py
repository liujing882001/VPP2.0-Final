from abc import ABC, abstractmethod

import pandas as pd


class ModelOutput(ABC):

    @abstractmethod
    def do_output(self, *args, **kwargs):
        pass


class DataSaver(ABC):

    def __init__(self, project, model, args):
        self.project = project
        self.model = model
        self.args = args

    @abstractmethod
    def save_data(self, df: pd.DataFrame) -> None:
        pass
