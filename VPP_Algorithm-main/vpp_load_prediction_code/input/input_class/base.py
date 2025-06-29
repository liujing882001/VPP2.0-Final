from abc import ABC, abstractmethod
from typing import Dict

import pandas as pd


class ModelInput(ABC):

    @abstractmethod
    def get_input(self, *args, **kwargs):
        pass


class DataLoader(ABC):

    def __init__(self, project, model, args: Dict):
        self.project = project
        self.model = model
        self.args = args

    @abstractmethod
    def get_data(self, *arg, **kwargs) -> pd.DataFrame:
        pass
