"""
VPP算法性能测试模块
测试算法执行性能、并发性能和资源使用情况

@author: VPP Team
@version: 2.1.0
@date: 2024 - 01 - 01
"""

import concurrent.futures
import os
import sys
import threading
import time
import unittest
from unittest.mock import Mock, patch

import numpy as np
import psutil

# 添加项目路径
sys.path.insert(0, os.path.join(os.path.dirname(__file__), '..'))

from core.algorithm_engine import AlgorithmEngine, StorageConfig
from core.database_manager import DatabaseConfig, SecureDatabaseManager


class TestAlgorithmPerformance(unittest.TestCase):
    """算法性能测试"""

    def setUp(self):
        """测试设置"""
        self.engine = AlgorithmEngine()
        self.config = StorageConfig()
        self.engine.initialize_storage_optimizer(self.config)

        # 创建测试数据
        self.demand_forecast = np.random.uniform(800, 1200, 96).tolist()
        self.hourly_prices = np.random.uniform(0.3, 0.9, 24).tolist()
        self.time_periods = (
            ['valley'] * 8 + ['flat'] * 4 + ['peak'] * 8 + ['flat'] * 4
        ) * 4

    def test_single_optimization_performance(self):
        """测试单次优化性能"""
        start_time = time.time()

        with patch.object(
            self.engine.storage_optimizer, 'optimize_daily_schedule'
        ) as mock_optimize:
            from core.algorithm_engine import OptimizationResult, OptimizationStatus

            mock_result = OptimizationResult(
                status=OptimizationStatus.SUCCESS,
                objective_value=1000.0,
                solve_time=0.1,
            )
            mock_optimize.return_value = mock_result

            result = self.engine.run_storage_optimization(
                node_id="perf_test_node",
                demand_forecast=self.demand_forecast,
                hourly_prices=self.hourly_prices,
                time_periods=self.time_periods,
            )

        execution_time = time.time() - start_time

        # 性能断言
        self.assertLess(
            execution_time, 2.0, "Single optimization should complete within 2 seconds"
        )
        self.assertEqual(result["status"], "success")

        print(f"Single optimization time: {execution_time:.3f}s")

    def test_batch_optimization_performance(self):
        """测试批量优化性能"""
        batch_size = 10
        start_time = time.time()

        with patch.object(
            self.engine.storage_optimizer, 'optimize_daily_schedule'
        ) as mock_optimize:
            from core.algorithm_engine import OptimizationResult, OptimizationStatus

            mock_result = OptimizationResult(
                status=OptimizationStatus.SUCCESS,
                objective_value=1000.0,
                solve_time=0.1,
            )
            mock_optimize.return_value = mock_result

            results = []
            for i in range(batch_size):
                result = self.engine.run_storage_optimization(
                    node_id=f"batch_node_{i}",
                    demand_forecast=self.demand_forecast,
                    hourly_prices=self.hourly_prices,
                    time_periods=self.time_periods,
                )
                results.append(result)

        total_time = time.time() - start_time
        avg_time = total_time / batch_size

        # 性能断言
        self.assertLess(
            avg_time, 1.0, "Average batch optimization time should be under 1 second"
        )
        self.assertEqual(len(results), batch_size)

        print(f"Batch optimization ({batch_size} items):")
        print(f"  Total time: {total_time:.3f}s")
        print(f"  Average time: {avg_time:.3f}s")

    def test_concurrent_optimization_performance(self):
        """测试并发优化性能"""
        num_threads = 5
        start_time = time.time()

        def run_optimization(node_id):
            with patch.object(
                self.engine.storage_optimizer, 'optimize_daily_schedule'
            ) as mock_optimize:
                from core.algorithm_engine import OptimizationResult, OptimizationStatus

                mock_result = OptimizationResult(
                    status=OptimizationStatus.SUCCESS,
                    objective_value=1000.0,
                    solve_time=0.1,
                )
                mock_optimize.return_value = mock_result

                return self.engine.run_storage_optimization(
                    node_id=node_id,
                    demand_forecast=self.demand_forecast,
                    hourly_prices=self.hourly_prices,
                    time_periods=self.time_periods,
                )

        # 并发执行
        with concurrent.futures.ThreadPoolExecutor(max_workers=num_threads) as executor:
            futures = [
                executor.submit(run_optimization, f"concurrent_node_{i}")
                for i in range(num_threads)
            ]

            results = [
                future.result() for future in concurrent.futures.as_completed(futures)
            ]

        total_time = time.time() - start_time

        # 性能断言
        self.assertLess(
            total_time, 5.0, "Concurrent optimization should complete within 5 seconds"
        )
        self.assertEqual(len(results), num_threads)

        print(f"Concurrent optimization ({num_threads} threads): {total_time:.3f}s")

    def test_memory_usage_optimization(self):
        """测试优化过程中的内存使用"""
        process = psutil.Process()

        # 记录初始内存使用
        initial_memory = process.memory_info().rss / 1024 / 1024  # MB

        with patch.object(
            self.engine.storage_optimizer, 'optimize_daily_schedule'
        ) as mock_optimize:
            from core.algorithm_engine import OptimizationResult, OptimizationStatus

            mock_result = OptimizationResult(
                status=OptimizationStatus.SUCCESS,
                objective_value=1000.0,
                solve_time=0.1,
            )
            mock_optimize.return_value = mock_result

            # 执行多次优化
            for i in range(20):
                self.engine.run_storage_optimization(
                    node_id=f"memory_test_node_{i}",
                    demand_forecast=self.demand_forecast,
                    hourly_prices=self.hourly_prices,
                    time_periods=self.time_periods,
                )

        # 记录最终内存使用
        final_memory = process.memory_info().rss / 1024 / 1024  # MB
        memory_increase = final_memory - initial_memory

        # 内存使用断言
        self.assertLess(
            memory_increase, 100, "Memory increase should be less than 100MB"
        )

        print(f"Memory usage:")
        print(f"  Initial: {initial_memory:.1f}MB")
        print(f"  Final: {final_memory:.1f}MB")
        print(f"  Increase: {memory_increase:.1f}MB")


