# 使用OpenJDK 8作为基础镜像
FROM openjdk:8-jdk-alpine

# 设置工作目录
WORKDIR /app

# 安装Maven
RUN apk add --no-cache maven

# 复制项目文件
COPY . /app

# 构建项目
RUN mvn clean package -DskipTests

# 查找生成的jar文件
RUN find /app -name "*.jar" -type f | head -1 > /tmp/jarfile

# 复制jar文件到根目录
RUN cp $(cat /tmp/jarfile) /app/app.jar

# 设置时区
ENV TZ=Asia/Shanghai
RUN apk add --no-cache tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 暴露端口
EXPOSE 8080

# 启动应用
CMD ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"] 