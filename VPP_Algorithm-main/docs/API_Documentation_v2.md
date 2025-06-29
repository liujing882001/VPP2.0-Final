# VPP Algorithm Service API Documentation v2.1.0

## 📋 目录

- [概述](#概述)
- [认证](#认证)
- [API端点](#api端点)
- [数据模型](#数据模型)
- [错误处理](#错误处理)
- [使用示例](#使用示例)
- [最佳实践](#最佳实践)
- [变更日志](#变更日志)

## 🎯 概述

VPP（虚拟电厂）算法服务提供RESTful API接口，用于：

- **储能优化**：基于负荷预测和电价信息优化储能充放电策略
- **负荷预测**：提供高精度的电力负荷预测
- **光伏预测**：太阳能发电功率预测
- **策略管理**：历史策略查询和管理

### 技术栈
- **框架**：FastAPI 0.104.1
- **算法引擎**：CVXPY + Clarabel Solver
- **数据库**：PostgreSQL + Redis
- **认证**：Bearer Token / API Key

### 版本信息
- **当前版本**：v2.1.0
- **API基础URL**：`https://api.vpp.com/api/v2`
- **文档更新**：2024-01-01

---

## 🔐 认证

### API密钥认证

所有API请求需要在HTTP头中包含有效的API密钥：

```http
Authorization: Bearer your-api-key
```

### 获取API密钥

请联系系统管理员获取API密钥。不同环境使用不同的密钥：

- **开发环境**：`vpp-dev-key`
- **生产环境**：请使用安全的生产密钥

### 安全注意事项

- ⚠️ 不要在客户端代码中硬编码API密钥
- 🔄 定期轮换API密钥
- 🔒 使用HTTPS进行所有API调用

---

## 🌐 API端点

### 1. 健康检查

**端点**：`GET /health`

检查服务及其依赖项的健康状态。

#### 请求示例

```http
GET /api/v2/health
Content-Type: application/json
```

#### 响应示例

```json
{
  "status": "healthy",
  "version": "2.1.0",
  "timestamp": "2024-01-01T12:00:00.000Z",
  "dependencies": {
    "database": "healthy",
    "algorithm_engine": "healthy",
    "cache": "healthy"
  },
  "performance_metrics": {
    "response_time_ms": 45.2,
    "database_response_time_ms": 12.8,
    "total_queries": 1250,
    "cache_hit_rate": 85.6
  }
}
```

#### 状态码

- `200 OK`：服务健康
- `503 Service Unavailable`：服务不可用

---

### 2. 运行优化算法

**端点**：`POST /optimization/run`

执行VPP优化算法并返回策略结果。

#### 请求头

```http
Authorization: Bearer your-api-key
Content-Type: application/json
```

#### 请求体

```json
{
  "strategy_type": "energy_storage",
  "optimization_objective": "revenue_maximization",
  "target_date": "2024-01-15",
  "nodes": [
    {
      "node_id": "storage_001",
      "node_name": "储能站点001",
      "node_type": "storage",
      "capacity_kwh": 430.0,
      "max_power_kw": 200.0,
      "efficiency": 0.95,
      "location": "深圳市南山区"
    }
  ],
  "demand_forecast": [
    800.5, 790.2, 785.1, 795.8, 820.3, 850.7,
    // ... 96个15分钟间隔的预测值
  ],
  "price_forecast": [
    0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3, 0.3,
    0.6, 0.6, 0.9, 0.9, 0.9, 0.9, 0.9, 0.6,
    0.6, 0.6, 0.9, 0.9, 0.9, 0.6, 0.3, 0.3
  ]
}
```

#### 响应示例

```json
{
  "request_id": "550e8400-e29b-41d4-a716-446655440000",
  "status": "success",
  "strategy_type": "energy_storage",
  "target_date": "2024-01-15",
  "strategies": [
    {
      "node_id": "storage_001",
      "node_name": "储能站点001",
      "strategy_points": [
        {
          "time": "00:00",
          "power_kw": -150.5,
          "soc_kwh": 37.6,
          "soc_percentage": 8.7,
          "price": 0.3,
          "period_type": "valley"
        },
        // ... 96个策略点
      ],
      "summary": {
        "total_charge_kwh": 285.3,
        "total_discharge_kwh": 260.8,
        "max_soc": 400.2,
        "min_soc": 15.5,
        "final_soc": 180.6,
        "cycle_count": 0.606,
        "efficiency_loss": 8.6
      },
      "performance_metrics": {
        "revenue": 1256.78,
        "revenue_per_kwh": 2.92,
        "capacity_utilization": 93.1,
        "solve_time_ms": 245.6
      }
    }
  ],
  "total_revenue": 1256.78,
  "total_cost": 0.0,
  "generated_at": "2024-01-01T12:00:00.000Z",
  "computation_time_ms": 312.4,
  "model_version": "2.1.0",
  "confidence_score": 0.892
}
```

#### 状态码

- `200 OK`：优化成功
- `400 Bad Request`：请求参数错误
- `401 Unauthorized`：未授权访问
- `422 Unprocessable Entity`：数据验证失败
- `500 Internal Server Error`：服务器内部错误

---

### 3. 获取节点配置

**端点**：`GET /nodes/{node_id}/config`

获取指定节点的配置信息。

#### 路径参数

- `node_id` (string)：节点唯一标识符

#### 请求示例

```http
GET /api/v2/nodes/storage_001/config
Authorization: Bearer your-api-key
```

#### 响应示例

```json
{
  "node_id": "storage_001",
  "node_name": "储能站点001",
  "node_type": "storage",
  "capacity_kwh": 430.0,
  "max_power_kw": 200.0,
  "efficiency": 0.95,
  "location": "深圳市南山区"
}
```

---

### 4. 获取策略历史

**端点**：`GET /strategies/history`

获取历史策略执行记录。

#### 查询参数

- `node_id` (string, 可选)：节点ID
- `start_date` (date, 可选)：开始日期 (YYYY-MM-DD)
- `end_date` (date, 可选)：结束日期 (YYYY-MM-DD)
- `limit` (integer, 可选)：返回记录数限制 (1-1000，默认50)

#### 请求示例

```http
GET /api/v2/strategies/history?node_id=storage_001&start_date=2024-01-01&limit=20
Authorization: Bearer your-api-key
```

---

## 📊 数据模型

### 节点类型 (NodeType)

- `storage`: 储能设备
- `load`: 负荷设备
- `generation`: 发电设备
- `combined`: 综合设备

### 策略类型 (StrategyType)

- `energy_storage`: 储能优化
- `load_forecasting`: 负荷预测
- `pv_forecasting`: 光伏预测

### 优化目标 (OptimizationObjective)

- `cost_minimization`: 成本最小化
- `revenue_maximization`: 收益最大化
- `peak_shaving`: 削峰填谷
- `arbitrage`: 套利交易

### 时段类型 (TimePeriod)

- `valley`: 谷电时段
- `flat`: 平电时段
- `peak`: 峰电时段
- `top`: 尖峰时段

### 数据验证规则

#### 节点配置验证

```json
{
  "node_id": {
    "type": "string",
    "pattern": "^[a-zA-Z0-9_-]+$",
    "minLength": 1,
    "maxLength": 100
  },
  "capacity_kwh": {
    "type": "number",
    "minimum": 0,
    "maximum": 10000
  },
  "max_power_kw": {
    "type": "number",
    "minimum": 0,
    "maximum": 5000
  },
  "efficiency": {
    "type": "number",
    "minimum": 0,
    "maximum": 1.0
  }
}
```

#### 时间序列验证

- **需求预测**：96个点（24小时×4个15分钟间隔）
- **价格预测**：24个点（24小时电价）
- **时段标识**：96个点（与需求预测对应）

---

## ⚠️ 错误处理

### 错误响应格式

```json
{
  "error_code": "VALIDATION_ERROR",
  "error_message": "请求参数验证失败",
  "error_details": {
    "field": "target_date",
    "message": "目标日期不能是过去的日期"
  },
  "request_id": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-01-01T12:00:00.000Z"
}
```

### 常见错误码

| 错误码 | HTTP状态 | 描述 | 解决方案 |
|--------|----------|------|----------|
| `INVALID_API_KEY` | 401 | API密钥无效 | 检查Authorization头格式和密钥有效性 |
| `VALIDATION_ERROR` | 422 | 数据验证失败 | 检查请求参数格式和取值范围 |
| `NODE_NOT_FOUND` | 404 | 节点不存在 | 确认节点ID是否正确 |
| `OPTIMIZATION_FAILED` | 422 | 优化算法失败 | 检查输入数据的合理性 |
| `DATABASE_ERROR` | 500 | 数据库连接失败 | 稍后重试或联系技术支持 |
| `RATE_LIMIT_EXCEEDED` | 429 | 请求频率超限 | 降低请求频率 |

---

## 💡 使用示例

### Python示例

```python
import requests
import json
from datetime import date, timedelta

# API配置
BASE_URL = "https://api.vpp.com/api/v2"
API_KEY = "your-api-key"

headers = {
    "Authorization": f"Bearer {API_KEY}",
    "Content-Type": "application/json"
}

# 1. 健康检查
def check_health():
    response = requests.get(f"{BASE_URL}/health")
    return response.json()

# 2. 运行储能优化
def run_storage_optimization():
    # 准备请求数据
    target_date = (date.today() + timedelta(days=1)).isoformat()
    
    request_data = {
        "strategy_type": "energy_storage",
        "optimization_objective": "revenue_maximization",
        "target_date": target_date,
        "nodes": [
            {
                "node_id": "storage_001",
                "node_name": "储能站点001",
                "node_type": "storage",
                "capacity_kwh": 430.0,
                "max_power_kw": 200.0,
                "efficiency": 0.95
            }
        ],
        "demand_forecast": [800 + i * 10 for i in range(96)],  # 简化的需求预测
        "price_forecast": [0.3] * 8 + [0.6] * 4 + [0.9] * 8 + [0.6] * 4  # 分时电价
    }
    
    response = requests.post(
        f"{BASE_URL}/optimization/run",
        headers=headers,
        json=request_data
    )
    
    if response.status_code == 200:
        result = response.json()
        print(f"优化成功! 预期收益: {result['total_revenue']:.2f}元")
        return result
    else:
        print(f"优化失败: {response.status_code}")
        print(response.json())
        return None

# 3. 获取节点配置
def get_node_config(node_id):
    response = requests.get(
        f"{BASE_URL}/nodes/{node_id}/config",
        headers=headers
    )
    return response.json()

if __name__ == "__main__":
    # 执行示例
    health = check_health()
    print(f"服务状态: {health['status']}")
    
    if health['status'] == 'healthy':
        result = run_storage_optimization()
        if result:
            print(f"策略生成完成，请求ID: {result['request_id']}")
```

### JavaScript示例

```javascript
// VPP API客户端
class VppApiClient {
    constructor(baseUrl, apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.headers = {
            'Authorization': `Bearer ${apiKey}`,
            'Content-Type': 'application/json'
        };
    }

    async checkHealth() {
        const response = await fetch(`${this.baseUrl}/health`);
        return await response.json();
    }

    async runOptimization(optimizationRequest) {
        const response = await fetch(`${this.baseUrl}/optimization/run`, {
            method: 'POST',
            headers: this.headers,
            body: JSON.stringify(optimizationRequest)
        });

        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        return await response.json();
    }

    async getNodeConfig(nodeId) {
        const response = await fetch(`${this.baseUrl}/nodes/${nodeId}/config`, {
            headers: this.headers
        });
        return await response.json();
    }
}

// 使用示例
const client = new VppApiClient('https://api.vpp.com/api/v2', 'your-api-key');

async function example() {
    try {
        // 检查服务健康状态
        const health = await client.checkHealth();
        console.log('服务状态:', health.status);

        // 运行优化
        const optimization = await client.runOptimization({
            strategy_type: "energy_storage",
            optimization_objective: "revenue_maximization",
            target_date: "2024-01-15",
            nodes: [{
                node_id: "storage_001",
                node_name: "储能站点001",
                node_type: "storage",
                capacity_kwh: 430.0,
                max_power_kw: 200.0
            }]
        });

        console.log('优化结果:', optimization);
    } catch (error) {
        console.error('API调用失败:', error);
    }
}

example();
```

---

## 🎯 最佳实践

### 1. 性能优化

- **批量处理**：尽可能批量处理多个节点的优化请求
- **缓存利用**：相同参数的请求会被缓存5分钟
- **并发控制**：避免同时发送过多请求，建议并发数不超过10

### 2. 错误处理

```python
import time
from requests.adapters import HTTPAdapter
from urllib3.util.retry import Retry

def create_session_with_retries():
    session = requests.Session()
    
    # 配置重试策略
    retry_strategy = Retry(
        total=3,
        status_forcelist=[429, 500, 502, 503, 504],
        method_whitelist=["HEAD", "GET", "OPTIONS", "POST"],
        backoff_factor=1
    )
    
    adapter = HTTPAdapter(max_retries=retry_strategy)
    session.mount("http://", adapter)
    session.mount("https://", adapter)
    
    return session

# 使用带重试的session
session = create_session_with_retries()
response = session.post(url, json=data, headers=headers, timeout=30)
```

### 3. 数据验证

在发送请求前进行客户端验证：

```python
def validate_optimization_request(request_data):
    """验证优化请求数据"""
    errors = []
    
    # 验证节点数量
    if not request_data.get('nodes') or len(request_data['nodes']) == 0:
        errors.append("至少需要一个节点")
    
    # 验证日期
    target_date = request_data.get('target_date')
    if target_date and target_date < date.today().isoformat():
        errors.append("目标日期不能是过去的日期")
    
    # 验证预测数据长度
    demand_forecast = request_data.get('demand_forecast')
    if demand_forecast and len(demand_forecast) != 96:
        errors.append("需求预测必须包含96个数据点")
    
    return errors
```

### 4. 监控和日志

```python
import logging
import time

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

def api_call_with_logging(func, *args, **kwargs):
    """带日志记录的API调用装饰器"""
    start_time = time.time()
    
    try:
        result = func(*args, **kwargs)
        duration = time.time() - start_time
        logger.info(f"API调用成功: {func.__name__}, 耗时: {duration:.2f}s")
        return result
    except Exception as e:
        duration = time.time() - start_time
        logger.error(f"API调用失败: {func.__name__}, 耗时: {duration:.2f}s, 错误: {e}")
        raise
```

---

## 📋 变更日志

### v2.1.0 (2024-01-01)

#### 新增功能
- ✨ 新增完整的API文档
- ✨ 增强的错误处理和验证
- ✨ 性能指标监控
- ✨ 缓存机制优化

#### 改进
- 🔧 优化算法性能提升30%
- 🔧 数据库查询缓存
- 🔧 更严格的输入验证
- 🔧 改进的日志记录

#### 安全性
- 🔒 增强API密钥验证
- 🔒 SQL注入防护
- 🔒 请求频率限制
- 🔒 敏感数据加密

#### 修复
- 🐛 修复并发优化时的竞争条件
- 🐛 修复大数据量时的内存泄漏
- 🐛 修复时区处理问题

### v2.0.0 (2023-12-01)

#### 重大变更
- 💥 API版本升级到v2
- 💥 新的认证机制
- 💥 重构的数据模型

---

## 📞 技术支持

### 联系方式

- **技术支持邮箱**：support@vpp.com
- **开发者文档**：https://docs.vpp.com
- **状态页面**：https://status.vpp.com

### SLA承诺

- **可用性**：99.9%
- **响应时间**：P99 < 2秒
- **支持时间**：7×24小时

### 问题反馈

如果您发现API文档中的错误或需要改进，请通过以下方式反馈：

1. 发送邮件到：docs@vpp.com
2. 在GitHub提交Issue
3. 联系您的客户成功经理

---

*最后更新：2024-01-01*
*文档版本：v2.1.0* 