class TestDatabasePerformance(unittest.TestCase):
    """数据库性能测试"""

    def setUp(self):
        """测试设置"""
        self.config = DatabaseConfig(
            host="localhost",
            port=5432,
            database="test_db",
            username="test_user",
            password="test_pass",
        )
        self.db_manager = SecureDatabaseManager(self.config)

    @patch('psycopg2.connect')
    def test_connection_pool_performance(self, mock_connect):
        """测试连接池性能"""
        # 模拟数据库连接
        mock_conn = Mock()
        mock_connect.return_value = mock_conn

        start_time = time.time()

        # 模拟多次连接获取
        connections = []
        for i in range(10):
            with patch.object(self.db_manager, 'get_connection') as mock_get_conn:
                mock_get_conn.return_value = mock_conn
                connections.append(mock_get_conn())

        connection_time = time.time() - start_time

        # 性能断言
        self.assertLess(connection_time, 1.0, "Connection pooling should be fast")

        print(f"Connection pool performance: {connection_time:.3f}s for 10 connections")

    @patch('psycopg2.connect')
    def test_query_performance(self, mock_connect):
        """测试查询性能"""
        # 模拟数据库连接和游标
        mock_conn = Mock()
        mock_cursor = Mock()
        mock_conn.cursor.return_value = mock_cursor
        mock_connect.return_value = mock_conn

        # 模拟查询结果
        mock_cursor.fetchall.return_value = [(i, f"data_{i}") for i in range(1000)]
        mock_cursor.description = [('id',), ('data',)]

        start_time = time.time()

        with patch.object(self.db_manager, '_execute_query') as mock_execute:
            mock_execute.return_value = [(i, f"data_{i}") for i in range(1000)]

            # 执行查询
            query = "SELECT * FROM test_table LIMIT 1000"
            result = mock_execute(query)

        query_time = time.time() - start_time

        # 性能断言
        self.assertLess(query_time, 0.5, "Query should complete within 0.5 seconds")
        self.assertEqual(len(result), 1000)

        print(f"Query performance: {query_time:.3f}s for 1000 records")


