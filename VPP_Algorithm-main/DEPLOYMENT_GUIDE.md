# VPP虚拟电厂系统部署指南 v2.0

## 📋 目录
- [系统概述](#系统概述)
- [环境要求](#环境要求)
- [快速开始](#快速开始)
- [生产环境部署](#生产环境部署)
- [监控和维护](#监控和维护)
- [故障排查](#故障排查)
- [升级指南](#升级指南)

---

## 🎯 系统概述

VPP虚拟电厂系统是一个分布式微服务架构，包含：

### 核心服务
- **算法服务 (Python)**: 储能调度、负荷预测、光伏预测
- **后端服务 (Java)**: 业务逻辑、权限管理、数据服务
- **前端界面 (React)**: 用户界面、数据可视化

### 技术栈
- **后端**: FastAPI (Python) + Spring Boot (Java)
- **数据库**: PostgreSQL + Redis
- **前端**: React + Ant Design
- **部署**: Docker + Kubernetes (可选)

---

## 🏗️ 环境要求

### 最低配置
- **CPU**: 4核心
- **内存**: 8GB RAM
- **磁盘**: 50GB 可用空间
- **网络**: 稳定的互联网连接

### 推荐配置 (生产环境)
- **CPU**: 8核心以上
- **内存**: 16GB RAM以上
- **磁盘**: 200GB SSD
- **网络**: 千兆网络

### 软件依赖
```bash
# 操作系统: Ubuntu 20.04+ / CentOS 7+ / macOS 10.15+
# Python: 3.8+
# Java: 11+
# Node.js: 16+
# PostgreSQL: 13+
# Redis: 6+
# Docker: 20.10+ (可选)
```

---

## 🚀 快速开始

### 1. 克隆项目
```bash
git clone <repository-url>
cd VPP-2.0-final
```

### 2. 环境配置
```bash
# 复制环境变量模板
cp VPP_Algorithm-main/environment.template VPP_Algorithm-main/.env

# 编辑配置文件（必须设置数据库密码等敏感信息）
vim VPP_Algorithm-main/.env
```

### 3. 数据库设置
```bash
# 安装PostgreSQL
sudo apt update
sudo apt install postgresql postgresql-contrib

# 创建数据库和用户
sudo -u postgres psql
CREATE DATABASE vpp_algorithm;
CREATE USER vpp_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE vpp_algorithm TO vpp_user;
\q

# 安装Redis
sudo apt install redis-server
sudo systemctl enable redis-server
sudo systemctl start redis-server
```

### 4. 算法服务部署
```bash
cd VPP_Algorithm-main

# 创建虚拟环境
python3 -m venv venv
source venv/bin/activate  # Linux/macOS
# 或 venv\Scripts\activate  # Windows

# 安装依赖
pip install -r requirements-lock.txt

# 数据库迁移
alembic upgrade head

# 启动服务
uvicorn api.v2.routes:app --host 0.0.0.0 --port 8000 --reload
```

### 5. Java后端部署
```bash
cd VPP-demo-main

# 编译项目
mvn clean compile

# 运行测试
mvn test

# 打包应用
mvn clean package -Dmaven.test.skip=true

# 启动应用
java -jar start/target/start-0.0.1-SNAPSHOT.jar
```

### 6. 前端部署
```bash
cd VPP-WEB-demo-de-main

# 安装依赖
npm install

# 开发模式启动
npm start

# 生产构建
npm run build
```

---

## 🏭 生产环境部署

### Docker部署 (推荐)

#### 1. 构建镜像
```bash
# 算法服务
cd VPP_Algorithm-main
docker build -t vpp-algorithm:2.0 .

# Java后端
cd VPP-demo-main
docker build -t vpp-backend:2.0 .

# 前端
cd VPP-WEB-demo-de-main
docker build -t vpp-frontend:2.0 .
```

#### 2. Docker Compose部署
```yaml
# docker-compose.yml
version: '3.8'

services:
  postgres:
    image: postgres:13
    environment:
      POSTGRES_DB: vpp_algorithm
      POSTGRES_USER: vpp_user
      POSTGRES_PASSWORD: ${DB_PASSWORD}
    volumes:
      - postgres_data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  redis:
    image: redis:6-alpine
    command: redis-server --requirepass ${REDIS_PASSWORD}
    ports:
      - "6379:6379"

  vpp-algorithm:
    image: vpp-algorithm:2.0
    environment:
      - DB_HOST=postgres
      - REDIS_HOST=redis
    depends_on:
      - postgres
      - redis
    ports:
      - "8000:8000"

  vpp-backend:
    image: vpp-backend:2.0
    depends_on:
      - postgres
    ports:
      - "8080:8080"

  vpp-frontend:
    image: vpp-frontend:2.0
    ports:
      - "3000:80"

volumes:
  postgres_data:
```

#### 3. 启动服务
```bash
# 设置环境变量
export DB_PASSWORD=your_secure_password
export REDIS_PASSWORD=your_redis_password

# 启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f vpp-algorithm
```

### Kubernetes部署

#### 1. 创建命名空间
```bash
kubectl create namespace vpp-system
```

#### 2. 配置Secret
```bash
kubectl create secret generic vpp-secrets \
  --from-literal=db-password=your_secure_password \
  --from-literal=redis-password=your_redis_password \
  --namespace=vpp-system
```

#### 3. 部署应用
```bash
# 应用所有Kubernetes配置
kubectl apply -f k8s/ --namespace=vpp-system

# 检查部署状态
kubectl get pods -n vpp-system
kubectl get services -n vpp-system
```

---

## 📊 监控和维护

### 健康检查
```bash
# 算法服务健康检查
curl http://localhost:8000/health

# Java后端健康检查
curl http://localhost:8080/actuator/health

# 前端可用性检查
curl http://localhost:3000
```

### 监控指标
```bash
# Prometheus指标端点
curl http://localhost:8000/metrics

# 系统资源监控
docker stats
```

### 日志管理
```bash
# 查看应用日志
docker-compose logs -f --tail=100 vpp-algorithm

# 日志轮转配置
# 在docker-compose.yml中添加：
logging:
  driver: "json-file"
  options:
    max-size: "100m"
    max-file: "5"
```

### 数据备份
```bash
# PostgreSQL备份
docker exec -t postgres pg_dump -U vpp_user vpp_algorithm > backup_$(date +%Y%m%d).sql

# Redis备份
docker exec redis redis-cli --rdb /data/dump.rdb
```

---

## 🔧 故障排查

### 常见问题

#### 1. 数据库连接失败
```bash
# 检查数据库状态
docker-compose ps postgres

# 检查连接配置
docker-compose logs postgres

# 测试连接
docker exec -it postgres psql -U vpp_user -d vpp_algorithm
```

#### 2. Redis连接问题
```bash
# 检查Redis状态
docker-compose ps redis

# 测试Redis连接
docker exec -it redis redis-cli ping
```

#### 3. 算法服务异常
```bash
# 查看详细日志
docker-compose logs -f vpp-algorithm

# 进入容器调试
docker exec -it vpp-algorithm bash

# 检查环境变量
docker exec vpp-algorithm env | grep DB_
```

#### 4. 性能问题
```bash
# 监控资源使用
docker stats

# 查看慢查询
docker exec postgres pg_stat_statements

# 分析内存使用
docker exec vpp-algorithm python -c "import psutil; print(psutil.virtual_memory())"
```

### 日志分析
```bash
# 错误日志过滤
docker-compose logs vpp-algorithm | grep ERROR

# 性能日志分析
docker-compose logs vpp-algorithm | grep "execution_time"

# 安全事件监控
docker-compose logs vpp-algorithm | grep "SECURITY"
```

---

## 📈 升级指南

### 版本升级流程

#### 1. 准备工作
```bash
# 备份数据
./scripts/backup.sh

# 检查当前版本
curl http://localhost:8000/health | grep version
```

#### 2. 滚动升级
```bash
# 更新镜像版本
docker-compose pull

# 逐个服务升级
docker-compose up -d --no-deps vpp-algorithm
docker-compose up -d --no-deps vpp-backend
docker-compose up -d --no-deps vpp-frontend
```

#### 3. 验证升级
```bash
# 健康检查
./scripts/health_check.sh

# 功能测试
./scripts/smoke_test.sh
```

### 回滚方案
```bash
# 快速回滚到上一版本
docker-compose down
docker-compose -f docker-compose.backup.yml up -d

# 数据库回滚
psql -U vpp_user -d vpp_algorithm < backup_before_upgrade.sql
```

---

## 🛡️ 安全配置

### SSL/TLS配置
```bash
# 生成自签名证书
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout ./certs/vpp.key -out ./certs/vpp.crt

# 配置nginx SSL
# 参考 nginx.conf.ssl 文件
```

### 防火墙设置
```bash
# Ubuntu UFW配置
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 8000/tcp
sudo ufw enable
```

### 安全扫描
```bash
# 容器安全扫描
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock \
  aquasec/trivy image vpp-algorithm:2.0

# 依赖安全检查
pip install safety
safety check -r requirements-lock.txt
```

---

## 📞 支持和联系

### 技术支持
- **文档**: [项目Wiki](./wiki)
- **问题反馈**: [GitHub Issues](./issues)
- **邮箱**: vpp-support@example.com

### 开发团队
- **架构师**: VPP Development Team
- **版本**: 2.0.0
- **更新日期**: 2024-12-29

---

## 📝 更新日志

### v2.0.0 (2024-12-29)
- ✅ 全面重构安全框架
- ✅ 优化算法引擎性能
- ✅ 完善监控和日志系统
- ✅ 增强Docker部署支持

### v1.0.0 (2024-01-01)
- 🎉 初始版本发布
- ✅ 核心功能实现
- ✅ 基础部署支持

---

**📋 部署前检查清单:**
- [ ] 环境变量配置完成
- [ ] 数据库连接测试通过
- [ ] Redis连接测试通过
- [ ] SSL证书配置正确
- [ ] 防火墙规则设置
- [ ] 备份策略制定
- [ ] 监控系统配置
- [ ] 日志收集配置
- [ ] 安全扫描通过
- [ ] 性能测试完成 