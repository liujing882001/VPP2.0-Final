#!/usr/bin/env python3
import os
import re
import shutil
from datetime import datetime

def backup_file(file_path):
    """备份文件"""
    backup_path = f"{file_path}.backup_{datetime.now().strftime('%Y%m%d_%H%M%S')}"
    shutil.copy2(file_path, backup_path)
    print(f"已备份: {backup_path}")
    return backup_path

def fix_time_conversion_errors():
    """修复时间转换错误"""
    fixes = [
        # HomePageController.java 修复
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java',
            'replacements': [
                # 修复 calendar.toInstant().toEpochMilli() 转换为 Date 的错误
                {
                    'old': 'Date thirtyMinutesAgo = calendar.toInstant().toEpochMilli();',
                    'new': 'Date thirtyMinutesAgo = new Date(calendar.getTimeInMillis());'
                },
                # 修复 command.toInstant().toEpochMilli() 错误
                {
                    'old': 'String queryTime = command.toInstant().toEpochMilli();',
                    'new': 'String queryTime = command.getQueryTime();'
                },
                # 修复 LocalDateTime 无法转换为 Date 的错误
                {
                    'old': 'Date sDateTime = Date.from(sLocalDate);',
                    'new': 'Date sDateTime = Date.from(sLocalDate.atZone(ZoneId.systemDefault()).toInstant());'
                },
                {
                    'old': 'Date eDateTime = Date.from(eLocalDate);',
                    'new': 'Date eDateTime = Date.from(eLocalDate.atZone(ZoneId.systemDefault()).toInstant());'
                },
                # 修复 LocalDateTime 无法转换为 Instant 的错误
                {
                    'old': 'LocalDateTime无法转换为Instant',
                    'new': 'LocalDateTime.atZone(ZoneId.systemDefault()).toInstant()'
                }
            ]
        },
        # CSPGDemandController.java 修复
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/demand/CSPGDemandController.java',
            'replacements': [
                # 修复复杂的时间转换链
                {
                    'old': 'ai.setCountDataTime(Date.from(countDateTime.toInstant().atZone(ZoneId.systemDefault()).toInstant()));',
                    'new': 'ai.setCountDataTime(countDateTime);'
                }
            ]
        },
        # TradePowerController.java 修复
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/tradepower/TradePowerController.java',
            'replacements': [
                # 修复 BigDecimal 转换错误
                {
                    'old': r'\.doubleValue\(\)\.toString\(\)',
                    'new': '.toString()'
                },
                # 修复 String 调用 doubleValue() 错误
                {
                    'old': r'String\.valueOf\(([^)]+)\)\.doubleValue\(\)',
                    'new': r'Double.parseDouble(String.valueOf(\1))'
                }
            ]
        },
        # AIPredictionController.java 修复
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/loadmanagement/AIPredictionController.java',
            'replacements': [
                # 修复通用的 LocalDateTime 转换错误
                {
                    'old': r'\.toInstant\(\)',
                    'new': '.atZone(ZoneId.systemDefault()).toInstant()'
                }
            ]
        }
    ]
    
    for fix in fixes:
        file_path = fix['file']
        if not os.path.exists(file_path):
            print(f"文件不存在: {file_path}")
            continue
            
        print(f"\n处理文件: {file_path}")
        backup_file(file_path)
        
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # 应用所有替换
        for replacement in fix['replacements']:
            old_pattern = replacement['old']
            new_pattern = replacement['new']
            
            if old_pattern.startswith(r'\.') or r'\(' in old_pattern:
                # 使用正则表达式替换
                content = re.sub(old_pattern, new_pattern, content)
                print(f"  应用正则替换: {old_pattern} -> {new_pattern}")
            else:
                # 使用字符串替换
                if old_pattern in content:
                    content = content.replace(old_pattern, new_pattern)
                    print(f"  应用字符串替换: {old_pattern} -> {new_pattern}")
                else:
                    print(f"  未找到模式: {old_pattern}")
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

def add_missing_imports():
    """添加缺失的导入"""
    import_fixes = [
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java',
            'imports': ['import java.time.ZoneId;']
        },
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/demand/CSPGDemandController.java',
            'imports': ['import java.time.ZoneId;']
        },
        {
            'file': 'vpp-web/src/main/java/com/example/vvpweb/loadmanagement/AIPredictionController.java',
            'imports': ['import java.time.ZoneId;']
        }
    ]
    
    for fix in import_fixes:
        file_path = fix['file']
        if not os.path.exists(file_path):
            continue
            
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
            
        # 添加缺失的导入
        for import_statement in fix['imports']:
            if import_statement not in content:
                # 在package声明后添加import
                content = re.sub(
                    r'(package [^;]+;)',
                    r'\1\n' + import_statement,
                    content
                )
                print(f"添加导入到 {file_path}: {import_statement}")
        
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write(content)

def fix_specific_method_errors():
    """修复特定的方法错误"""
    
    # 检查并修复所有.java文件中的常见错误
    java_files = []
    for root, dirs, files in os.walk('vpp-web/src/main/java'):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    print(f"找到 {len(java_files)} 个Java文件")
    
    for java_file in java_files:
        with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
        
        original_content = content
        
        # 修复常见的时间转换错误
        content = re.sub(r'calendar\.toInstant\(\)\.toEpochMilli\(\)', 'calendar.getTimeInMillis()', content)
        content = re.sub(r'Date\.from\(([^)]+LocalDateTime[^)]+)\)', r'Date.from(\1.atZone(ZoneId.systemDefault()).toInstant())', content)
        content = re.sub(r'(\w+DateTime)\.toInstant\(\)', r'\1.atZone(ZoneId.systemDefault()).toInstant()', content)
        
        # 修复String调用doubleValue()的错误
        content = re.sub(r'String\.valueOf\(([^)]+)\)\.doubleValue\(\)', r'Double.parseDouble(String.valueOf(\1))', content)
        
        # 修复BigDecimal转换错误
        content = re.sub(r'\.doubleValue\(\)\.toString\(\)', '.toString()', content)
        
        if content != original_content:
            backup_file(java_file)
            with open(java_file, 'w', encoding='utf-8') as f:
                f.write(content)
            print(f"修复了文件: {java_file}")

def main():
    print("=== VPP-2.0-FINAL 全面编译错误修复 ===")
    print(f"开始时间: {datetime.now()}")
    
    try:
        print("\n1. 修复时间转换错误...")
        fix_time_conversion_errors()
        
        print("\n2. 添加缺失的导入...")
        add_missing_imports()
        
        print("\n3. 修复特定方法错误...")
        fix_specific_method_errors()
        
        print(f"\n=== 修复完成 ===")
        print(f"结束时间: {datetime.now()}")
        print("建议运行 'mvn compile' 验证修复效果")
        
    except Exception as e:
        print(f"修复过程中出现错误: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main()) 