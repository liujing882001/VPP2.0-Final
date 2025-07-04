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

def fix_homepage_controller_errors():
    """修复HomePageController中的错误"""
    file_path = 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java'
    
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return
    
    print(f"\n修复 HomePageController 错误...")
    backup_file(file_path)
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    # 修复long无法转换为Date的错误
    content = re.sub(
        r'Date\s+(\w+)\s*=\s*calendar\.getTimeInMillis\(\);',
        r'Date \1 = new Date(calendar.getTimeInMillis());',
        content
    )
    
    # 修复其他类似错误
    content = re.sub(
        r'String\s+queryTime\s*=\s*command\.getQueryTime\(\);',
        r'String queryTime = command.getQueryTime() != null ? command.getQueryTime() : "month";',
        content
    )
    
    # 修复静态上下文错误
    content = re.sub(
        r'\.getCountDataTime\(\)',
        r'.getCountTime()',
        content
    )
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print("HomePageController 修复完成")

def fix_all_time_errors():
    """修复所有时间相关错误"""
    java_files = []
    
    for root, dirs, files in os.walk('vpp-web/src/main/java'):
        for file in files:
            if file.endswith('.java'):
                java_files.append(os.path.join(root, file))
    
    print(f"\n扫描 {len(java_files)} 个Java文件...")
    
    for java_file in java_files:
        try:
            with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
                content = f.read()
            
            original_content = content
            
            # 修复各种时间转换错误
            content = re.sub(r'Date\s+(\w+)\s*=\s*([^;]+)\.toEpochMilli\(\);', r'Date \1 = new Date(\2.toEpochMilli());', content)
            content = re.sub(r'(\w*DateTime)\.toInstant\(\)', r'\1.atZone(ZoneId.systemDefault()).toInstant()', content)
            content = re.sub(r'\.doubleValue\(\)\.toString\(\)', '.toString()', content)
            content = re.sub(r'String\.valueOf\(([^)]+)\)\.doubleValue\(\)', r'Double.parseDouble(String.valueOf(\1))', content)
            
            if content != original_content:
                backup_file(java_file)
                
                # 添加必要的import
                if 'ZoneId.systemDefault()' in content and 'import java.time.ZoneId;' not in content:
                    content = re.sub(r'(package [^;]+;)', r'\1\nimport java.time.ZoneId;', content)
                
                with open(java_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                
                print(f"修复了: {java_file}")
                
        except Exception as e:
            print(f"处理文件 {java_file} 时出错: {e}")

def main():
    print("=== VPP-2.0-FINAL 最终编译错误修复 ===")
    print(f"开始时间: {datetime.now()}")
    
    try:
        fix_homepage_controller_errors()
        fix_all_time_errors()
        
        print(f"\n=== 修复完成 ===")
        print(f"结束时间: {datetime.now()}")
        print("建议运行 'mvn compile' 验证修复效果")
        
    except Exception as e:
        print(f"修复过程中出现错误: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main()) 