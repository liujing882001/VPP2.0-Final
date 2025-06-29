#!/usr/bin/env python3
"""
VPP项目编译错误修复脚本
用于批量修复常见的编译错误，包括：
1. 添加缺失的ElectricityPrice字段和方法
2. 修复方法命名问题（下划线转驼峰）
3. 修复Date和LocalDateTime类型转换
"""

import os
import re
import glob

def fix_electricity_price_class():
    """修复ElectricityPrice类，添加缺失的字段"""
    file_path = "vpp-service/src/main/java/com/example/vvpservice/electricitytrading/model/ElectricityPrice.java"
    
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 检查是否已经有sTime和eTime字段
    if 'String sTime' in content:
        print("ElectricityPrice已经包含sTime字段")
        return
    
    # 在类定义后添加sTime和eTime字段
    new_fields = '''
	@ApiModelProperty("开始时间")
	private String sTime;
	
	@ApiModelProperty("结束时间")
	private String eTime;
'''
    
    # 在name字段后添加新字段
    content = content.replace(
        '@ApiModelProperty("字段名称")\n\tprivate String name;',
        '@ApiModelProperty("字段名称")\n\tprivate String name;' + new_fields
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"✅ 修复了 {file_path}")

def fix_method_naming_issues():
    """修复方法命名问题：将下划线命名转换为驼峰命名"""
    
    # 定义需要修复的方法映射
    method_mappings = {
        'getParam_name': 'getParamName',
        'setParam_name': 'setParamName',
        'getParam_id': 'getParamId',
        'setParam_id': 'setParamId',
        'getDefault_value': 'getDefaultValue',
        'setDefault_value': 'setDefaultValue',
        'getSTime': 'getStime',  # 修正大小写
        'setSTime': 'setStime',
        'getETime': 'getEtime',
        'setETime': 'setEtime'
    }
    
    # 查找所有Java文件
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 替换方法调用
            for old_method, new_method in method_mappings.items():
                content = content.replace(f'.{old_method}(', f'.{new_method}(')
                content = content.replace(f'::{old_method}', f'::{new_method}')
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 修复了方法命名: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def fix_datetime_conversion_issues():
    """修复Date和LocalDateTime转换问题"""
    
    # 查找有问题的文件
    problem_files = [
        "vpp-service/src/main/java/com/example/vvpservice/point/service/mappingStrategy/impl/CalculatePointMappingStrategy.java",
        "vpp-service/src/main/java/com/example/vvpservice/demand/service/DemandCalendarServiceImpl.java"
    ]
    
    for file_path in problem_files:
        if not os.path.exists(file_path):
            print(f"文件不存在: {file_path}")
            continue
            
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 修复LocalDateTime转Date的问题
            content = re.sub(
                r'Date\s+(\w+)\s*=\s*([^;]*LocalDateTime[^;]*);',
                r'Date \1 = java.sql.Timestamp.valueOf(\2);',
                content
            )
            
            # 修复Date转LocalDateTime的问题
            content = re.sub(
                r'LocalDateTime\s+(\w+)\s*=\s*([^;]*\.getTime\(\)[^;]*);',
                r'LocalDateTime \1 = new java.sql.Timestamp(\2).toLocalDateTime();',
                content
            )
            
            # 修复LocalDateTime.getTime()调用
            content = content.replace('.getTime()', '.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()')
            
            # 修复BigDecimal转String问题
            content = re.sub(
                r'String\s+(\w+)\s*=\s*([^;]*BigDecimal[^;]*);',
                r'String \1 = \2.toString();',
                content
            )
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 修复了类型转换: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def main():
    """主函数"""
    print("🔧 开始修复VPP项目编译错误...")
    
    # 切换到正确的目录
    if os.path.exists("VPP-demo-main"):
        os.chdir("VPP-demo-main")
    
    # 执行修复
    fix_electricity_price_class()
    fix_method_naming_issues()
    fix_datetime_conversion_issues()
    
    print("🎉 编译错误修复完成！")
    print("\n请运行以下命令重新编译：")
    print("export JAVA_HOME=$(/usr/libexec/java_home) && mvn clean compile")

if __name__ == "__main__":
    main() 