#!/usr/bin/env python3
"""
VPP项目代码质量检查脚本 v2.0
============================

全面的代码质量检查工具，包括：
- 代码风格检查
- 安全漏洞扫描
- 性能分析
- 依赖安全检查
- 测试覆盖率分析
- 文档完整性检查

使用方法:
    python scripts/quality_check.py --all
    python scripts/quality_check.py --security
    python scripts/quality_check.py --performance

作者: VPP Development Team
版本: 2.0.0
更新: 2024-12-29
"""

import argparse
import json
import logging
import os
import subprocess
import sys
import time
from pathlib import Path
from typing import Dict, List, Any, Optional, Tuple
import warnings

# 配置日志
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.StreamHandler(),
        logging.FileHandler('quality_check.log')
    ]
)
logger = logging.getLogger(__name__)

class QualityChecker:
    """代码质量检查器"""
    
    def __init__(self, project_root: str = "."):
        self.project_root = Path(project_root)
        self.results = {
            "timestamp": time.strftime("%Y-%m-%d %H:%M:%S"),
            "checks": {},
            "summary": {
                "total_issues": 0,
                "critical_issues": 0,
                "warnings": 0,
                "score": 0
            }
        }
        
    def run_command(self, command: List[str], cwd: Optional[Path] = None) -> Tuple[int, str, str]:
        """运行命令并返回结果"""
        try:
            result = subprocess.run(
                command,
                cwd=cwd or self.project_root,
                capture_output=True,
                text=True,
                timeout=300  # 5分钟超时
            )
            return result.returncode, result.stdout, result.stderr
        except subprocess.TimeoutExpired:
            return -1, "", "Command timed out"
        except Exception as e:
            return -1, "", str(e)
    
    def check_python_style(self) -> Dict[str, Any]:
        """检查Python代码风格"""
        logger.info("🔍 检查Python代码风格...")
        
        results = {
            "black": {"status": "unknown", "issues": []},
            "isort": {"status": "unknown", "issues": []},
            "flake8": {"status": "unknown", "issues": []},
            "mypy": {"status": "unknown", "issues": []}
        }
        
        # Black格式检查
        returncode, stdout, stderr = self.run_command([
            "black", "--check", "--diff", "."
        ])
        if returncode == 0:
            results["black"]["status"] = "passed"
        else:
            results["black"]["status"] = "failed"
            results["black"]["issues"] = stdout.split('\n') if stdout else []
        
        # isort检查
        returncode, stdout, stderr = self.run_command([
            "isort", "--check-only", "--diff", "."
        ])
        if returncode == 0:
            results["isort"]["status"] = "passed"
        else:
            results["isort"]["status"] = "failed"
            results["isort"]["issues"] = stdout.split('\n') if stdout else []
        
        # Flake8检查
        returncode, stdout, stderr = self.run_command([
            "flake8", ".", "--count", "--statistics", "--format=json"
        ])
        if returncode == 0:
            results["flake8"]["status"] = "passed"
        else:
            results["flake8"]["status"] = "failed"
            try:
                flake8_data = json.loads(stdout) if stdout else []
                results["flake8"]["issues"] = flake8_data
            except json.JSONDecodeError:
                results["flake8"]["issues"] = stdout.split('\n') if stdout else []
        
        # MyPy类型检查
        returncode, stdout, stderr = self.run_command([
            "mypy", ".", "--ignore-missing-imports", "--json-report", "/tmp/mypy_report"
        ])
        if returncode == 0:
            results["mypy"]["status"] = "passed"
        else:
            results["mypy"]["status"] = "failed"
            results["mypy"]["issues"] = stdout.split('\n') if stdout else []
        
        return results
    
    def check_security(self) -> Dict[str, Any]:
        """安全漏洞检查"""
        logger.info("🛡️ 运行安全检查...")
        
        results = {
            "bandit": {"status": "unknown", "issues": []},
            "safety": {"status": "unknown", "issues": []},
            "semgrep": {"status": "unknown", "issues": []}
        }
        
        # Bandit安全检查
        returncode, stdout, stderr = self.run_command([
            "bandit", "-r", ".", "-x", "tests/", "-f", "json"
        ])
        
        if returncode == 0:
            results["bandit"]["status"] = "passed"
        else:
            results["bandit"]["status"] = "failed"
            try:
                bandit_data = json.loads(stdout) if stdout else {"results": []}
                results["bandit"]["issues"] = bandit_data.get("results", [])
            except json.JSONDecodeError:
                results["bandit"]["issues"] = [{"error": "Failed to parse bandit output"}]
        
        # Safety依赖检查
        if (self.project_root / "requirements-lock.txt").exists():
            returncode, stdout, stderr = self.run_command([
                "safety", "check", "-r", "requirements-lock.txt", "--json"
            ])
            
            if returncode == 0:
                results["safety"]["status"] = "passed"
            else:
                results["safety"]["status"] = "failed"
                try:
                    safety_data = json.loads(stdout) if stdout else []
                    results["safety"]["issues"] = safety_data
                except json.JSONDecodeError:
                    results["safety"]["issues"] = [{"error": "Failed to parse safety output"}]
        
        return results
    
    def check_test_coverage(self) -> Dict[str, Any]:
        """测试覆盖率检查"""
        logger.info("🧪 检查测试覆盖率...")
        
        results = {
            "coverage": {"status": "unknown", "percentage": 0, "details": {}},
            "test_results": {"status": "unknown", "passed": 0, "failed": 0, "total": 0}
        }
        
        # 运行测试并生成覆盖率报告
        returncode, stdout, stderr = self.run_command([
            "pytest", "tests/", "--cov=.", "--cov-report=json", "--cov-report=term", "-v"
        ])
        
        if returncode == 0:
            results["test_results"]["status"] = "passed"
        else:
            results["test_results"]["status"] = "failed"
        
        # 解析测试结果
        test_lines = stdout.split('\n') if stdout else []
        for line in test_lines:
            if "passed" in line and "failed" in line:
                # 解析测试统计信息
                pass
        
        # 读取覆盖率报告
        coverage_file = self.project_root / "coverage.json"
        if coverage_file.exists():
            try:
                with open(coverage_file, 'r') as f:
                    coverage_data = json.load(f)
                    results["coverage"]["percentage"] = coverage_data.get("totals", {}).get("percent_covered", 0)
                    results["coverage"]["details"] = coverage_data.get("files", {})
                    results["coverage"]["status"] = "passed" if results["coverage"]["percentage"] >= 80 else "warning"
            except Exception as e:
                results["coverage"]["status"] = "error"
                results["coverage"]["error"] = str(e)
        
        return results
    
    def check_performance(self) -> Dict[str, Any]:
        """性能检查"""
        logger.info("⚡ 运行性能检查...")
        
        results = {
            "load_time": {"status": "unknown", "time_ms": 0},
            "memory_usage": {"status": "unknown", "peak_mb": 0},
            "benchmark": {"status": "unknown", "results": []}
        }
        
        # 运行性能基准测试
        if (self.project_root / "tests" / "test_performance.py").exists():
            returncode, stdout, stderr = self.run_command([
                "pytest", "tests/test_performance.py", "-v", "--benchmark-only"
            ])
            
            if returncode == 0:
                results["benchmark"]["status"] = "passed"
                results["benchmark"]["results"] = stdout.split('\n')
            else:
                results["benchmark"]["status"] = "failed"
                results["benchmark"]["error"] = stderr
        
        return results
    
    def check_documentation(self) -> Dict[str, Any]:
        """文档完整性检查"""
        logger.info("📚 检查文档完整性...")
        
        results = {
            "readme": {"exists": False, "quality": "unknown"},
            "api_docs": {"exists": False, "quality": "unknown"},
            "deployment_guide": {"exists": False, "quality": "unknown"},
            "docstrings": {"coverage": 0, "quality": "unknown"}
        }
        
        # 检查README文件
        readme_files = ["README.md", "README.rst", "README.txt"]
        for readme in readme_files:
            if (self.project_root / readme).exists():
                results["readme"]["exists"] = True
                # 简单的质量检查
                with open(self.project_root / readme, 'r', encoding='utf-8') as f:
                    content = f.read()
                    if len(content) > 500:
                        results["readme"]["quality"] = "good"
                    else:
                        results["readme"]["quality"] = "basic"
                break
        
        # 检查部署指南
        if (self.project_root / "DEPLOYMENT_GUIDE.md").exists():
            results["deployment_guide"]["exists"] = True
            results["deployment_guide"]["quality"] = "good"
        
        # 检查API文档
        api_doc_files = ["API.md", "api.md", "docs/API.md", "docs/api.md"]
        for api_doc in api_doc_files:
            if (self.project_root / api_doc).exists():
                results["api_docs"]["exists"] = True
                results["api_docs"]["quality"] = "good"
                break
        
        return results
    
    def check_dependencies(self) -> Dict[str, Any]:
        """依赖检查"""
        logger.info("📦 检查项目依赖...")
        
        results = {
            "python_deps": {"status": "unknown", "locked": False, "outdated": []},
            "security_vulns": {"status": "unknown", "count": 0, "details": []}
        }
        
        # 检查是否有锁定的依赖文件
        if (self.project_root / "requirements-lock.txt").exists():
            results["python_deps"]["locked"] = True
            results["python_deps"]["status"] = "good"
        elif (self.project_root / "requirements.txt").exists():
            results["python_deps"]["status"] = "warning"
            results["python_deps"]["message"] = "建议使用锁定版本的依赖文件"
        
        return results
    
    def calculate_score(self) -> int:
        """计算代码质量分数"""
        score = 100
        
        # 代码风格扣分
        style_checks = self.results["checks"].get("style", {})
        for tool, result in style_checks.items():
            if result["status"] == "failed":
                score -= 5
        
        # 安全问题扣分
        security_checks = self.results["checks"].get("security", {})
        for tool, result in security_checks.items():
            if result["status"] == "failed":
                issue_count = len(result.get("issues", []))
                score -= min(issue_count * 2, 20)  # 最多扣20分
        
        # 测试覆盖率扣分
        coverage = self.results["checks"].get("coverage", {}).get("coverage", {})
        if coverage.get("status") == "passed":
            coverage_pct = coverage.get("percentage", 0)
            if coverage_pct < 80:
                score -= (80 - coverage_pct) * 0.5
        
        # 文档完整性扣分
        docs = self.results["checks"].get("documentation", {})
        if not docs.get("readme", {}).get("exists", False):
            score -= 5
        if not docs.get("deployment_guide", {}).get("exists", False):
            score -= 3
        
        return max(0, min(100, score))
    
    def generate_report(self) -> str:
        """生成质量报告"""
        score = self.calculate_score()
        self.results["summary"]["score"] = score
        
        report = []
        report.append("=" * 60)
        report.append("🎯 VPP项目代码质量报告")
        report.append("=" * 60)
        report.append(f"📊 综合得分: {score}/100")
        report.append(f"🕐 检查时间: {self.results['timestamp']}")
        report.append("")
        
        # 详细结果
        if "style" in self.results["checks"]:
            report.append("📝 代码风格检查:")
            for tool, result in self.results["checks"]["style"].items():
                status_emoji = "✅" if result["status"] == "passed" else "❌"
                report.append(f"  {status_emoji} {tool}: {result['status']}")
            report.append("")
        
        if "security" in self.results["checks"]:
            report.append("🛡️ 安全检查:")
            for tool, result in self.results["checks"]["security"].items():
                status_emoji = "✅" if result["status"] == "passed" else "❌"
                issue_count = len(result.get("issues", []))
                report.append(f"  {status_emoji} {tool}: {result['status']} ({issue_count} issues)")
            report.append("")
        
        if "coverage" in self.results["checks"]:
            coverage = self.results["checks"]["coverage"].get("coverage", {})
            coverage_pct = coverage.get("percentage", 0)
            status_emoji = "✅" if coverage_pct >= 80 else "⚠️"
            report.append(f"🧪 测试覆盖率: {status_emoji} {coverage_pct:.1f}%")
            report.append("")
        
        # 改进建议
        report.append("💡 改进建议:")
        if score < 90:
            report.append("  • 修复代码风格问题")
        if score < 85:
            report.append("  • 解决安全漏洞")
        if score < 80:
            report.append("  • 提高测试覆盖率")
        if score < 75:
            report.append("  • 完善项目文档")
        
        return "\n".join(report)
    
    def run_all_checks(self):
        """运行所有检查"""
        logger.info("🚀 开始全面质量检查...")
        
        self.results["checks"]["style"] = self.check_python_style()
        self.results["checks"]["security"] = self.check_security()
        self.results["checks"]["coverage"] = self.check_test_coverage()
        self.results["checks"]["performance"] = self.check_performance()
        self.results["checks"]["documentation"] = self.check_documentation()
        self.results["checks"]["dependencies"] = self.check_dependencies()
        
        # 保存详细结果
        with open("quality_report.json", "w") as f:
            json.dump(self.results, f, indent=2)
        
        # 生成并显示报告
        report = self.generate_report()
        print(report)
        
        # 保存报告
        with open("quality_report.txt", "w") as f:
            f.write(report)
        
        return self.results["summary"]["score"]


