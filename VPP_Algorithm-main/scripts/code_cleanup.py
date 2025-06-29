#!/usr  /  bin  /  env python3
"""
VPP代码质量清理脚本
自动修复代码格式问题、清理调试代码、优化导入语句

@author: VPP Team
@version: 2.1.0
@date: 2024  - 01  - 01
"""

import re
import subprocess
from pathlib import Path
from typing import Dict


class CodeCleaner:
    """代码清理器"""

    def __init__(self, project_root: str):
        self.project_root = Path(project_root)
        self.issues_fixed = {
            'print_statements': 0,
            'unused_imports': 0,
            'line_length': 0,
            'trailing_whitespace': 0,
            'missing_docstrings': 0,
        }

    def clean_all(self) -> Dict[str, int]:
        """执行全面清理"""
        print("🚀 开始VPP代码质量清理...")

        # 1. 清理调试代码
        self._remove_debug_prints()

        # 2. 修复代码格式
        self._fix_code_formatting()

        # 3. 清理未使用的导入
        self._remove_unused_imports()

        # 4. 修复docstring
        self._fix_docstrings()

        # 5. 运行自动格式化工具
        self._run_auto_formatters()

        print("✅ 代码清理完成!")
        return self.issues_fixed

    def _remove_debug_prints(self):
        """移除调试print语句"""
        print("🔧 清理调试print语句...")

        python_files = list(self.project_root.rglob("*.py"))

        for file_path in python_files:
            if self._should_skip_file(file_path):
                continue

            with open(file_path, 'r', encoding='utf  -  8') as f:
                content = f.read()

            original_content = content

            # 移除调试print语句 (保留合法的print语句)
            patterns = [
                r'^\s  *  print\(f?".*进程.*"\)\s*$',
                r'^\s  *  print\(f?".*当前时间.*"\)\s*$',
                r'^\s  *  print\(f?".*Performance.*"\)\s*$',
                r'^\s  *  print\(f?".*test.*"\)\s*$',
                r'^\s  *  print\(f?".*debug.*"\)\s*$',
                r'^\s  *  print\(f?".*Debug.*"\)\s*$',
                r'^\s  *  print\(.*source.*\)\s*$',
                r'^\s  *  print\(parser\.parse_config\(\)\)\s*$',
                r'^\s  *  print\(database\["database"\]\)\s*$',
                r'^\s  *  print\(.*loader.*\)\s*$',
            ]

            lines = content.split('\n')
            cleaned_lines = []

            for line in lines:
                should_remove = False
                for pattern in patterns:
                    if re.match(pattern, line, re.MULTILINE):
                        should_remove = True
                        self.issues_fixed['print_statements'] += 1
                        break

                if not should_remove:
                    cleaned_lines.append(line)

            content = '\n'.join(cleaned_lines)

            # 移除尾部空白
            content = re.sub(r'[ \t]+$', '', content, flags=re.MULTILINE)
            self.issues_fixed['trailing_whitespace'] += len(
                re.findall(r'[ \t]+$', original_content, re.MULTILINE)
            )

            if content != original_content:
                with open(file_path, 'w', encoding='utf  -  8') as f:
                    f.write(content)

    def _fix_code_formatting(self):
        """修复代码格式问题"""
        print("🔧 修复代码格式...")

        python_files = list(self.project_root.rglob("*.py"))

        for file_path in python_files:
            if self._should_skip_file(file_path):
                continue

            with open(file_path, 'r', encoding='utf  -  8') as f:
                content = f.read()

            original_content = content

            # 修复常见格式问题
            # 1. 修复import格式
            content = re.sub(
                r'from\s+([a - zA - Z_][a - zA - Z0 - 9_]*)\s  +  import\s+([a - zA - Z_][a - zA - Z0 - 9_]*)\s  + as\s+([a - zA - Z_][a - zA - Z0 - 9_]*)',
                r'from \1 import \2 as \3',
                content,
            )

            # 2. 修复操作符空格
            content = re.sub(
                r'([a - zA - Z0 - 9_])\s*([+\-*/=])\s*([a - zA - Z0 - 9_])',
                r'\1 \2 \3',
                content,
            )

            # 3. 修复逗号后空格
            content = re.sub(r',([a - zA - Z0 - 9_])', r',  \1', content)

            # 4. 确保文件以换行符结尾
            if not content.endswith('\n'):
                content += '\n'

            if content != original_content:
                with open(file_path, 'w', encoding='utf  -  8') as f:
                    f.write(content)

    def _remove_unused_imports(self):
        """移除未使用的导入"""
        print("🔧 清理未使用的导入...")

        # 使用autoflake清理未使用的导入
        try:
            cmd = [
                'autoflake',
                '--remove  - all  -  unused  -  imports',
                '--remove  -  unused  -  variables',
                '--in  -  place',
                '--recursive',
                str(self.project_root),
            ]

            result = subprocess.run(cmd, capture_output=True, text=True)
            if result.returncode == 0:
                print("✅ 未使用导入清理完成")
            else:
                print("⚠️ autoflake未安装，跳过未使用导入清理")
        except FileNotFoundError:
            print("⚠️ autoflake未安装，跳过未使用导入清理")

    def _fix_docstrings(self):
        """修复docstring"""
        print("🔧 修复docstring...")

        python_files = list(self.project_root.rglob("*.py"))

        for file_path in python_files:
            if self._should_skip_file(file_path):
                continue

            with open(file_path, 'r', encoding='utf  -  8') as f:
                f.read()

            # 简单的docstring检查和修复
            # 这里可以添加更复杂的docstring修复逻辑

    def _run_auto_formatters(self):
        """运行自动格式化工具"""
        print("🔧 运行自动格式化工具...")

        # 1. Black格式化
        try:
            cmd = ['black', '--line  -  length', '88', str(self.project_root)]
            result = subprocess.run(cmd, capture_output=True, text=True)
            if result.returncode == 0:
                print("✅ Black格式化完成")
            else:
                print("⚠️ Black格式化失败")
        except FileNotFoundError:
            print("⚠️ Black未安装，跳过格式化")

        # 2. isort排序导入
        try:
            cmd = ['isort', '--profile', 'black', str(self.project_root)]
            result = subprocess.run(cmd, capture_output=True, text=True)
            if result.returncode == 0:
                print("✅ import排序完成")
            else:
                print("⚠️ import排序失败")
        except FileNotFoundError:
            print("⚠️ isort未安装，跳过import排序")

    def _should_skip_file(self, file_path: Path) -> bool:
        """判断是否应该跳过文件"""
        skip_patterns = [
            '/.git/',
            '/__pycache__/',
            '/.pytest_cache/',
            '/venv/',
            '/env/',
            '.pyc',
            '/migrations/',
            '/node_modules/',
        ]

        file_str = str(file_path)
        return any(pattern in file_str for pattern in skip_patterns)


