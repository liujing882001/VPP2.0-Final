# VPP 2.0 虚拟电厂管理系统 - 部署指南

## 概述

VPP (Virtual Power Plant) 2.0 是一个基于 Spring Boot + PostgreSQL + Redis + Kafka 的分布式虚拟电厂管理系统。本文档提供了完整的部署指南。

## 系统架构

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Nginx         │    │   VPP App       │    │   PostgreSQL    │
│   (反向代理)     │────│   (Spring Boot) │────│   (数据库)      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
                              │
                       ┌──────┴──────┐
                       │             │
            ┌─────────────────┐    ┌─────────────────┐
            │   Redis         │    │   Kafka         │
            │   (缓存)        │    │   (消息队列)    │
            └─────────────────┘    └─────────────────┘
```

## 系统要求

### 硬件要求
- CPU: 最少 4 核心，推荐 8 核心
- 内存: 最少 8GB，推荐 16GB
- 存储: 最少 100GB 可用空间
- 网络: 稳定的网络连接

### 软件要求
- Docker: 20.10+
- Docker Compose: 1.29+
- 操作系统: Linux/macOS/Windows

## 快速开始

### 1. 下载源码
```bash
git clone <repository-url>
cd VPP-demo-main
```

### 2. 配置环境变量
```bash
# 复制环境变量模板
cp env-template.txt .env

# 编辑环境变量（请修改敏感信息）
vim .env
```

### 3. 启动服务
```bash
# 使用启动脚本
./scripts/start.sh start

# 或直接使用 Docker Compose
docker-compose up -d
```

### 4. 验证部署
```bash
# 检查服务状态
docker-compose ps

# 查看应用日志
docker-compose logs -f vpp-app

# 访问健康检查
curl http://localhost:8080/actuator/health
```

## 部署方式

### 方式一：Docker Compose (推荐)

#### 优点
- 简单易用，适合开发和测试
- 包含完整的技术栈
- 内置监控和日志

#### 使用方法
```bash
# 启动所有服务
./scripts/start.sh start

# 停止所有服务
./scripts/start.sh stop

# 重启服务
./scripts/start.sh restart

# 查看日志
./scripts/start.sh logs
```

### 方式二：Kubernetes

#### 优点
- 生产级部署
- 高可用和自动扩缩容
- 完整的运维功能

#### 使用方法
```bash
# 部署到 Kubernetes
kubectl apply -f k8s/vpp-deployment.yaml

# 查看部署状态
kubectl get pods -n vpp-system

# 查看服务
kubectl get services -n vpp-system

# 获取外部访问地址
kubectl get service vpp-service -n vpp-system
```

## 服务配置

### 端口分配
| 服务 | 端口 | 描述 |
|------|------|------|
| VPP App | 8080 | 主应用服务 |
| PostgreSQL | 5432 | 数据库 |
| Redis | 6379 | 缓存服务 |
| Kafka | 9092 | 消息队列 |
| Nginx | 80/443 | 反向代理 |
| Prometheus | 9090 | 监控 |
| Grafana | 3000 | 监控面板 |

### 环境变量说明

#### 数据库配置
```bash
DB_HOST=postgres           # 数据库主机
DB_PORT=5432              # 数据库端口  
DB_NAME=vpp_db            # 数据库名称
DB_USER=vpp_user          # 数据库用户
DB_PASSWORD=vpp_password  # 数据库密码
```

#### Redis 配置
```bash
REDIS_HOST=redis          # Redis主机
REDIS_PORT=6379           # Redis端口
REDIS_PASSWORD=           # Redis密码（可选）
```

#### Kafka 配置
```bash
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
```

#### 阿里云配置（重要）
```bash
ALIYUN_ACCESS_KEY_ID=your_key_id
ALIYUN_ACCESS_KEY_SECRET=your_key_secret
```

## 监控和日志

### Prometheus 监控
- 访问地址: http://localhost:9090
- 监控指标: 应用性能、JVM状态、数据库连接等

### Grafana 监控面板
- 访问地址: http://localhost:3000
- 默认账号: admin/admin123
- 包含预配置的 VPP 监控面板

### 日志管理
```bash
# 查看应用日志
docker-compose logs -f vpp-app

