#!/bin/bash

# VPP 2.0 虚拟电厂管理系统 - 启动脚本

set -e

echo "VPP 2.0 虚拟电厂管理系统启动脚本"
echo "================================"

case "${1:-start}" in
    start)
        echo "启动VPP服务..."
        # 检查Docker
        if ! command -v docker &> /dev/null; then
            echo "错误: Docker 未安装"
            exit 1
        fi
        
        # 启动服务
        docker-compose pull
        docker-compose build --no-cache
        docker-compose up -d
        
        echo "服务启动完成！"
        echo "访问地址: http://localhost:8080"
        ;;
    stop)
        echo "停止VPP服务..."
        docker-compose down
        ;;
    restart)
        echo "重启VPP服务..."
        docker-compose down
        docker-compose up -d
        ;;
    logs)
        docker-compose logs -f vpp-app
        ;;
    *)
        echo "用法: $0 {start|stop|restart|logs}"
        exit 1
        ;;
esac
