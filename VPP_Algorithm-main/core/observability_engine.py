"""
智能监控和可观测性引擎
实现分布式链路追踪、性能监控、异常检测和实时告警
企业级可观测性解决方案
"""

import asyncio
import json
import logging
import time
import uuid
from datetime import datetime, timedelta
from typing import Dict, List, Optional, Any, Callable, Union
from dataclasses import dataclass, asdict
from enum import Enum
import threading
from collections import deque, defaultdict
import statistics
import numpy as np
from pathlib import Path

logger = logging.getLogger(__name__)


class MetricType(Enum):
    """指标类型"""
    COUNTER = "counter"
    GAUGE = "gauge"
    HISTOGRAM = "histogram"
    SUMMARY = "summary"


class AlertSeverity(Enum):
    """告警严重程度"""
    CRITICAL = "critical"
    HIGH = "high"
    MEDIUM = "medium"
    LOW = "low"
    INFO = "info"


class TraceStatus(Enum):
    """链路追踪状态"""
    SUCCESS = "success"
    ERROR = "error"
    TIMEOUT = "timeout"
    CANCELLED = "cancelled"


@dataclass
class Span:
    """链路追踪Span"""
    trace_id: str
    span_id: str
    parent_span_id: Optional[str]
    operation_name: str
    start_time: float
    end_time: Optional[float] = None
    status: TraceStatus = TraceStatus.SUCCESS
    tags: Dict[str, Any] = None
    logs: List[Dict[str, Any]] = None
    
    def __post_init__(self):
        if self.tags is None:
            self.tags = {}
        if self.logs is None:
            self.logs = []
    
    @property
    def duration(self) -> Optional[float]:
        if self.end_time:
            return self.end_time - self.start_time
        return None
    
    def finish(self, status: TraceStatus = TraceStatus.SUCCESS):
        """结束Span"""
        self.end_time = time.time()
        self.status = status
    
    def log(self, event: str, **fields):
        """记录日志"""
        log_entry = {
            "timestamp": time.time(),
            "event": event,
            **fields
        }
        self.logs.append(log_entry)
    
    def set_tag(self, key: str, value: Any):
        """设置标签"""
        self.tags[key] = value


@dataclass
class Metric:
    """指标数据"""
    name: str
    type: MetricType
    value: Union[float, int]
    timestamp: float
    tags: Dict[str, str] = None
    
    def __post_init__(self):
        if self.tags is None:
            self.tags = {}


@dataclass
class Alert:
    """告警数据"""
    id: str
    name: str
    severity: AlertSeverity
    message: str
    timestamp: float
    source: str
    tags: Dict[str, str] = None
    resolved: bool = False
    resolved_at: Optional[float] = None
    
    def __post_init__(self):
        if self.tags is None:
            self.tags = {}
    
    def resolve(self):
        """解决告警"""
        self.resolved = True
        self.resolved_at = time.time()


class DistributedTracer:
    """分布式链路追踪器"""
    
    def __init__(self, service_name: str = "vpp-algorithm"):
        self.service_name = service_name
        self._spans: Dict[str, Span] = {}
        self._active_spans: Dict[str, str] = {}  # thread_id -> span_id
        self._lock = threading.RLock()
        
    def start_span(self, operation_name: str, parent_span: Optional[Span] = None) -> Span:
        """开始新的Span"""
        trace_id = str(uuid.uuid4()) if parent_span is None else parent_span.trace_id
        span_id = str(uuid.uuid4())
        parent_span_id = parent_span.span_id if parent_span else None
        
        span = Span(
            trace_id=trace_id,
            span_id=span_id,
            parent_span_id=parent_span_id,
            operation_name=operation_name,
            start_time=time.time()
        )
        
        span.set_tag("service.name", self.service_name)
        
        with self._lock:
            self._spans[span_id] = span
            thread_id = threading.get_ident()
            self._active_spans[thread_id] = span_id
        
        logger.debug(f"开始Span: {operation_name} (trace_id={trace_id}, span_id={span_id})")
        return span
    
    def get_active_span(self) -> Optional[Span]:
        """获取当前活跃的Span"""
        thread_id = threading.get_ident()
        with self._lock:
            span_id = self._active_spans.get(thread_id)
            return self._spans.get(span_id) if span_id else None
    
    def finish_span(self, span: Span, status: TraceStatus = TraceStatus.SUCCESS):
        """结束Span"""
        span.finish(status)
        
        thread_id = threading.get_ident()
        with self._lock:
            if self._active_spans.get(thread_id) == span.span_id:
                del self._active_spans[thread_id]
        
        logger.debug(f"结束Span: {span.operation_name} (duration={span.duration:.3f}s)")
    
    def get_trace(self, trace_id: str) -> List[Span]:
        """获取完整的调用链"""
        with self._lock:
            return [span for span in self._spans.values() if span.trace_id == trace_id]


