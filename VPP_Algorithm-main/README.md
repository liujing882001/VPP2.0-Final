# VPP_Algorithm
预测算法  Python

# VPP_Algorithm-main 优化说明

## 依赖安装

```bash
pip install -r requirements.txt
```

## 代码规范

- 使用 flake8 检查代码风格：
  ```bash
  flake8 .
  ```
- 使用 black 格式化代码：
  ```bash
  black .
  ```
- 使用 isort 排序导入：
  ```bash
  isort .
  ```

## 测试

```bash
pytest tests/
```

## 配置

- 复制 `.env.example` 为 `.env`，并根据实际情况填写。
- 修改 `config.yaml` 以适配你的环境。

## 公共模块

- `common/` 目录下包含日志、异常等通用工具。

## 其他建议

- 建议为每个子模块补充单元测试。
- 建议完善 API 文档和接口说明。
