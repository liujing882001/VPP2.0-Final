from typing import Dict, List, Literal

from pydantic import BaseModel


class InputArg(BaseModel):
    type: Literal["database", "json", "combine"]
    args: Dict | None = None
    handler: bool | None = None


class ExecuteArg(BaseModel):
    distribute: bool
    type: Literal["database", "api"]
    args: Dict | None = None


class OutputArg(BaseModel):
    data_structure: Dict
    execute: ExecuteArg | None = None


class DatabaseArg(BaseModel):
    name: str
    driver: str
    host: str
    port: int
    user: str
    password: str
    database: str


class ModelArg(BaseModel):
    model: Dict
    input: Dict[str, InputArg]
    output: Dict[str, OutputArg] = {}
    databases: List[DatabaseArg] | None = None
    cron: Dict | None = None
