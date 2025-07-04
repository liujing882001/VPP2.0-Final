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

def fix_homepage_long_to_date_errors():
    """专门修复HomePageController中的long转Date错误"""
    file_path = 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java'
    
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return
    
    print(f"\n专门修复 HomePageController 中的 long 转 Date 错误...")
    backup_file(file_path)
    
    with open(file_path, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    # 修复具体的行
    fixes_made = []
    
    for i, line in enumerate(lines):
        original_line = line
        line_num = i + 1
        
        # 修复所有 long 无法转换为 Date 的错误
        if 'Date ' in line and '.toEpochMilli()' in line:
            # 找到所有 Date variable = something.toEpochMilli(); 的模式
            pattern = r'(Date\s+\w+\s*=\s*)([^;]+\.toEpochMilli\(\);)'
            match = re.search(pattern, line)
            if match:
                prefix = match.group(1)
                expr = match.group(2).replace('.toEpochMilli();', '.toEpochMilli()));')
                # 将 long 包装在 new Date() 中
                new_expr = f'new Date({expr}'
                lines[i] = line.replace(match.group(0), prefix + new_expr)
                fixes_made.append(f"行{line_num}: long转Date修复")
        
        # 修复静态上下文错误，将 getCountDataTime() 替换为具体的值或正确的静态调用
        if '.getCountDataTime()' in line:
            lines[i] = line.replace('.getCountDataTime()', '.getCountTime()')
            fixes_made.append(f"行{line_num}: 静态上下文修复")
        
        # 修复特定的queryTime错误
        if 'String queryTime = command.getQueryTime()' in line and 'null' not in line:
            lines[i] = line.replace(
                'String queryTime = command.getQueryTime();',
                'String queryTime = command.getQueryTime() != null ? command.getQueryTime() : "month";'
            )
            fixes_made.append(f"行{line_num}: queryTime空值检查")
    
    # 写入修复后的内容
    with open(file_path, 'w', encoding='utf-8') as f:
        f.writelines(lines)
    
    print(f"HomePageController 修复完成，共修复 {len(fixes_made)} 处:")
    for fix in fixes_made:
        print(f"  - {fix}")

def main():
    print("=== HomePageController 专门修复 ===")
    print(f"开始时间: {datetime.now()}")
    
    try:
        fix_homepage_long_to_date_errors()
        
        print(f"\n=== 修复完成 ===")
        print(f"结束时间: {datetime.now()}")
        print("建议运行 'mvn compile' 验证修复效果")
        
    except Exception as e:
        print(f"修复过程中出现错误: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main()) 