class MetricsCollector:
    """指标收集器"""
    
    def __init__(self, retention_period: int = 3600):  # 1小时保留期
        self.retention_period = retention_period
        self._metrics: Dict[str, deque] = defaultdict(lambda: deque(maxlen=10000))
        self._lock = threading.RLock()
        
        # 启动清理任务
        self._cleanup_task = asyncio.create_task(self._cleanup_old_metrics())
    
    def record_counter(self, name: str, value: float = 1.0, tags: Dict[str, str] = None):
        """记录计数器指标"""
        metric = Metric(name, MetricType.COUNTER, value, time.time(), tags)
        self._store_metric(metric)
    
    def record_gauge(self, name: str, value: float, tags: Dict[str, str] = None):
        """记录仪表盘指标"""
        metric = Metric(name, MetricType.GAUGE, value, time.time(), tags)
        self._store_metric(metric)
    
    def record_histogram(self, name: str, value: float, tags: Dict[str, str] = None):
        """记录直方图指标"""
        metric = Metric(name, MetricType.HISTOGRAM, value, time.time(), tags)
        self._store_metric(metric)
    
    def _store_metric(self, metric: Metric):
        """存储指标"""
        with self._lock:
            self._metrics[metric.name].append(metric)
    
    def get_metrics(self, name: str, since: Optional[float] = None) -> List[Metric]:
        """获取指标数据"""
        with self._lock:
            metrics = list(self._metrics[name])
            
            if since:
                metrics = [m for m in metrics if m.timestamp >= since]
            
            return metrics
    
    def get_metric_summary(self, name: str, window: int = 300) -> Dict[str, Any]:
        """获取指标摘要"""
        since = time.time() - window
        metrics = self.get_metrics(name, since)
        
        if not metrics:
            return {"count": 0}
        
        values = [m.value for m in metrics]
        
        return {
            "count": len(values),
            "min": min(values),
            "max": max(values),
            "mean": statistics.mean(values),
            "median": statistics.median(values),
            "p95": np.percentile(values, 95) if len(values) > 1 else values[0],
            "p99": np.percentile(values, 99) if len(values) > 1 else values[0]
        }
    
    async def _cleanup_old_metrics(self):
        """清理过期指标"""
        while True:
            try:
                cutoff_time = time.time() - self.retention_period
                
                with self._lock:
                    for name, metric_queue in self._metrics.items():
                        # 移除过期指标
                        while metric_queue and metric_queue[0].timestamp < cutoff_time:
                            metric_queue.popleft()
                
                await asyncio.sleep(300)  # 每5分钟清理一次
                
            except Exception as e:
                logger.error(f"指标清理失败: {e}")
                await asyncio.sleep(60)


