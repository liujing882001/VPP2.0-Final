# 使用分阶段构建优化镜像大小
# 第一阶段：构建阶段
FROM openjdk:11-jdk-slim as builder

# 设置工作目录
WORKDIR /build

# 安装必要的工具
RUN apt-get update && apt-get install -y \
    maven \
    wget \
    && rm -rf /var/lib/apt/lists/*

# 复制Maven配置文件
COPY pom.xml .
COPY start/pom.xml start/
COPY vpp-common/pom.xml vpp-common/
COPY vpp-domain/pom.xml vpp-domain/
COPY vpp-gateway/pom.xml vpp-gateway/
COPY vpp-kafka/pom.xml vpp-kafka/
COPY vpp-scheduling/pom.xml vpp-scheduling/
COPY vpp-service/pom.xml vpp-service/
COPY vpp-web/pom.xml vpp-web/

# 预下载依赖（Docker层缓存优化）
RUN mvn dependency:go-offline -B

# 复制源代码
COPY . .

# 构建项目
RUN mvn clean package -DskipTests -B

# 第二阶段：运行阶段
FROM openjdk:11-jre-slim

# 创建非root用户
RUN groupadd -r vpp && useradd -r -g vpp vpp

# 安装必要的运行时依赖
RUN apt-get update && apt-get install -y \
    curl \
    tzdata \
    && rm -rf /var/lib/apt/lists/*

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR文件
COPY --from=builder /build/start/target/*.jar app.jar

# 创建日志目录
RUN mkdir -p /app/logs && chown -R vpp:vpp /app

# 切换到非root用户
USER vpp

# JVM优化参数
ENV JAVA_OPTS="-server -Xms1g -Xmx2g -XX:+UseG1GC -XX:+UseContainerSupport -XX:MaxRAMPercentage=80.0"

# 健康检查
HEALTHCHECK --interval=30s --timeout=10s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}"] 