# 查看所有服务日志
docker-compose logs -f

# 日志文件位置
ls -la logs/
```

## 备份和恢复

### 数据库备份
```bash
# 备份数据库
docker exec vpp-postgres pg_dump -U vpp_user vpp_db > backup_$(date +%Y%m%d_%H%M%S).sql

# 恢复数据库
docker exec -i vpp-postgres psql -U vpp_user vpp_db < backup_file.sql
```

### 完整备份
```bash
# 停止服务
docker-compose down

# 备份数据目录
tar -czf vpp_backup_$(date +%Y%m%d_%H%M%S).tar.gz data/ logs/ config/

# 恢复数据
tar -xzf vpp_backup_YYYYMMDD_HHMMSS.tar.gz
```

## 性能调优

### JVM 参数优化
```bash
# 在 .env 文件中配置
JAVA_OPTS="-server -Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxRAMPercentage=80.0"
```

### 数据库优化
```bash
# PostgreSQL 参数调优（在 docker-compose.yml 中）
POSTGRES_SHARED_BUFFERS=256MB
POSTGRES_EFFECTIVE_CACHE_SIZE=1GB
POSTGRES_MAINTENANCE_WORK_MEM=64MB
```

### Redis 优化
```bash
# Redis 内存限制
REDIS_MAXMEMORY=512mb
REDIS_MAXMEMORY_POLICY=allkeys-lru
```

## 故障排除

### 常见问题

#### 1. 服务启动失败
```bash
# 检查日志
docker-compose logs vpp-app

# 检查端口占用
netstat -tulpn | grep :8080

# 重新构建镜像
docker-compose build --no-cache
```

#### 2. 数据库连接失败
```bash
# 检查数据库状态
docker-compose exec postgres pg_isready

# 检查网络连通性
docker-compose exec vpp-app ping postgres
```

#### 3. 内存不足
```bash
# 检查系统资源
docker stats

# 调整内存限制
vim docker-compose.yml  # 修改 memory 限制
```

### 健康检查
```bash
# 应用健康状态
curl http://localhost:8080/actuator/health

# 详细健康信息
curl http://localhost:8080/actuator/health | jq .

# 应用信息
curl http://localhost:8080/actuator/info
```

## 安全配置

### SSL/TLS 配置
1. 将证书文件放在 `ssl/` 目录
2. 修改 `nginx/conf.d/vpp.conf` 启用 HTTPS
3. 重启 Nginx 服务

### 防火墙配置
```bash
# 只开放必要端口
ufw allow 80    # HTTP
ufw allow 443   # HTTPS
ufw allow 22    # SSH
```

### 数据库安全
- 使用强密码
- 限制数据库访问IP
- 定期备份数据
- 启用审计日志

## 生产环境部署建议

1. **高可用部署**: 使用 Kubernetes 集群
2. **负载均衡**: 配置多个应用实例
3. **数据备份**: 设置自动备份策略
4. **监控告警**: 配置 Prometheus + AlertManager
5. **日志收集**: 使用 ELK 或 EFK 栈
6. **安全加固**: 配置 SSL、防火墙、访问控制

## 技术支持

- 查看应用日志定位问题
- 检查系统资源使用情况
- 参考官方文档
- 联系技术支持团队

---

## 附录

### A. 服务访问地址
- VPP 主应用: http://localhost:8080
- Swagger API: http://localhost:8080/swagger-ui/
- 健康检查: http://localhost:8080/actuator/health
- Prometheus: http://localhost:9090
- Grafana: http://localhost:3000

### B. 配置文件位置
- 主配置: `start/src/main/resources/application-docker.properties`
- Nginx配置: `nginx/conf.d/vpp.conf`
- Redis配置: `config/redis.conf`
- 监控配置: `monitoring/prometheus.yml`

### C. 数据目录
- PostgreSQL: `data/postgres/`
- Redis: `data/redis/`
- Kafka: `data/kafka/`
- 应用日志: `logs/` 