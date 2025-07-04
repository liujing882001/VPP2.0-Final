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

def fix_static_method_calls():
    """修复静态方法调用错误"""
    file_path = 'vpp-web/src/main/java/com/example/vvpweb/flexibleresourcemanagement/HomePageController.java'
    
    if not os.path.exists(file_path):
        print(f"文件不存在: {file_path}")
        return
    
    print(f"\n修复静态方法调用错误...")
    backup_file(file_path)
    
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()
    
    fixes_made = []
    
    # 修复 IotTsKvMeteringDevice96.getCountTime() 静态调用错误
    old_pattern = r'IotTsKvMeteringDevice96\.getCountTime\(\)'
    new_pattern = r'obj.getCountTime()'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("IotTsKvMeteringDevice96.getCountTime() -> obj.getCountTime()")
    
    # 修复 AiLoadForecasting.getCountTime() 静态调用错误
    old_pattern = r'AiLoadForecasting\.getCountTime\(\)'
    new_pattern = r'obj.getCountTime()'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("AiLoadForecasting.getCountTime() -> obj.getCountTime()")
    
    # 修复所有 calendar.getTimeInMillis() 转换为 Date 的错误
    old_pattern = r'generateResponses\(startDate,\s*calendar\.getTimeInMillis\(\)\)'
    new_pattern = r'generateResponses(startDate, new Date(calendar.getTimeInMillis()))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("generateResponses参数修复")
    
    # 修复 pointService.getDValuesByTime 调用中的 long 转 Date 错误
    old_pattern = r'pointService\.getDValuesByTime\(([^,]+),\s*([^,]+),\s*calendar\.getTimeInMillis\(\)\)'
    new_pattern = r'pointService.getDValuesByTime(\1, \2, new Date(calendar.getTimeInMillis()))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("pointService.getDValuesByTime参数修复")
    
    # 修复其他 calendar.getTimeInMillis() 作为Date参数的情况
    old_pattern = r'findAllByNodeIdAndPointDescAndCountDataTimeBetween\(([^,]+),\s*([^,]+),\s*([^,]+),\s*calendar\.getTimeInMillis\(\)\)'
    new_pattern = r'findAllByNodeIdAndPointDescAndCountDataTimeBetween(\1, \2, \3, new Date(calendar.getTimeInMillis()))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("findAllByNodeIdAndPointDescAndCountDataTimeBetween参数修复")
    
    # 修复其他类似的long转Date错误
    old_pattern = r'findAllBySystemIdAndNodeIde\(([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*([^,]+),\s*calendar\.getTimeInMillis\(\)\)'
    new_pattern = r'findAllBySystemIdAndNodeIde(\1, \2, \3, \4, \5, new Date(calendar.getTimeInMillis()))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("findAllBySystemIdAndNodeIde参数修复")
    
    # 修复 findByDateNodeIdSystemId 参数
    old_pattern = r'findByDateNodeIdSystemId\(([^,]+),\s*([^,]+),\s*([^,]+),\s*calendar\.getTimeInMillis\(\)\)'
    new_pattern = r'findByDateNodeIdSystemId(\1, \2, \3, new Date(calendar.getTimeInMillis()))'
    if re.search(old_pattern, content):
        content = re.sub(old_pattern, new_pattern, content)
        fixes_made.append("findByDateNodeIdSystemId参数修复")
    
    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    
    print(f"修复完成，共修复 {len(fixes_made)} 处:")
    for fix in fixes_made:
        print(f"  - {fix}")

def main():
    print("=== 修复静态方法调用错误 ===")
    print(f"开始时间: {datetime.now()}")
    
    try:
        fix_static_method_calls()
        
        print(f"\n=== 修复完成 ===")
        print(f"结束时间: {datetime.now()}")
        print("建议运行 'mvn compile' 验证修复效果")
        
    except Exception as e:
        print(f"修复过程中出现错误: {e}")
        return 1
    
    return 0

if __name__ == "__main__":
    exit(main()) 