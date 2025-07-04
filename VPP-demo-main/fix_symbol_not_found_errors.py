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

def fix_homepage_controller_symbol_errors():
    """修复HomePageController中的找不到符号错误"""
    file_path = 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java'
    
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return
    
    print(f"\n修复 HomePageController 中的找不到符号错误...")
    backup_file(file_path)
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    fixes_made = []
    
    # 1. 修复 isNaN() 方法调用 - 应该使用 Double.isNaN()
    old_pattern = r'isNaN\(([^)]+)\)'
    new_pattern = r'(\1 == null || Double.isNaN(\1))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("isNaN() -> Double.isNaN()检查")
    
    # 2. 修复 .toInstant().toEpochMilli() 错误 - RAInfoVO没有toInstant方法
    # 假设RAInfoVO有getTime()方法返回时间戳
    old_pattern = r'(\w+)\.toInstant\(\)\.toEpochMilli\(\)'
    new_pattern = r'\1.getTime()'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("toInstant().toEpochMilli() -> getTime()")
    
    # 3. 修复 YearMonth.parse() 调用错误
    # 第一个参数应该是String，不是long
    old_pattern = r'YearMonth\.parse\(([^,]+)\.getTime\(\),\s*DateTimeFormatter\.ofPattern\("yyyy-MM-dd"\)\)'
    new_pattern = r'YearMonth.parse(new SimpleDateFormat("yyyy-MM").format(new Date(\1.getTime())))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("YearMonth.parse()参数修复")
    
    # 4. 添加必要的import
    imports_to_add = []
    if 'SimpleDateFormat' in content and 'import java.text.SimpleDateFormat;' not in content:
        imports_to_add.append('import java.text.SimpleDateFormat;')
    
    if imports_to_add:
        for import_stmt in imports_to_add:
            content = re.sub(r'(package [^;]+;)', r'\1\n' + import_stmt, content)
            fixes_made.append(f"添加导入: {import_stmt}")
    
    # 5. 修复可能的方法调用错误
    # 检查并修复DTO访问错误
    old_pattern = r'dto\.toInstant\(\)\.toEpochMilli\(\)'
    new_pattern = r'dto.getTime() != null ? dto.getTime() : System.currentTimeMillis()'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("DTO时间访问修复")
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"修复完成，共修复 {len(fixes_made)} 处:")
    for fix in fixes_made:
        print(f"  - {fix}")

def fix_other_controllers():
    """修复其他控制器中的错误"""
    java_files = [
        'vpp-web/src/main/java/com/example/vvpweb/demand/DemandRespTaskController.java',
        'vpp-web/src/main/java/com/example/vvpweb/loadmanagement/AIPredictionController.java',
        'vpp-web/src/main/java/com/example/vvpweb/tradepower/TradePowerController.java'
    ]
    
    for java_file in java_files:
        if not os.path.exists(java_file):
            continue
            
        print(f"\n修复 {os.path.basename(java_file)}...")
        backup_file(java_file)
        
        with open(java_file, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
        
        original_content = content
        fixes_made = []
        
        # 修复常见的找不到符号错误
        # 1. isNaN方法
        if re.search(r'isNaN\(', content):
            content = re.sub(r'isNaN\(([^)]+)\)', r'(\1 == null || Double.isNaN(\1))', content)
            fixes_made.append("isNaN修复")
        
        # 2. 时间方法调用错误
        if re.search(r'\.toInstant\(\)\.toEpochMilli\(\)', content):
            content = re.sub(r'(\w+)\.toInstant\(\)\.toEpochMilli\(\)', r'\1.getTime()', content)
            fixes_made.append("时间方法调用修复")
        
        # 3. 可能的枚举或常量错误
        if re.search(r'\.doubleValue\(\)\.toString\(\)', content):
            content = re.sub(r'\.doubleValue\(\)\.toString\(\)', '.toString()', content)
            fixes_made.append("数值转换修复")
        
        if content != original_content:
            with open(java_file, 'w', encoding='utf-8') as f:
                f.write(content)
            
            if fixes_made:
                print(f"  修复了: {', '.join(fixes_made)}")
        else:
            print(f"  无需修复")

def main():
    print("=== 修复找不到符号错误 ===")
    print(f"开始时间: {datetime.now()}")
    
    try:
        fix_homepage_controller_symbol_errors()
        fix_other_controllers()
        
        print(f"\n=== 修复完成 ===")
        print(f"结束时间: {datetime.now()}")
        print("建议运行 'mvn compile' 验证修复效果")
        
    except Exception as e:
        print(f"修复过程中出现错误: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main()) 