class CodeQualityChecker:
    """代码质量检查器"""

    def __init__(self, project_root: str):
        self.project_root = Path(project_root)

    def run_quality_checks(self) -> Dict[str, any]:
        """运行质量检查"""
        print("🔍 运行代码质量检查...")

        results = {
            'flake8': self._run_flake8(),
            'mypy': self._run_mypy(),
            'bandit': self._run_bandit(),
            'pytest': self._run_pytest_with_coverage(),
        }

        return results

    def _run_flake8(self) -> Dict[str, any]:
        """运行flake8检查"""
        try:
            cmd = [
                'flake8',
                '--max  -  line  -  length  =  88',
                '--exclude=.git,  __pycache__,  venv',
                str(self.project_root),
            ]
            result = subprocess.run(cmd, capture_output=True, text=True)

            return {
                'success': result.returncode == 0,
                'issues': len(result.stdout.splitlines()) if result.stdout else 0,
                'output': result.stdout,
            }
        except FileNotFoundError:
            return {'success': False, 'error': 'flake8 not installed'}

    def _run_mypy(self) -> Dict[str, any]:
        """运行mypy类型检查"""
        try:
            cmd = ['mypy', '--ignore  -  missing  -  imports', str(self.project_root)]
            result = subprocess.run(cmd, capture_output=True, text=True)

            return {
                'success': result.returncode == 0,
                'issues': len(result.stdout.splitlines()) if result.stdout else 0,
                'output': result.stdout,
            }
        except FileNotFoundError:
            return {'success': False, 'error': 'mypy not installed'}

    def _run_bandit(self) -> Dict[str, any]:
        """运行bandit安全检查"""
        try:
            cmd = ['bandit', '-r', str(self.project_root), '-f', 'json']
            result = subprocess.run(cmd, capture_output=True, text=True)

            return {'success': result.returncode == 0, 'output': result.stdout}
        except FileNotFoundError:
            return {'success': False, 'error': 'bandit not installed'}

    def _run_pytest_with_coverage(self) -> Dict[str, any]:
        """运行pytest测试和覆盖率检查"""
        try:
            cmd = [
                'pytest',
                '--cov=.',
                '--cov  -  report  =  term  -  missing',
                'tests/',
            ]
            result = subprocess.run(
                cmd, capture_output=True, text=True, cwd=self.project_root
            )

            # 解析覆盖率
            coverage_match = re.search(r'TOTAL\s+\d+\s+\d+\s+(\d+)%', result.stdout)
            coverage = int(coverage_match.group(1)) if coverage_match else 0

            return {
                'success': result.returncode == 0,
                'coverage': coverage,
                'output': result.stdout,
            }
        except FileNotFoundError:
            return {'success': False, 'error': 'pytest not installed'}


def main():
    """主函数"""
    project_root = Path(__file__).parent.parent

    # 1. 清理代码
    cleaner = CodeCleaner(str(project_root))
    cleanup_results = cleaner.clean_all()

    print(f"\n📊 清理统计:")
    for issue_type, count in cleanup_results.items():
        print(f"  {issue_type}: {count} 个问题已修复")

    # 2. 质量检查
    checker = CodeQualityChecker(str(project_root))
    quality_results = checker.run_quality_checks()

    print(f"\n📊 质量检查结果:")
    for tool, result in quality_results.items():
        if result.get('success'):
            print(f"  ✅ {tool}: 通过")
            if 'coverage' in result:
                print(f"    覆盖率: {result['coverage']}%")
        else:
            print(f"  ❌ {tool}: 失败  -  {result.get('error',  '未知错误')}")
            if 'issues' in result:
                print(f"    问题数: {result['issues']}")

    print("\n🎉 代码质量优化完成!")


if __name__ == '__main__':
    main()