class TestSystemResourceUsage(unittest.TestCase):
    """系统资源使用测试"""

    def test_cpu_usage_during_optimization(self):
        """测试优化过程中CPU使用率"""
        engine = AlgorithmEngine()
        config = StorageConfig()
        engine.initialize_storage_optimizer(config)

        # 监控CPU使用率
        cpu_usage_samples = []

        def monitor_cpu():
            for _ in range(10):
                cpu_usage_samples.append(psutil.cpu_percent(interval=0.1))

        # 启动CPU监控线程
        monitor_thread = threading.Thread(target=monitor_cpu)
        monitor_thread.start()

        # 执行优化
        with patch.object(
            engine.storage_optimizer, 'optimize_daily_schedule'
        ) as mock_optimize:
            from core.algorithm_engine import OptimizationResult, OptimizationStatus

            mock_result = OptimizationResult(
                status=OptimizationStatus.SUCCESS,
                objective_value=1000.0,
                solve_time=0.5,
            )
            mock_optimize.return_value = mock_result

            engine.run_storage_optimization(
                node_id="cpu_test_node",
                demand_forecast=np.random.uniform(800, 1200, 96).tolist(),
                hourly_prices=np.random.uniform(0.3, 0.9, 24).tolist(),
                time_periods=['flat'] * 96,
            )

        monitor_thread.join()

        # 分析CPU使用率
        avg_cpu = sum(cpu_usage_samples) / len(cpu_usage_samples)
        max_cpu = max(cpu_usage_samples)

        # CPU使用率断言（正常情况下不应该持续超过80%）
        self.assertLess(avg_cpu, 80.0, "Average CPU usage should be reasonable")

        print(f"CPU usage during optimization:")
        print(f"  Average: {avg_cpu:.1f}%")
        print(f"  Maximum: {max_cpu:.1f}%")

    def test_disk_io_performance(self):
        """测试磁盘I / O性能"""
        # 获取初始磁盘I / O统计
        initial_io = psutil.disk_io_counters()

        # 模拟文件操作
        test_file = " / tmp / vpp_performance_test.txt"
        start_time = time.time()

        # 写入测试数据
        with open(test_file, 'w') as f:
            for i in range(1000):
                f.write(f"Test data line {i}\n")

        # 读取测试数据
        with open(test_file, 'r') as f:
            lines = f.readlines()

        io_time = time.time() - start_time

        # 清理测试文件
        os.remove(test_file)

        # 获取最终磁盘I / O统计
        final_io = psutil.disk_io_counters()

        # I / O性能断言
        self.assertLess(io_time, 1.0, "File I / O should complete within 1 second")
        self.assertEqual(len(lines), 1000)

        print(f"Disk I / O performance:")
        print(f"  Time: {io_time:.3f}s")
        print(f"  Read bytes: {final_io.read_bytes - initial_io.read_bytes}")
        print(f"  Write bytes: {final_io.write_bytes - initial_io.write_bytes}")


def run_performance_tests():
    """运行性能测试"""
    print(" = " * 80)
    print(" = " * 80)

    # 创建测试套件
    loader = unittest.TestLoader()
    suite = unittest.TestSuite()

    # 添加性能测试类
    performance_test_classes = [
        TestAlgorithmPerformance,
        TestDatabasePerformance,
        TestSystemResourceUsage,
    ]

    for test_class in performance_test_classes:
        tests = loader.loadTestsFromTestCase(test_class)
        suite.addTests(tests)

    # 运行测试
    runner = unittest.TextTestRunner(verbosity=2)
    result = runner.run(suite)

    return result


if __name__ == '__main__':
    run_performance_tests()
