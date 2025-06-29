import functools
import re
from contextlib import contextmanager

from config import global_config
from pydantic_core import InitErrorDetails, ValidationError
from sqlalchemy import create_engine, inspect
from sqlalchemy.orm import Session, declarative_base, declared_attr, sessionmaker
from utils.exceptions import NotFoundError
from utils.log_util import logger

# from fastapi import Depends
# from starlette.requests import Request


engine_dict = {}

for project, project_config in global_config.items():
    project_dict = engine_dict.setdefault(project, {})
    for model, model_configs in project_config.items():
        # 取第一个节点的数据库配置 通用
        model_config = model_configs[list(model_configs.keys())[0]]
        model_dict = project_dict.setdefault(model, {})
        if model_config.get("databases"):
            for database_config in model_config.get("databases"):
                url = url = (
                    f"{database_config['driver']}://{database_config['user']}:{database_config['password']}@{database_config['host']}:{database_config['port']}/{database_config['database']}"
                )
                engine = create_engine(
                    url,
                    pool_size=database_config.get("pool_size", 5),
                    max_overflow=database_config.get("max_overflow", 10),
                    pool_pre_ping=database_config.get("pool_pre_ping", True),
                )
                model_dict.setdefault(database_config["name"], engine)
                logger.info(
                    f"Created engine for {database_config['name']} in {model} of {project}"
                )


def resolve_table_name(name):
    """Resolves table names to their mapped names."""
    names = re.split("(?=[A - Z])", name)  # noqa
    return "_".join([x.lower() for x in names if x])


raise_attribute_error = object()


def resolve_attr(obj, attr, default=None):
    """Attempts to access attr via dotted notation,  returns none if attr does not exist."""
    try:
        return functools.reduce(getattr, attr.split("."), obj)
    except AttributeError:
        return default


class CustomBase:
    __repr_attrs__ = []
    __repr_max_length__ = 15

    @declared_attr
    def __tablename__(self):
        return resolve_table_name(self.__name__)

    def dict(self):
        """Returns a dict representation of a model."""
        return {c.name: getattr(self, c.name) for c in self.__table__.columns}

    @property
    def _id_str(self):
        ids = inspect(self).identity
        if ids:
            return "-".join([str(x) for x in ids]) if len(ids) > 1 else str(ids[0])
        else:
            return "None"

    @property
    def _repr_attrs_str(self):
        max_length = self.__repr_max_length__

        values = []
        single = len(self.__repr_attrs__) == 1
        for key in self.__repr_attrs__:
            if not hasattr(self, key):
                raise KeyError(
                    "{} has incorrect attribute '{}' in "
                    "__repr__attrs__".format(self.__class__, key)
                )
            value = getattr(self, key)
            wrap_in_quote = isinstance(value, str)

            value = str(value)
            if len(value) > max_length:
                value = value[:max_length] + "..."

            if wrap_in_quote:
                value = "'{}'".format(value)
            values.append(value if single else "{}:{}".format(key, value))

        return " ".join(values)

    def __repr__(self):
        # get id like '#123'
        id_str = ("#" + self._id_str) if self._id_str else ""
        # join class name,  id and repr_attrs
        return "<{} {}{}>".format(
            self.__class__.__name__,
            id_str,
            " " + self._repr_attrs_str if self._repr_attrs_str else "",
        )


Base = declarative_base(cls=CustomBase)
session_dict = {}
for project, project_dict in engine_dict.items():
    session_dict[project] = {}
    for model, model_dict in project_dict.items():
        session_dict[project][model] = {}
        for database, engine in model_dict.items():
            session_dict[project][model][database] = sessionmaker(bind=engine)
            logger.info(f"Created session for {database} in {model} of {project}")


# def get_db(request: Request,  project: str,  model: str,  database: str):
#     return request.state.db[project][model][database]


# DbSession  = Annotated[Session,  Depends(get_db)]


def get_model_name_by_tablename(table_fullname: str) -> str:
    """Returns the model name of a given table."""
    return get_class_by_tablename(table_fullname=table_fullname).__name__


def get_class_by_tablename(table_fullname: str):
    """Return class reference mapped to table."""

    def _find_class(name):
        for c in Base.registry._class_registry.values():
            if hasattr(c, "__table__"):
                if c.__table__.fullname.lower() == name.lower():
                    return c

    logger.info(f"table_fullname: {table_fullname}")
    mapped_name = resolve_table_name(table_fullname)
    logger.info(f"mapped_name: {mapped_name}")
    mapped_class = _find_class(mapped_name)
    logger.info(f"mapped_class: {mapped_class}")
    # try looking in the 'app_core' schema
    if not mapped_class:
        mapped_class = _find_class(f"app_core.{mapped_name}")

    if not mapped_class:
        raise ValidationError(
            [
                InitErrorDetails(
                    type=NotFoundError(
                        message="Model not found. Check the name of your model.",
                        code=None,
                    ),
                    input=mapped_name,
                )
            ],
        )

    return mapped_class


def get_table_name_by_class_instance(class_instance) -> str:
    """Returns the name of the table for a given class instance."""
    return class_instance._sa_instance_state.mapper.mapped_table.name


@contextmanager
def get_session(project: str, model: str, database: str):
    """Context manager to ensure the session is closed after use."""
    session_maker = session_dict[project][model][database]
    session: Session = session_maker()
    try:
        yield session
        session.commit()
    except Exception:
        session.rollback()
        raise
    finally:
        session.close()
