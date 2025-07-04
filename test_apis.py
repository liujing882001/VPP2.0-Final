#!/usr/bin/env python3
"""
DeepEngine Platform API测试脚本
验证所有三大核心模块的API功能
"""

import requests
import json
import time
from datetime import datetime

BASE_URL = "http://localhost:8000"

def test_api(endpoint, description):
    """测试API端点"""
    try:
        print(f"🧪 测试: {description}")
        response = requests.get(f"{BASE_URL}{endpoint}", timeout=5)
        
        if response.status_code == 200:
            data = response.json()
            print(f"✅ 成功 - 状态: {data.get('status', 'N/A')}")
            
            # 显示关键数据
            if 'data' in data:
                if 'solar_power' in data['data']:
                    print(f"   📊 太阳能: {data['data']['solar_power']['current']}kW")
                elif 'current_load' in data['data']:
                    print(f"   📊 当前负荷: {data['data']['current_load']['value']}kW")
                elif 'market_revenue' in data['data']:
                    print(f"   📊 市场收益: {data['data']['market_revenue']['today']}元")
            
            return True
        else:
            print(f"❌ 失败 - HTTP {response.status_code}")
            return False
            
    except requests.exceptions.ConnectionError:
        print(f"❌ 连接失败 - 服务未启动")
        return False
    except Exception as e:
        print(f"❌ 错误: {str(e)}")
        return False

def main():
    """主测试函数"""
    print("🚀 DeepEngine分布式能源管理平台 - API测试")
    print("=" * 80)
    print(f"📅 测试时间: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print(f"🌐 测试地址: {BASE_URL}")
    print()
    
    # 等待服务启动
    print("⏳ 等待服务启动...")
    time.sleep(3)
    
    # 测试列表
    tests = [
        # 基础API
        ("/", "根API"),
        ("/health", "健康检查"),
        ("/docs", "API文档"),
        
        # PowerGen模块
        ("/api/v1/powergen/dashboard", "PowerGen仪表盘"),
        ("/api/v1/powergen/forecast", "PowerGen预测"),
        ("/api/v1/powergen/devices", "PowerGen设备"),
        ("/api/v1/powergen/optimization", "PowerGen优化建议"),
        
        # SmartLoad模块
        ("/api/v1/smartload/dashboard", "SmartLoad仪表盘"),
        ("/api/v1/smartload/buildings", "SmartLoad建筑"),
        
        # VPPCloud模块
        ("/api/v1/vppcloud/dashboard", "VPPCloud仪表盘"),
        ("/api/v1/vppcloud/markets", "VPPCloud市场"),
        ("/api/v1/vppcloud/resources", "VPPCloud资源"),
        ("/api/v1/vppcloud/participants", "VPPCloud参与者"),
    ]
    
    # 执行测试
    success_count = 0
    total_count = len(tests)
    
    for endpoint, description in tests:
        if test_api(endpoint, description):
            success_count += 1
        print()
        time.sleep(0.5)
    
    # 总结
    print("=" * 80)
    print("📊 测试总结")
    print(f"✅ 成功: {success_count}/{total_count}")
    print(f"❌ 失败: {total_count - success_count}/{total_count}")
    print(f"📈 成功率: {success_count/total_count*100:.1f}%")
    
    if success_count == total_count:
        print("\n🎉 所有API测试通过！DeepEngine平台运行正常！")
        print("\n🌟 核心功能验证:")
        print("   ⚡ PowerGen - VPP算法集成的智能发电管理")
        print("   🏠 SmartLoad - AI驱动的智慧用能管理")
        print("   ☁️  VPPCloud - 企业级虚拟电厂运营")
        print("\n🔗 访问地址:")
        print(f"   📱 前端界面: simple-frontend.html")
        print(f"   📡 API文档: {BASE_URL}/docs")
        print(f"   🏥 健康检查: {BASE_URL}/health")
    else:
        print(f"\n⚠️  有{total_count - success_count}个API测试失败，请检查服务状态")

if __name__ == "__main__":
    main() 