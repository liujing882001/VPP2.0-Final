# DeepEngine平台API接口规范文档

[![API版本](https://img.shields.io/badge/API-v2.0-blue.svg)](#)
[![标准](https://img.shields.io/badge/Standard-RESTful-green.svg)](#)
[![认证](https://img.shields.io/badge/Auth-OAuth2.1-orange.svg)](#)

> 📡 **文档目标**: 定义DeepEngine平台完整的API接口规范  
> 🔗 **设计标准**: RESTful API + OpenAPI 3.0 + JSON Schema  
> 🔐 **安全机制**: OAuth 2.1 + JWT + API密钥双重认证

---

## 📋 API概览

### 🎯 设计原则

#### 1. RESTful设计标准
```typescript
// API设计原则
interface APIDesignPrinciples {
  restfulStandards: {
    resourceOriented: "以资源为中心的URL设计";
    httpMethods: "GET/POST/PUT/DELETE/PATCH语义化";
    stateless: "无状态设计，每次请求包含完整信息";
    idempotent: "幂等性保证，相同请求产生相同结果";
  };
  
  responseFormat: {
    contentType: "application/json";
    encoding: "UTF-8";
    structure: "统一的响应结构体";
    errorHandling: "标准化错误码和消息";
  };
  
  versioning: {
    strategy: "URL版本控制 /api/v1, /api/v2";
    deprecation: "渐进式版本废弃策略";
    compatibility: "向后兼容性保证";
  };
}
```

#### 2. 通用响应格式
```typescript
// 标准响应格式
interface APIResponse<T> {
  success: boolean;
  code: number;
  message: string;
  data: T;
  timestamp: string;
  requestId: string;
  pagination?: PaginationInfo;
  metadata?: Record<string, any>;
}

// 分页信息
interface PaginationInfo {
  page: number;
  pageSize: number;
  total: number;
  totalPages: number;
  hasNext: boolean;
  hasPrev: boolean;
}

// 错误响应格式
interface ErrorResponse {
  success: false;
  code: number;
  message: string;
  details?: ValidationError[];
  timestamp: string;
  requestId: string;
}
```

### 🔐 认证与授权

#### OAuth 2.1 认证流程
```typescript
// 认证接口
interface AuthenticationAPI {
  // 获取访问令牌
  "POST /api/v2/auth/token": {
    request: {
      grant_type: "authorization_code" | "refresh_token" | "client_credentials";
      code?: string;
      refresh_token?: string;
      client_id: string;
      client_secret: string;
      redirect_uri?: string;
    };
    response: {
      access_token: string;
      token_type: "Bearer";
      expires_in: number;
      refresh_token: string;
      scope: string;
    };
  };
  
  // 刷新令牌
  "POST /api/v2/auth/refresh": {
    request: {
      refresh_token: string;
    };
    response: {
      access_token: string;
      expires_in: number;
      refresh_token: string;
    };
  };
}
```

---

## 🔋 PowerGen发电管理API

### 📊 设备管理接口

#### 设备基础操作
```typescript
// PowerGen设备管理API
interface PowerGenDeviceAPI {
  // 获取设备列表
  "GET /api/v2/powergen/devices": {
    query: {
      page?: number;
      pageSize?: number;
      deviceType?: "solar" | "wind" | "storage";
      status?: "online" | "offline" | "maintenance";
      location?: string;
    };
    response: APIResponse<{
      devices: PowerGenDevice[];
      summary: DeviceSummary;
    }>;
  };
  
  // 获取设备详情
  "GET /api/v2/powergen/devices/{deviceId}": {
    params: {
      deviceId: string;
    };
    response: APIResponse<PowerGenDevice>;
  };
  
  // 创建设备
  "POST /api/v2/powergen/devices": {
    request: CreateDeviceRequest;
    response: APIResponse<PowerGenDevice>;
  };
}

// 设备数据模型
interface PowerGenDevice {
  deviceId: string;
  deviceName: string;
  deviceType: "solar" | "wind" | "storage" | "hybrid";
  status: "online" | "offline" | "maintenance" | "error";
  location: {
    latitude: number;
    longitude: number;
    address: string;
    timezone: string;
  };
  specifications: {
    ratedPower: number; // MW
    installedCapacity: number; // MW
    efficiency: number; // %
    manufacturer: string;
    model: string;
    commissionDate: string;
  };
  realTimeData: {
    activePower: number; // MW
    reactivePower: number; // MVAr
    voltage: number; // V
    current: number; // A
    frequency: number; // Hz
    temperature: number; // °C
    timestamp: string;
  };
  metadata: Record<string, any>;
}
```

### 📈 功率预测接口

#### AI预测服务
```typescript
// 功率预测API
interface PowerForecastAPI {
  // 获取功率预测
  "POST /api/v2/powergen/forecast": {
    request: {
      deviceId: string;
      forecastType: "power" | "energy" | "revenue";
      timeHorizon: "1hour" | "6hour" | "24hour" | "7day" | "30day";
      startTime: string;
      endTime: string;
      weatherData?: WeatherForecastData;
      modelPreference?: string[];
    };
    response: APIResponse<ForecastResult>;
  };
  
  // 获取历史预测准确性
  "GET /api/v2/powergen/forecast/{deviceId}/accuracy": {
    params: {
      deviceId: string;
    };
    query: {
      period: "24hour" | "7day" | "30day";
      modelType?: string;
    };
    response: APIResponse<AccuracyMetrics>;
  };
}

// 预测结果数据模型
interface ForecastResult {
  deviceId: string;
  forecastType: string;
  timeHorizon: string;
  forecastData: {
    timestamp: string;
    predictedValue: number;
    confidenceInterval: {
      lower: number;
      upper: number;
      confidence: number; // 置信度 0-1
    };
    weatherConditions?: {
      irradiance?: number;
      windSpeed?: number;
      temperature: number;
      humidity: number;
    };
  }[];
  modelInfo: {
    primaryModel: string;
    ensembleWeights: Record<string, number>;
    predictionAccuracy: number;
  };
  generatedAt: string;
  validUntil: string;
}
```

### ⚡ 储能优化接口

#### 储能系统控制
```typescript
// 储能优化API
interface StorageOptimizationAPI {
  // 启动储能优化
  "POST /api/v2/powergen/storage/optimize": {
    request: {
      storageSystemId: string;
      optimizationPeriod: {
        startTime: string;
        endTime: string;
      };
      demandForecast: number[];
      priceSchedule: PriceScheduleData;
      constraints: {
        minSOC: number; // 最小荷电状态
        maxSOC: number; // 最大荷电状态
        maxChargeRate: number; // 最大充电功率 MW
        maxDischargeRate: number; // 最大放电功率 MW
        efficiency: number; // 充放电效率
      };
      objective: "cost_minimize" | "revenue_maximize" | "peak_shaving";
    };
    response: APIResponse<OptimizationResult>;
  };
}

// 优化结果数据模型
interface OptimizationResult {
  optimizationId: string;
  storageSystemId: string;
  status: "pending" | "running" | "completed" | "failed";
  objective: string;
  results: {
    chargingSchedule: {
      timestamp: string;
      chargePower: number; // MW
      socLevel: number; // %
    }[];
    dischargingSchedule: {
      timestamp: string;
      dischargePower: number; // MW
      socLevel: number; // %
    }[];
    economicMetrics: {
      totalCost: number; // 元
      totalRevenue: number; // 元
      netProfit: number; // 元
      peakReduction: number; // MW
      energyArbitrage: number; // MWh
    };
  };
  computationTime: number; // seconds
  createdAt: string;
  completedAt?: string;
}
```

---

## 🏠 SmartLoad用能管理API

### 📊 负荷监控接口

#### 实时负荷数据
```typescript
// SmartLoad监控API
interface SmartLoadMonitoringAPI {
  // 获取实时负荷数据
  "GET /api/v2/smartload/load/realtime": {
    query: {
      meterId: string;
      aggregation?: "raw" | "minute" | "hour";
    };
    response: APIResponse<RealTimeLoadData>;
  };
  
  // 获取历史负荷数据
  "GET /api/v2/smartload/load/historical": {
    query: {
      meterId: string;
      startTime: string;
      endTime: string;
      interval: "15min" | "1hour" | "1day";
      dataType?: "power" | "energy" | "both";
    };
    response: APIResponse<HistoricalLoadData>;
  };
}

// 负荷数据模型
interface RealTimeLoadData {
  meterId: string;
  timestamp: string;
  measurements: {
    activePower: number; // kW
    reactivePower: number; // kVAr
    apparentPower: number; // kVA
    powerFactor: number;
    voltage: number; // V
    current: number; // A
    frequency: number; // Hz
  };
  tariffInfo: {
    currentTariff: string;
    currentPrice: number; // 元/kWh
    tariffPeriod: "peak" | "flat" | "valley";
  };
  qualityMetrics: {
    thd: number; // 总谐波失真
    voltageUnbalance: number;
    frequencyDeviation: number;
  };
}
```

### 🎯 负荷优化接口

#### AI驱动的负荷优化
```typescript
// 负荷优化API
interface LoadOptimizationAPI {
  // 启动负荷优化
  "POST /api/v2/smartload/optimization/start": {
    request: {
      meterId: string;
      optimizationPeriod: {
        startTime: string;
        endTime: string;
      };
      baselineLoad: number[]; // 基准负荷曲线
      priceSchedule: TariffSchedule;
      userPreferences: {
        comfortLevel: number; // 1-10
        costSensitivity: number; // 1-10
        environmentalPriority: number; // 1-10
      };
      constraints: {
        maxLoadReduction: number; // %
        essentialLoads: string[]; // 不可调节负荷列表
        timeWindows: TimeWindow[]; // 可调节时间窗口
      };
      strategy: "cost_minimize" | "comfort_maximize" | "carbon_reduce";
    };
    response: APIResponse<OptimizationJob>;
  };
  
  // 获取优化建议
  "GET /api/v2/smartload/optimization/{jobId}/recommendations": {
    params: {
      jobId: string;
    };
    response: APIResponse<OptimizationRecommendations>;
  };
}

// 优化建议数据模型
interface OptimizationRecommendations {
  jobId: string;
  meterId: string;
  optimizationStrategy: string;
  recommendations: {
    actionId: string;
    actionType: "load_shift" | "load_reduce" | "schedule_change";
    description: string;
    impact: {
      costSaving: number; // 元
      energySaving: number; // kWh
      comfortImpact: number; // 1-10 分
      carbonReduction: number; // kg CO2
    };
    timeWindow: {
      startTime: string;
      endTime: string;
    };
    feasibility: number; // 0-1
    priority: "high" | "medium" | "low";
  }[];
  totalBenefit: {
    costSaving: number;
    energySaving: number;
    carbonReduction: number;
  };
  generatedAt: string;
  validUntil: string;
}
```

### 📱 需求响应接口

#### 需求响应管理
```typescript
// 需求响应API
interface DemandResponseAPI {
  // 获取可用DR项目
  "GET /api/v2/smartload/demand-response/programs": {
    query: {
      location?: string;
      programType?: "emergency" | "economic" | "capacity";
      status?: "active" | "upcoming" | "ended";
    };
    response: APIResponse<DRProgram[]>;
  };
  
  // 参与DR项目
  "POST /api/v2/smartload/demand-response/participate": {
    request: {
      programId: string;
      meterId: string;
      commitment: {
        maxReduction: number; // kW
        minDuration: number; // minutes
        availableHours: string[]; // 可参与时段
      };
    };
    response: APIResponse<ParticipationResult>;
  };
}

// DR项目数据模型
interface DRProgram {
  programId: string;
  programName: string;
  provider: string;
  programType: "emergency" | "economic" | "capacity";
  description: string;
  incentives: {
    participationPayment: number; // 元/kW
    performancePayment: number; // 元/kWh
    penaltyRate: number; // 元/kWh
  };
  requirements: {
    minCapacity: number; // kW
    maxResponseTime: number; // minutes
    minParticipationRate: number; // %
  };
  schedule: {
    season: string;
    availableHours: string[];
    maxEvents: number;
    maxDuration: number; // hours
  };
  status: "active" | "full" | "suspended";
}
```

---

## ⚡ VPPCloud虚拟电厂API

### 🔗 资源聚合接口

#### 分布式资源管理
```typescript
// VPP资源聚合API
interface VPPResourceAPI {
  // 注册分布式资源
  "POST /api/v2/vppcloud/resources/register": {
    request: {
      resourceType: "generation" | "storage" | "load" | "hybrid";
      capacity: number; // MW
      location: {
        latitude: number;
        longitude: number;
        gridNode: string;
      };
      technicalSpecs: {
        rampRate: number; // MW/min
        minimumRunTime: number; // minutes
        startupTime: number; // minutes
        efficiency: number;
      };
      communicationProtocol: "modbus" | "iec61850" | "sunspec" | "proprietary";
      owner: {
        entityName: string;
        contactInfo: ContactInfo;
        contractTerms: ContractTerms;
      };
    };
    response: APIResponse<ResourceRegistration>;
  };
  
  // 获取VPP资源列表
  "GET /api/v2/vppcloud/resources": {
    query: {
      vppId?: string;
      resourceType?: string;
      status?: "available" | "committed" | "offline";
      location?: string;
      minCapacity?: number;
    };
    response: APIResponse<VPPResource[]>;
  };
  
  // 创建虚拟电厂
  "POST /api/v2/vppcloud/vpps": {
    request: {
      vppName: string;
      description: string;
      aggregationStrategy: "geographic" | "technical" | "market";
      resourceIds: string[];
      operatingParameters: {
        maxCapacity: number; // MW
        minCapacity: number; // MW
        responseTime: number; // seconds
        availabilityWindows: TimeWindow[];
      };
      contractualTerms: VPPContractTerms;
    };
    response: APIResponse<VPPCreationResult>;
  };
}

// VPP资源数据模型
interface VPPResource {
  resourceId: string;
  resourceName: string;
  resourceType: "generation" | "storage" | "load" | "hybrid";
  status: "available" | "committed" | "offline" | "maintenance";
  capacity: {
    rated: number; // MW
    available: number; // MW
    committed: number; // MW
  };
  location: {
    latitude: number;
    longitude: number;
    gridNode: string;
    region: string;
  };
  performance: {
    availability: number; // %
    reliability: number; // %
    responseAccuracy: number; // %
    lastResponseTime: number; // seconds
  };
  currentState: {
    activePower: number; // MW
    reactivePower: number; // MVAr
    status: string;
    lastUpdate: string;
  };
}
```

### 💹 市场交易接口

#### 电力市场参与
```typescript
// 市场交易API
interface MarketTradingAPI {
  // 提交日前市场报价
  "POST /api/v2/vppcloud/markets/day-ahead/bids": {
    request: {
      vppId: string;
      tradingDate: string;
      bidSegments: {
        hour: number; // 0-23
        quantity: number; // MW
        price: number; // 元/MWh
        bidType: "energy" | "capacity" | "regulation";
      }[];
      constraints: {
        minOutput: number; // MW
        maxOutput: number; // MW
        rampRate: number; // MW/min
      };
    };
    response: APIResponse<BidSubmissionResult>;
  };
  
  // 获取市场出清结果
  "GET /api/v2/vppcloud/markets/clearing-results": {
    query: {
      marketType: "day_ahead" | "real_time" | "ancillary";
      tradingDate: string;
      vppId?: string;
    };
    response: APIResponse<MarketClearingResult>;
  };
}

// 市场出清结果数据模型
interface MarketClearingResult {
  marketType: string;
  tradingDate: string;
  clearingData: {
    hour: number;
    clearingPrice: number; // 元/MWh
    totalDemand: number; // MW
    totalSupply: number; // MW
    priceVolatility: number;
  }[];
  vppResults?: {
    vppId: string;
    allocatedQuantity: number; // MW
    clearingPrice: number; // 元/MWh
    revenue: number; // 元
    settlementAmount: number; // 元
  };
  marketStatistics: {
    averagePrice: number;
    peakPrice: number;
    priceRange: number;
    tradingVolume: number; // MWh
  };
}
```

---

## 🔧 错误处理和状态码

### 📋 标准HTTP状态码

| 状态码 | 含义 | 使用场景 |
|--------|------|----------|
| **200** | OK | 请求成功 |
| **201** | Created | 资源创建成功 |
| **204** | No Content | 删除成功，无返回内容 |
| **400** | Bad Request | 请求参数错误 |
| **401** | Unauthorized | 未认证或认证失败 |
| **403** | Forbidden | 权限不足 |
| **404** | Not Found | 资源不存在 |
| **409** | Conflict | 资源冲突 |
| **422** | Unprocessable Entity | 数据验证失败 |
| **429** | Too Many Requests | 请求频率限制 |
| **500** | Internal Server Error | 服务器内部错误 |

### 🚨 业务错误码

```typescript
// 业务错误码定义
enum BusinessErrorCode {
  // 通用错误 (1000-1099)
  VALIDATION_ERROR = 1001,
  BUSINESS_RULE_VIOLATION = 1002,
  RESOURCE_NOT_FOUND = 1003,
  DUPLICATE_RESOURCE = 1004,
  
  // 认证错误 (1100-1199)
  INVALID_CREDENTIALS = 1101,
  TOKEN_EXPIRED = 1102,
  INSUFFICIENT_PERMISSIONS = 1103,
  ACCOUNT_SUSPENDED = 1104,
  
  // PowerGen错误 (2000-2099)
  DEVICE_OFFLINE = 2001,
  FORECAST_MODEL_ERROR = 2002,
  OPTIMIZATION_FAILED = 2003,
  STORAGE_CONTROL_ERROR = 2004,
  
  // SmartLoad错误 (3000-3099)
  LOAD_DATA_UNAVAILABLE = 3001,
  OPTIMIZATION_INFEASIBLE = 3002,
  DR_EVENT_CONFLICT = 3003,
  METER_COMMUNICATION_ERROR = 3004,
  
  // VPPCloud错误 (4000-4099)
  RESOURCE_AGGREGATION_FAILED = 4001,
  MARKET_BID_REJECTED = 4002,
  DISPATCH_INSTRUCTION_FAILED = 4003,
  VPP_CAPACITY_EXCEEDED = 4004,
}
```

---

## 📝 使用示例

### 🔋 PowerGen使用示例

```typescript
// PowerGen API使用示例
async function managePowerGeneration() {
  // 1. 获取设备列表
  const devices = await fetch('/api/v2/powergen/devices', {
    headers: { 'Authorization': 'Bearer ' + token }
  });
  
  // 2. 获取功率预测
  const forecast = await fetch('/api/v2/powergen/forecast', {
    method: 'POST',
    headers: { 
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json' 
    },
    body: JSON.stringify({
      deviceId: 'solar-001',
      forecastType: 'power',
      timeHorizon: '24hour',
      startTime: '2024-01-01T00:00:00Z',
      endTime: '2024-01-02T00:00:00Z'
    })
  });
  
  // 3. 启动储能优化
  const optimization = await fetch('/api/v2/powergen/storage/optimize', {
    method: 'POST',
    headers: { 
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json' 
    },
    body: JSON.stringify({
      storageSystemId: 'battery-001',
      optimizationPeriod: {
        startTime: '2024-01-01T00:00:00Z',
        endTime: '2024-01-02T00:00:00Z'
      },
      demandForecast: [100, 120, 140], // 示例数据
      objective: 'cost_minimize'
    })
  });
}
```

### 🏠 SmartLoad使用示例

```javascript
// SmartLoad API使用示例
async function optimizeEnergyUsage() {
  // 1. 获取实时负荷
  const realTimeLoad = await fetch('/api/v2/smartload/load/realtime?meterId=meter-001', {
    headers: { 'Authorization': 'Bearer ' + token }
  });
  
  // 2. 启动负荷优化
  const optimization = await fetch('/api/v2/smartload/optimization/start', {
    method: 'POST',
    headers: { 
      'Authorization': 'Bearer ' + token,
      'Content-Type': 'application/json' 
    },
    body: JSON.stringify({
      meterId: 'meter-001',
      optimizationPeriod: {
        startTime: '2024-01-01T00:00:00Z',
        endTime: '2024-01-02T00:00:00Z'
      },
      baselineLoad: [50, 55, 60], // 基准负荷曲线
      userPreferences: {
        comfortLevel: 8,
        costSensitivity: 9,
        environmentalPriority: 7
      },
      strategy: 'cost_minimize'
    })
  });
  
  // 3. 获取优化建议
  const recommendations = await fetch(`/api/v2/smartload/optimization/${optimization.data.jobId}/recommendations`, {
    headers: { 'Authorization': 'Bearer ' + token }
  });
}
```

---

**文档版本**: v2.0  
**创建日期**: 2024-12-29  
**API设计师**: DeepEngine技术组  
**审核状态**: 待Review

> 🔗 **API演进**: 本API规范将根据业务需求和技术发展持续更新，保持向后兼容性。 