def main():
    """主函数"""
    parser = argparse.ArgumentParser(description="VPP项目代码质量检查工具")
    parser.add_argument("--all", action="store_true", help="运行所有检查")
    parser.add_argument("--style", action="store_true", help="只运行代码风格检查")
    parser.add_argument("--security", action="store_true", help="只运行安全检查")
    parser.add_argument("--coverage", action="store_true", help="只运行测试覆盖率检查")
    parser.add_argument("--performance", action="store_true", help="只运行性能检查")
    parser.add_argument("--docs", action="store_true", help="只运行文档检查")
    parser.add_argument("--min-score", type=int, default=80, help="最低质量分数")
    
    args = parser.parse_args()
    
    checker = QualityChecker()
    
    if args.all or not any([args.style, args.security, args.coverage, args.performance, args.docs]):
        score = checker.run_all_checks()
    else:
        if args.style:
            checker.results["checks"]["style"] = checker.check_python_style()
        if args.security:
            checker.results["checks"]["security"] = checker.check_security()
        if args.coverage:
            checker.results["checks"]["coverage"] = checker.check_test_coverage()
        if args.performance:
            checker.results["checks"]["performance"] = checker.check_performance()
        if args.docs:
            checker.results["checks"]["documentation"] = checker.check_documentation()
        
        score = checker.calculate_score()
        print(checker.generate_report())
    
    # 根据分数决定退出码
    if score < args.min_score:
        logger.error(f"质量分数 {score} 低于最低要求 {args.min_score}")
        sys.exit(1)
    else:
        logger.info(f"质量检查通过，得分: {score}")
        sys.exit(0)


if __name__ == "__main__":
    main() 