class AnomalyDetector:
    """异常检测器"""
    
    def __init__(self, sensitivity: float = 2.0):
        self.sensitivity = sensitivity
        self._baselines: Dict[str, Dict[str, float]] = {}
        self._lock = threading.RLock()
    
    def learn_baseline(self, metric_name: str, values: List[float]):
        """学习基线指标"""
        if len(values) < 10:
            return
        
        mean = statistics.mean(values)
        stdev = statistics.stdev(values) if len(values) > 1 else 0
        
        with self._lock:
            self._baselines[metric_name] = {
                "mean": mean,
                "stdev": stdev,
                "min": min(values),
                "max": max(values)
            }
        
        logger.info(f"学习指标基线 {metric_name}: mean={mean:.2f}, stdev={stdev:.2f}")
    
    def detect_anomaly(self, metric_name: str, value: float) -> Optional[Dict[str, Any]]:
        """检测异常"""
        with self._lock:
            baseline = self._baselines.get(metric_name)
        
        if not baseline:
            return None
        
        mean = baseline["mean"]
        stdev = baseline["stdev"]
        
        # Z-score异常检测
        z_score = abs(value - mean) / (stdev + 1e-10)
        
        if z_score > self.sensitivity:
            return {
                "metric": metric_name,
                "value": value,
                "expected_range": [
                    mean - self.sensitivity * stdev,
                    mean + self.sensitivity * stdev
                ],
                "z_score": z_score,
                "severity": self._calculate_severity(z_score)
            }
        
        return None
    
    def _calculate_severity(self, z_score: float) -> AlertSeverity:
        """计算告警严重程度"""
        if z_score > 5:
            return AlertSeverity.CRITICAL
        elif z_score > 4:
            return AlertSeverity.HIGH
        elif z_score > 3:
            return AlertSeverity.MEDIUM
        else:
            return AlertSeverity.LOW


class AlertManager:
    """告警管理器"""
    
    def __init__(self, max_alerts: int = 1000):
        self.max_alerts = max_alerts
        self._alerts: Dict[str, Alert] = {}
        self._alert_rules: List[Dict[str, Any]] = []
        self._subscribers: List[Callable[[Alert], None]] = []
        self._lock = threading.RLock()
    
    def add_alert_rule(self, rule: Dict[str, Any]):
        """添加告警规则"""
        self._alert_rules.append(rule)
        logger.info(f"添加告警规则: {rule['name']}")
    
    def subscribe(self, callback: Callable[[Alert], None]):
        """订阅告警通知"""
        self._subscribers.append(callback)
    
    def trigger_alert(
        self,
        name: str,
        severity: AlertSeverity,
        message: str,
        source: str,
        tags: Dict[str, str] = None
    ) -> str:
        """触发告警"""
        alert_id = str(uuid.uuid4())
        
        alert = Alert(
            id=alert_id,
            name=name,
            severity=severity,
            message=message,
            timestamp=time.time(),
            source=source,
            tags=tags or {}
        )
        
        with self._lock:
            self._alerts[alert_id] = alert
            
            # 限制告警数量
            if len(self._alerts) > self.max_alerts:
                oldest_id = min(self._alerts.keys(), 
                              key=lambda k: self._alerts[k].timestamp)
                del self._alerts[oldest_id]
        
        # 通知订阅者
        for subscriber in self._subscribers:
            try:
                subscriber(alert)
            except Exception as e:
                logger.error(f"告警通知失败: {e}")
        
        logger.warning(f"触发告警: {name} ({severity.value}) - {message}")
        return alert_id
    
    def resolve_alert(self, alert_id: str):
        """解决告警"""
        with self._lock:
            alert = self._alerts.get(alert_id)
            if alert:
                alert.resolve()
                logger.info(f"解决告警: {alert.name}")
    
    def get_active_alerts(self) -> List[Alert]:
        """获取活跃告警"""
        with self._lock:
            return [alert for alert in self._alerts.values() if not alert.resolved]
    
    def get_alert_history(self, hours: int = 24) -> List[Alert]:
        """获取告警历史"""
        since = time.time() - hours * 3600
        with self._lock:
            return [alert for alert in self._alerts.values() 
                   if alert.timestamp >= since]


