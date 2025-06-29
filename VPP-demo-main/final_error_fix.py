#!/usr/bin/env python3
"""
最终编译错误修复脚本
修复所有剩余的类型转换和方法调用问题
"""

import os
import re
import glob

def fix_all_remaining_type_issues():
    """修复所有剩余的类型转换问题"""
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 修复 Date.atZone() 错误 - Date类没有atZone方法
            content = re.sub(
                r'(\w+)\.atZone\(([^)]+)\.toInstant\(\)\)',
                r'\1.toInstant().atZone(\2)',
                content
            )
            
            # 修复 LocalDateTime转Date时多余的.toInstant()
            content = re.sub(
                r'Date\.from\((\w+)\.atZone\(([^)]+)\)\.toInstant\(\)\.toInstant\(\)\)',
                r'Date.from(\1.atZone(\2).toInstant())',
                content
            )
            
            # 修复BigDecimal转String问题
            content = re.sub(
                r'String\s+(\w+)\s*=\s*([^;]*BigDecimal[^;]+);',
                r'String \1 = String.valueOf(\2);',  
                content
            )
            
            # 修复List<BigDecimal>转String[]问题
            content = re.sub(
                r'String\[\]\s+(\w+)\s*=\s*([^;]*List<BigDecimal>[^;]+);',
                r'String[] \1 = \2.stream().map(String::valueOf).toArray(String[]::new);',
                content
            )
            
            # 修复Optional.orElse(null)问题
            content = re.sub(
                r'\.orElse\(null\)\.(\w+)',
                r'.map(v -> v.\1).orElse(null)',
                content
            )
            
            # 修复Date.valueOf问题 - Date没有valueOf方法
            content = re.sub(
                r'Date\.valueOf\(([^)]+)\)',
                r'java.sql.Date.valueOf(\1)',
                content
            )
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 修复了类型转换问题: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def fix_method_not_found_errors():
    """修复找不到方法的错误"""
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    java_files.extend(glob.glob("vpp-domain/**/*.java", recursive=True))
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 修复getStime/getEtime方法名问题
            method_fixes = [
                (r'\.getStime\(\)', '.getSTime()'),
                (r'\.setStime\(', '.setSTime('),
                (r'\.getEtime\(\)', '.getETime()'),
                (r'\.setEtime\(', '.setETime('),
                (r'::getStime', '::getSTime'),
                (r'::getEtime', '::getETime'),
                (r'::setStime', '::setSTime'),
                (r'::setEtime', '::setETime'),
            ]
            
            for old_pattern, new_pattern in method_fixes:
                content = re.sub(old_pattern, new_pattern, content)
            
            # 修复可能的其他下划线命名问题
            content = re.sub(r'\.getParam_(\w+)\(\)', lambda m: f'.getParam{m.group(1).capitalize()}()', content)
            content = re.sub(r'\.setParam_(\w+)\(', lambda m: f'.setParam{m.group(1).capitalize()}(', content)
            content = re.sub(r'\.getDefault_(\w+)\(\)', lambda m: f'.getDefault{m.group(1).capitalize()}()', content)
            content = re.sub(r'\.setDefault_(\w+)\(', lambda m: f'.setDefault{m.group(1).capitalize()}(', content)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 修复了方法调用: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def add_missing_imports():
    """添加缺失的导入语句"""
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    import_mappings = {
        'stream()': 'java.util.stream.Stream',
        'String.valueOf': None,  # 内置方法，不需要导入
        'BigDecimal': 'java.math.BigDecimal',
        'LocalDateTime': 'java.time.LocalDateTime',
        'ZoneId': 'java.time.ZoneId',
        'Instant': 'java.time.Instant',
        'Collectors': 'java.util.stream.Collectors',
    }
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 检查是否需要添加stream相关的导入
            if '.stream()' in content and 'import java.util.stream.Stream;' not in content:
                if 'import java.' in content:
                    content = re.sub(
                        r'(import java\.[^;]+;)',
                        r'\1\nimport java.util.stream.Stream;',
                        content,
                        count=1
                    )
                    
            if 'Collectors.' in content and 'import java.util.stream.Collectors;' not in content:
                if 'import java.util.stream.Stream;' in content:
                    content = re.sub(
                        r'(import java\.util\.stream\.Stream;)',
                        r'\1\nimport java.util.stream.Collectors;',
                        content
                    )
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 添加了缺失的导入: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def fix_lombok_issues():
    """修复Lombok相关问题"""
    
    entity_files = glob.glob("vpp-domain/**/entity/*.java", recursive=True)
    
    for file_path in entity_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 确保有@Data注解的类导入了lombok.Data
            if '@Data' in content and 'import lombok.Data;' not in content:
                if 'package ' in content:
                    content = re.sub(
                        r'(package [^;]+;)',
                        r'\1\n\nimport lombok.Data;',
                        content
                    )
                    
            # 确保有@Getter @Setter的类也有相应导入
            if '@Getter' in content and 'import lombok.Getter;' not in content:
                if 'import lombok.Data;' in content:
                    content = re.sub(
                        r'(import lombok\.Data;)',
                        r'\1\nimport lombok.Getter;',
                        content
                    )
                    
            if '@Setter' in content and 'import lombok.Setter;' not in content:
                if 'import lombok.Getter;' in content:
                    content = re.sub(
                        r'(import lombok\.Getter;)',
                        r'\1\nimport lombok.Setter;',
                        content
                    )
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"✅ 修复了Lombok导入: {file_path}")
                
        except Exception as e:
            print(f"❌ 处理文件失败 {file_path}: {e}")

def main():
    """主函数"""
    print("🔧 最终修复所有编译错误...")
    
    print("\n1. 修复所有类型转换问题...")
    fix_all_remaining_type_issues()
    
    print("\n2. 修复方法找不到错误...")
    fix_method_not_found_errors()
    
    print("\n3. 添加缺失的导入语句...")
    add_missing_imports()
    
    print("\n4. 修复Lombok相关问题...")
    fix_lombok_issues()
    
    print("\n🎉 最终修复完成！")
    print("\n请运行编译验证：")
    print("export JAVA_HOME=$(/usr/libexec/java_home) && mvn clean compile")

if __name__ == "__main__":
    main() 