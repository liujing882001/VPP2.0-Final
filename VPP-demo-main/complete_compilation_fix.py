#!/usr/bin/env python3
"""
完整编译错误修复脚本
目标：解决所有54个剩余编译错误，实现100%编译成功
"""

import os
import re
import glob

def fix_chrono_localdatetime_errors():
    """修复ChronoLocalDateTime<D>的toInstant错误"""
    print("🔧 修复ChronoLocalDateTime.toInstant()错误...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 修复各种LocalDateTime.toInstant()错误，需要添加ZoneOffset
            patterns_to_fix = [
                # 修复复杂表达式后的.toInstant()
                (r'(LocalDateTime\.[^)]+\([^)]*\)[^.]*?)\.toInstant\(\)', 
                 r'\1.atZone(ZoneId.systemDefault()).toInstant()'),
                
                # 修复变量.toInstant()（排除Date类型）
                (r'(?<!Date\.)(?<!date\.)(\w+(?:\.\w+)*)\.toInstant\(\)(?=\s*[;\)])', 
                 r'\1.atZone(ZoneId.systemDefault()).toInstant()'),
                
                # 修复方法链式调用中的toInstant()
                (r'(\w+(?:\.\w+)*(?:\([^)]*\))?)\.toInstant\(\)(?=\.)', 
                 r'\1.atZone(ZoneId.systemDefault()).toInstant()'),
            ]
            
            for pattern, replacement in patterns_to_fix:
                content = re.sub(pattern, replacement, content)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 修复: {os.path.basename(file_path)}")
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def fix_date_atzone_errors():
    """修复Date.atZone()错误 - Date类没有atZone方法"""
    print("🔧 修复Date.atZone()错误...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # Date类没有atZone方法，需要先转换为Instant
            fixes = [
                # 修复静态Date.atZone()调用
                (r'Date\.atZone\(', 'new Date().toInstant().atZone('),
                
                # 修复变量.atZone()调用
                (r'(\w*[Dd]ate\w*)\.atZone\(', r'\1.toInstant().atZone('),
                
                # 修复复杂的Date表达式.atZone()
                (r'([^.\s]+\([^)]*\))\.atZone\(ZoneId\.systemDefault\(\)\)', 
                 r'\1.toInstant().atZone(ZoneId.systemDefault())'),
            ]
            
            for pattern, replacement in fixes:
                content = re.sub(pattern, replacement, content)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 修复: {os.path.basename(file_path)}")
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def fix_type_conversion_errors():
    """修复类型转换错误"""
    print("🔧 修复类型转换错误...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 逐行处理，更精确的类型转换修复
            lines = content.split('\n')
            fixed_lines = []
            
            for line in lines:
                original_line = line
                
                # 修复LocalDateTime到Date的转换
                if re.search(r'Date\s+\w+\s*=\s*LocalDateTime\.[^;]+;', line):
                    line = re.sub(
                        r'Date\s+(\w+)\s*=\s*(LocalDateTime\.[^;]+);',
                        r'Date \1 = Date.from(\2.atZone(ZoneId.systemDefault()).toInstant());',
                        line
                    )
                
                # 修复Date到LocalDateTime的转换
                elif re.search(r'LocalDateTime\s+\w+\s*=\s*[^;]*[Dd]ate[^;]*;', line):
                    line = re.sub(
                        r'LocalDateTime\s+(\w+)\s*=\s*([^;]*[Dd]ate[^;]*);',
                        r'LocalDateTime \1 = \2.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();',
                        line
                    )
                
                # 修复BigDecimal到String的转换
                elif re.search(r'String\s+\w+\s*=\s*[^;]*BigDecimal[^;]*;', line):
                    line = re.sub(
                        r'String\s+(\w+)\s*=\s*([^;]*BigDecimal[^;]*);',
                        r'String \1 = String.valueOf(\2);',
                        line
                    )
                
                if line != original_line:
                    print(f"    🔄 转换: {original_line.strip()} -> {line.strip()}")
                
                fixed_lines.append(line)
            
            content = '\n'.join(fixed_lines)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 修复: {os.path.basename(file_path)}")
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def fix_yearmonth_errors():
    """修复YearMonth相关错误"""
    print("🔧 修复YearMonth相关错误...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # YearMonth没有toInstant方法，需要特殊处理
            yearmonth_fixes = [
                # 基本YearMonth.toInstant()错误
                (r'(\w+)\.toInstant\(\)\.atZone\([^)]+\)\.toLocalDate\(\)\.atEndOfMonth\(\)\.toInstant\(\)',
                 r'\1.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()'),
                
                # 复杂的YearMonth表达式
                (r'request\.getDate\(\)\.toInstant\(\)\.atZone\([^)]+\)\.toLocalDate\(\)\.atEndOfMonth\(\)\.toInstant\(\)',
                 r'request.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant()'),
            ]
            
            for pattern, replacement in yearmonth_fixes:
                content = re.sub(pattern, replacement, content)
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 修复: {os.path.basename(file_path)}")
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def fix_zoneddatetime_errors():
    """修复ZonedDateTime重复atZone调用错误"""
    print("🔧 修复ZonedDateTime重复atZone调用错误...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # ZonedDateTime已经是带时区的，不需要再次调用atZone
            content = re.sub(
                r'(\w+)\.atZone\(ZoneId\.systemDefault\(\)\)\.atZone\(',
                r'\1.atZone(',
                content
            )
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 修复: {os.path.basename(file_path)}")
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def add_missing_imports():
    """添加所有缺失的导入语句"""
    print("🔧 添加缺失的导入语句...")
    
    java_files = glob.glob("vpp-service/**/*.java", recursive=True)
    
    for file_path in java_files:
        try:
            with open(file_path, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            imports_needed = set()
            
            # 检查各种需要的导入
            import_checks = [
                ('ZoneId.systemDefault()', 'import java.time.ZoneId;'),
                ('LocalDateTime', 'import java.time.LocalDateTime;'),
                ('LocalDate', 'import java.time.LocalDate;'),
                ('LocalTime', 'import java.time.LocalTime;'),
                ('Instant', 'import java.time.Instant;'),
                ('YearMonth', 'import java.time.YearMonth;'),
                ('ZonedDateTime', 'import java.time.ZonedDateTime;'),
                ('Date', 'import java.util.Date;'),
                ('BigDecimal', 'import java.math.BigDecimal;'),
            ]
            
            for check, import_stmt in import_checks:
                if check in content and import_stmt not in content:
                    imports_needed.add(import_stmt)
            
            # 添加导入语句到package声明后
            if imports_needed:
                package_match = re.search(r'package [^;]+;', content)
                if package_match:
                    insert_pos = package_match.end()
                    imports_str = '\n' + '\n'.join(sorted(imports_needed))
                    content = content[:insert_pos] + imports_str + content[insert_pos:]
                    print(f"  📦 添加导入: {os.path.basename(file_path)} ({len(imports_needed)}个)")
            
            if content != original_content:
                with open(file_path, 'w', encoding='utf-8') as f:
                    f.write(content)
                
        except Exception as e:
            print(f"  ❌ 错误 {file_path}: {e}")

def fix_specific_problematic_files():
    """修复特定的问题文件"""
    print("🔧 修复特定问题文件...")
    
    # 修复NodeEpService.java中的YearMonth.toInstant()错误
    nodeep_file = "vpp-service/src/main/java/com/example/vvpservice/nodeep/service/NodeEpService.java"
    if os.path.exists(nodeep_file):
        try:
            with open(nodeep_file, 'r', encoding='utf-8') as f:
                content = f.read()
            
            original_content = content
            
            # 特殊处理YearMonth.toInstant()错误
            # YearMonth没有toInstant方法，需要转换为LocalDate然后处理
            content = re.sub(
                r'YearMonth\.([^.]+)\.toInstant\(\)',
                r'YearMonth.\1.atDay(1).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()',
                content
            )
            
            if content != original_content:
                with open(nodeep_file, 'w', encoding='utf-8') as f:
                    f.write(content)
                print(f"  ✅ 特殊修复: NodeEpService.java")
                
        except Exception as e:
            print(f"  ❌ 错误 NodeEpService.java: {e}")

def compile_and_check():
    """编译并检查结果"""
    print("\n🔍 验证修复结果...")
    
    import subprocess
    
    try:
        # 运行编译
        result = subprocess.run(
            ['mvn', 'compile', '-q'],
            env={**os.environ, 'JAVA_HOME': subprocess.check_output(
                ['/usr/libexec/java_home'], 
                text=True
            ).strip()},
            capture_output=True,
            text=True,
            cwd='.'
        )
        
        # 统计错误
        error_count = result.stderr.count('错误:')
        
        if result.returncode == 0:
            print("🎉 编译成功！所有错误已修复！")
            return True, 0
        else:
            print(f"⚠️ 还有 {error_count} 个编译错误需要处理")
            
            # 显示前5个错误
            errors = [line for line in result.stderr.split('\n') if '错误:' in line]
            for i, error in enumerate(errors[:5]):
                print(f"  {i+1}. {error.split('错误:')[1].strip()}")
            
            return False, error_count
            
    except Exception as e:
        print(f"❌ 编译检查失败: {e}")
        return False, -1

def main():
    """主修复函数"""
    print("🚀 开始完整编译错误修复...")
    print("🎯 目标：修复所有54个编译错误，实现100%编译成功")
    print("=" * 60)
    
    # 修复步骤
    fix_chrono_localdatetime_errors()
    print()
    
    fix_date_atzone_errors()
    print()
    
    fix_zoneddatetime_errors()
    print()
    
    fix_type_conversion_errors()
    print()
    
    fix_yearmonth_errors()
    print()
    
    fix_specific_problematic_files()
    print()
    
    add_missing_imports()
    print()
    
    # 验证结果
    success, error_count = compile_and_check()
    
    print("\n" + "=" * 60)
    if success:
        print("🎊 恭喜！项目现在可以100%编译成功！")
        print("✅ 所有模块编译通过")
        print("🟢 文件树将显示绿色状态")
    else:
        print(f"📈 编译错误从54个减少到{error_count}个")
        print(f"🎯 修复进度：{((54-error_count)/54*100):.1f}%")
        print("🔄 需要进一步修复剩余错误")
    
    return success

if __name__ == "__main__":
    main() 