class ObservabilityEngine:
    """可观测性引擎"""
    
    def __init__(
        self,
        service_name: str = "vpp-algorithm",
        config: Optional[Dict[str, Any]] = None
    ):
        self.service_name = service_name
        self.config = config or {}
        
        # 初始化组件
        self.tracer = DistributedTracer(service_name)
        self.metrics = MetricsCollector()
        self.anomaly_detector = AnomalyDetector()
        self.alert_manager = AlertManager()
        
        # 性能监控
        self._performance_monitors: Dict[str, Any] = {}
        self._health_checks: List[Callable[[], Dict[str, Any]]] = []
        
        # 启动后台任务
        self._monitoring_task = None
        self._start_monitoring()
        
        logger.info(f"可观测性引擎初始化完成 - 服务: {service_name}")
    
    def _start_monitoring(self):
        """启动监控任务"""
        try:
            loop = asyncio.get_event_loop()
            self._monitoring_task = loop.create_task(self._monitoring_loop())
        except RuntimeError:
            # 如果没有运行的事件循环，后续手动启动
            pass
    
    async def _monitoring_loop(self):
        """监控循环"""
        while True:
            try:
                await self._collect_system_metrics()
                await self._detect_anomalies()
                await self._check_health()
                await asyncio.sleep(30)  # 每30秒执行一次
            except Exception as e:
                logger.error(f"监控循环错误: {e}")
                await asyncio.sleep(60)
    
    async def _collect_system_metrics(self):
        """收集系统指标"""
        import psutil
        
        # CPU使用率
        cpu_percent = psutil.cpu_percent()
        self.metrics.record_gauge("system.cpu.usage", cpu_percent)
        
        # 内存使用率
        memory = psutil.virtual_memory()
        self.metrics.record_gauge("system.memory.usage", memory.percent)
        self.metrics.record_gauge("system.memory.available", memory.available)
        
        # 磁盘使用率
        disk = psutil.disk_usage('/')
        self.metrics.record_gauge("system.disk.usage", disk.percent)
    
    async def _detect_anomalies(self):
        """异常检测"""
        # 检查关键指标的异常
        key_metrics = [
            "algorithm.solve_time",
            "algorithm.success_rate",
            "system.cpu.usage",
            "system.memory.usage"
        ]
        
        for metric_name in key_metrics:
            # 获取最近的指标值
            recent_metrics = self.metrics.get_metrics(metric_name, time.time() - 300)
            if not recent_metrics:
                continue
            
            # 学习基线
            values = [m.value for m in recent_metrics]
            self.anomaly_detector.learn_baseline(metric_name, values)
            
            # 检测最新值的异常
            if recent_metrics:
                latest_value = recent_metrics[-1].value
                anomaly = self.anomaly_detector.detect_anomaly(metric_name, latest_value)
                
                if anomaly:
                    self.alert_manager.trigger_alert(
                        name=f"异常检测: {metric_name}",
                        severity=anomaly["severity"],
                        message=f"指标 {metric_name} 异常: 当前值 {latest_value:.2f}, "
                               f"预期范围 {anomaly['expected_range']}, Z-score: {anomaly['z_score']:.2f}",
                        source="anomaly_detector",
                        tags={"metric": metric_name}
                    )
    
    async def _check_health(self):
        """健康检查"""
        for health_check in self._health_checks:
            try:
                result = health_check()
                if not result.get("healthy", True):
                    self.alert_manager.trigger_alert(
                        name="健康检查失败",
                        severity=AlertSeverity.HIGH,
                        message=result.get("message", "健康检查失败"),
                        source="health_check",
                        tags={"check": result.get("name", "unknown")}
                    )
            except Exception as e:
                logger.error(f"健康检查错误: {e}")
    
    def add_health_check(self, check_function: Callable[[], Dict[str, Any]]):
        """添加健康检查"""
        self._health_checks.append(check_function)
    
    def trace_operation(self, operation_name: str):
        """装饰器：追踪操作"""
        def decorator(func):
            if asyncio.iscoroutinefunction(func):
                async def async_wrapper(*args, **kwargs):
                    span = self.tracer.start_span(operation_name)
                    start_time = time.time()
                    
                    try:
                        result = await func(*args, **kwargs)
                        span.set_tag("success", True)
                        self.tracer.finish_span(span, TraceStatus.SUCCESS)
                        
                        # 记录性能指标
                        duration = time.time() - start_time
                        self.metrics.record_histogram(f"{operation_name}.duration", duration)
                        
                        return result
                    except Exception as e:
                        span.set_tag("error", str(e))
                        span.log("error", error=str(e))
                        self.tracer.finish_span(span, TraceStatus.ERROR)
                        
                        # 记录错误指标
                        self.metrics.record_counter(f"{operation_name}.errors")
                        
                        raise
                
                return async_wrapper
            else:
                def sync_wrapper(*args, **kwargs):
                    span = self.tracer.start_span(operation_name)
                    start_time = time.time()
                    
                    try:
                        result = func(*args, **kwargs)
                        span.set_tag("success", True)
                        self.tracer.finish_span(span, TraceStatus.SUCCESS)
                        
                        # 记录性能指标
                        duration = time.time() - start_time
                        self.metrics.record_histogram(f"{operation_name}.duration", duration)
                        
                        return result
                    except Exception as e:
                        span.set_tag("error", str(e))
                        span.log("error", error=str(e))
                        self.tracer.finish_span(span, TraceStatus.ERROR)
                        
                        # 记录错误指标
                        self.metrics.record_counter(f"{operation_name}.errors")
                        
                        raise
                
                return sync_wrapper
        
        return decorator
    
    def get_dashboard_data(self) -> Dict[str, Any]:
        """获取仪表板数据"""
        current_time = time.time()
        
        # 获取关键指标摘要
        key_metrics = {}
        for metric_name in ["algorithm.solve_time", "algorithm.success_rate", "system.cpu.usage"]:
            summary = self.metrics.get_metric_summary(metric_name)
            if summary["count"] > 0:
                key_metrics[metric_name] = summary
        
        # 获取活跃告警
        active_alerts = self.alert_manager.get_active_alerts()
        
        # 获取最近的链路追踪统计
        trace_stats = self._get_trace_statistics()
        
        return {
            "timestamp": current_time,
            "service_name": self.service_name,
            "metrics": key_metrics,
            "active_alerts": len(active_alerts),
            "alerts": [asdict(alert) for alert in active_alerts[:10]],  # 最近10个
            "trace_stats": trace_stats,
            "health_status": "healthy" if len(active_alerts) == 0 else "degraded"
        }
    
    def _get_trace_statistics(self) -> Dict[str, Any]:
        """获取链路追踪统计"""
        # 简化的统计实现
        return {
            "total_spans": len(self.tracer._spans),
            "active_traces": len(set(span.trace_id for span in self.tracer._spans.values() 
                                   if span.end_time is None))
        }
    
    def export_metrics(self, format: str = "prometheus") -> str:
        """导出指标"""
        if format == "prometheus":
            return self._export_prometheus_metrics()
        elif format == "json":
            return self._export_json_metrics()
        else:
            raise ValueError(f"不支持的格式: {format}")
    
    def _export_prometheus_metrics(self) -> str:
        """导出Prometheus格式指标"""
        lines = []
        
        # 导出所有指标
        for metric_name in self.metrics._metrics.keys():
            recent_metrics = self.metrics.get_metrics(metric_name, time.time() - 300)
            if recent_metrics:
                latest_metric = recent_metrics[-1]
                
                # 构建Prometheus格式
                metric_line = f"{metric_name.replace('.', '_')}"
                if latest_metric.tags:
                    tag_str = ",".join([f'{k}="{v}"' for k, v in latest_metric.tags.items()])
                    metric_line += f"{{{tag_str}}}"
                metric_line += f" {latest_metric.value} {int(latest_metric.timestamp * 1000)}"
                
                lines.append(metric_line)
        
        return "\n".join(lines)
    
    def _export_json_metrics(self) -> str:
        """导出JSON格式指标"""
        metrics_data = {}
        
        for metric_name in self.metrics._metrics.keys():
            recent_metrics = self.metrics.get_metrics(metric_name, time.time() - 300)
            if recent_metrics:
                metrics_data[metric_name] = [asdict(m) for m in recent_metrics]
        
        return json.dumps(metrics_data, indent=2)


# 全局可观测性引擎实例
_global_observability_engine: Optional[ObservabilityEngine] = None


def get_observability_engine() -> ObservabilityEngine:
    """获取全局可观测性引擎"""
    global _global_observability_engine
    if _global_observability_engine is None:
        _global_observability_engine = ObservabilityEngine()
    return _global_observability_engine


def init_observability(service_name: str = "vpp-algorithm", config: Dict[str, Any] = None):
    """初始化可观测性"""
    global _global_observability_engine
    _global_observability_engine = ObservabilityEngine(service_name, config)
    return _global_observability_engine


# 便捷装饰器
def traced(operation_name: str = None):
    """链路追踪装饰器"""
    def decorator(func):
        op_name = operation_name or f"{func.__module__}.{func.__name__}"
        return get_observability_engine().trace_operation(op_name)(func)
    return decorator 