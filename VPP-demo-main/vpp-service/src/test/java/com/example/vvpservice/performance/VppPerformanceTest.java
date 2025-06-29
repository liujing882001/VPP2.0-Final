package com.example.vvpservice.performance;
import java.time.LocalDate;

import com.example.vvpdomain.entity.User;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.IotTsKv;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.iotdata.service.IIotDeviceService;
import com.example.vvpcommom.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

package com.example.vvpservice.performance;

import com.example.vvpdomain.entity.User;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.IotTsKv;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.iotdata.service.IIotDeviceService;
import com.example.vvpcommom.RedisUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Date;
import java.time.ZoneId;
import java.util.Calendar;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VPP系统性能测试
 * 测试系统在高并发、大数据量场景下的性能表现
 */
@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:perfdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "logging.level.org.springframework.web=DEBUG"
})
public class VppPerformanceTest {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IIotDeviceService iotDeviceService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private IotTsKvRepository iotTsKvRepository;
    
    @Autowired
    private RedisUtils redisUtils;
    
    private static final int CONCURRENT_USERS = 50;
    private static final int BATCH_SIZE = 1000;
    private static final int PERFORMANCE_THRESHOLD_MS = 5000;

    @BeforeEach
    void setUp() {
        // 清理测试数据
        System.out.println("开始性能测试环境准备...");
    }

    /**
     * 测试1：并发用户访问性能
     */
    @Test
    @DisplayName("并发用户访问性能测试")
    void testConcurrentUserAccess() throws InterruptedException {
        System.out.println("=== 并发用户访问性能测试 ===");
        
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_USERS);
        CountDownLatch latch = new CountDownLatch(CONCURRENT_USERS);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        AtomicLong totalTime = new AtomicLong(0);
        
        long startTime = System.currentTimeMillis();
        
        // 模拟50个并发用户同时访问
        for (int i = 0; i < CONCURRENT_USERS; i++) {
            final int userId = i;
            executor.submit(() -> {
                try {
                    long threadStartTime = System.currentTimeMillis();
                    
                    // 创建用户
                    User user = new User();
                    user.setUserId("concurrent_user_" + userId);
                    user.setUserName("并发测试用户" + userId);
                    user.setConfigType("0");
                    user.setCreatedTime(new Date());
                    
                    User savedUser = userRepository.save(user);
                    
                    // 查询用户
                    userRepository.findById(savedUser.getUserId());
                    
                    // 更新用户
                    savedUser.setUserName("更新后的用户" + userId);
                    userRepository.save(savedUser);
                    
                    long threadEndTime = System.currentTimeMillis();
                    totalTime.addAndGet(threadEndTime - threadStartTime);
                    successCount.incrementAndGet();
                    
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("并发测试异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        boolean completed = latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 性能断言
        assertTrue(completed, "所有并发任务应该在30秒内完成");
        assertTrue(totalDuration < PERFORMANCE_THRESHOLD_MS * 2, "总耗时应该在合理范围内");
        assertEquals(CONCURRENT_USERS, successCount.get(), "所有并发任务都应该成功");
        assertEquals(0, errorCount.get(), "不应该有错误");
        
        double avgResponseTime = totalTime.get() / (double) CONCURRENT_USERS;
        
        System.out.println("并发用户数: " + CONCURRENT_USERS);
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("平均响应时间: " + String.format("%.2f", avgResponseTime) + "ms");
        System.out.println("成功数: " + successCount.get());
        System.out.println("错误数: " + errorCount.get());
        System.out.println("TPS: " + String.format("%.2f", CONCURRENT_USERS * 1000.0 / totalDuration));
        
        // 性能要求：平均响应时间应该小于1000ms
        assertTrue(avgResponseTime < 1000, "平均响应时间应该小于1000ms，当前: " + avgResponseTime + "ms");
    }

    /**
     * 测试2：数据库批量操作性能
     */
    @Test
    @DisplayName("数据库批量操作性能测试")
    void testDatabaseBatchPerformance() {
        System.out.println("=== 数据库批量操作性能测试 ===");
        
        // 测试批量插入
        long startTime = System.currentTimeMillis();
        
        List<User> users = new ArrayList<>();
        for (int i = 0; i < BATCH_SIZE; i++) {
            User user = new User();
            user.setUserId("batch_user_" + i);
            user.setUserName("批量测试用户" + i);
            user.setConfigType("0");
            user.setCreatedTime(new Date());
            users.add(user);
        }
        
        // 批量保存
        List<User> savedUsers = userRepository.saveAll(users);
        
        long insertEndTime = System.currentTimeMillis();
        long insertDuration = insertEndTime - startTime;
        
        // 测试批量查询
        startTime = System.currentTimeMillis();
        List<User> allUsers = userRepository.findAll();
        long queryEndTime = System.currentTimeMillis();
        long queryDuration = queryEndTime - startTime;
        
        // 测试批量更新
        startTime = System.currentTimeMillis();
        savedUsers.forEach(user -> user.setUserName("批量更新用户"));
        userRepository.saveAll(savedUsers);
        long updateEndTime = System.currentTimeMillis();
        long updateDuration = updateEndTime - startTime;
        
        // 性能断言
        assertTrue(insertDuration < PERFORMANCE_THRESHOLD_MS, 
                  "批量插入" + BATCH_SIZE + "条记录应该在" + PERFORMANCE_THRESHOLD_MS + "ms内完成");
        assertTrue(queryDuration < 2000, "批量查询应该在2秒内完成");
        assertTrue(updateDuration < PERFORMANCE_THRESHOLD_MS, 
                  "批量更新应该在" + PERFORMANCE_THRESHOLD_MS + "ms内完成");
        
        assertEquals(BATCH_SIZE, savedUsers.size(), "应该保存" + BATCH_SIZE + "条记录");
        assertTrue(allUsers.size() >= BATCH_SIZE, "应该查询到至少" + BATCH_SIZE + "条记录");
        
        System.out.println("批量插入" + BATCH_SIZE + "条记录耗时: " + insertDuration + "ms");
        System.out.println("批量查询" + allUsers.size() + "条记录耗时: " + queryDuration + "ms");
        System.out.println("批量更新" + BATCH_SIZE + "条记录耗时: " + updateDuration + "ms");
        System.out.println("插入TPS: " + String.format("%.2f", BATCH_SIZE * 1000.0 / insertDuration));
        System.out.println("查询TPS: " + String.format("%.2f", allUsers.size() * 1000.0 / queryDuration));
    }

    /**
     * 测试3：缓存性能测试
     */
    @Test
    @DisplayName("缓存性能测试")
    void testCachePerformance() {
        System.out.println("=== 缓存性能测试 ===");
        
        String cacheKey = "performance_test_key";
        String cacheValue = "performance_test_value";
        int iterations = 1000;
        
        // 测试Redis写入性能
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            redisUtils.add(cacheKey + "_" + i, cacheValue + "_" + i);
        }
        long writeEndTime = System.currentTimeMillis();
        long writeDuration = writeEndTime - startTime;
        
        // 测试Redis读取性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            Object value = redisUtils.get(cacheKey + "_" + i);
            assertNotNull(value, "缓存值不应该为空");
        }
        long readEndTime = System.currentTimeMillis();
        long readDuration = readEndTime - startTime;
        
        // 测试Redis删除性能
        startTime = System.currentTimeMillis();
        for (int i = 0; i < iterations; i++) {
            redisUtils.delete(cacheKey + "_" + i);
        }
        long deleteEndTime = System.currentTimeMillis();
        long deleteDuration = deleteEndTime - startTime;
        
        // 性能断言
        assertTrue(writeDuration < 3000, "缓存写入性能应该在3秒内完成");
        assertTrue(readDuration < 2000, "缓存读取性能应该在2秒内完成");
        assertTrue(deleteDuration < 3000, "缓存删除性能应该在3秒内完成");
        
        System.out.println("缓存写入" + iterations + "次耗时: " + writeDuration + "ms");
        System.out.println("缓存读取" + iterations + "次耗时: " + readDuration + "ms");
        System.out.println("缓存删除" + iterations + "次耗时: " + deleteDuration + "ms");
        System.out.println("写入TPS: " + String.format("%.2f", iterations * 1000.0 / writeDuration));
        System.out.println("读取TPS: " + String.format("%.2f", iterations * 1000.0 / readDuration));
    }

    /**
     * 测试4：IoT数据处理性能
     */
    @Test
    @DisplayName("IoT数据处理性能测试")
    @Transactional
    void testIotDataProcessingPerformance() {
        System.out.println("=== IoT数据处理性能测试 ===");
        
        // 创建测试设备
        List<Device> devices = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Device device = new Device();
            device.setDeviceId("test_device_" + i);
            device.setDeviceName("测试设备" + i);
            device.setDeviceModel("测试型号");
            device.setDeviceRatedPower(100.0);
            device.setOnline(true);
            device.setCreatedTime(new Date());
            devices.add(device);
        }
        
        // 批量保存设备
        List<Device> savedDevices = deviceRepository.saveAll(devices);
        
        // 创建IoT数据
        List<IotTsKv> iotData = new ArrayList<>();
        Date now = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        
        for (Device device : savedDevices) {
            // 为每个设备生成24小时的数据，每小时一个点
            for (int hour = 0; hour < 24; hour++) {
                IotTsKv data = new IotTsKv();
                data.setId("test_data_" + device.getDeviceId() + "_" + hour);
                data.setDeviceSn(device.getDeviceSn());
                data.setDeviceName(device.getDeviceName());
                data.setPointName("power");
                data.setPointValue(String.valueOf(Math.random() * 100));
                
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                data.setTs(calendar.getTime());
                data.setCreatedTime(new Date());
                iotData.add(data);
            }
            calendar.setTime(now); // 重置时间
        }
        
        // 批量保存IoT数据
        long startTime = System.currentTimeMillis();
        iotTsKvRepository.saveAll(iotData);
        long endTime = System.currentTimeMillis();
        
        // 性能断言
        long duration = endTime - startTime;
        assertTrue(duration < PERFORMANCE_THRESHOLD_MS, 
                  "批量保存IoT数据应该在" + PERFORMANCE_THRESHOLD_MS + "ms内完成");
        
        System.out.println("IoT数据处理测试完成");
        System.out.println("设备数量: " + devices.size());
        System.out.println("数据点数量: " + iotData.size());
        System.out.println("处理耗时: " + duration + "ms");
    }

    /**
     * 测试5：内存使用性能
     */
    @Test
    @DisplayName("内存使用性能测试")
    void testMemoryUsagePerformance() {
        System.out.println("=== 内存使用性能测试 ===");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 记录初始内存
        long initialMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("初始内存使用: " + formatBytes(initialMemory));
        
        // 创建大量对象
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            User user = new User();
            user.setUserId("memory_test_user_" + i);
            user.setUserName("内存测试用户" + i);
            user.setConfigType("0");
            user.setCreatedTime(LocalDateTime.now());
            users.add(user);
        }
        
        // 记录峰值内存
        long peakMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("峰值内存使用: " + formatBytes(peakMemory));
        System.out.println("内存增长: " + formatBytes(peakMemory - initialMemory));
        
        // 清理对象
        users.clear();
        System.gc(); // 建议垃圾回收
        
        try {
            Thread.sleep(1000); // 等待GC完成
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // 记录GC后内存
        long afterGcMemory = runtime.totalMemory() - runtime.freeMemory();
        System.out.println("GC后内存使用: " + formatBytes(afterGcMemory));
        
        // 内存使用应该合理
        long memoryIncrease = peakMemory - initialMemory;
        assertTrue(memoryIncrease < 100 * 1024 * 1024, "内存增长应该小于100MB"); // 100MB
        
        long memoryAfterGc = afterGcMemory - initialMemory;
        assertTrue(memoryAfterGc < 50 * 1024 * 1024, "GC后内存增长应该小于50MB"); // 50MB
    }

    /**
     * 格式化字节数
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.2f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.2f MB", bytes / (1024.0 * 1024));
        return String.format("%.2f GB", bytes / (1024.0 * 1024 * 1024));
    }

    /**
     * 测试6：综合性能压力测试
     */
    @Test
    @DisplayName("综合性能压力测试")
    void testComprehensiveStressTest() throws InterruptedException {
        System.out.println("=== 综合性能压力测试 ===");
        
        int threadCount = 10;
        int operationsPerThread = 1000;
        
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        if (j % 4 == 0) {
                            // 用户操作
                            User user = new User();
                            user.setUserId("stress_user_" + threadId + "_" + j);
                            user.setUserName("压力测试用户");
                            user.setConfigType("0");
                            user.setCreatedTime(new Date());
                            userRepository.save(user);
                        } else if (j % 4 == 1) {
                            // 设备操作
                            Device device = new Device();
                            device.setDeviceId("stress_device_" + threadId + "_" + j);
                            device.setDeviceName("压力测试设备");
                            device.setDeviceModel("压力测试型号");
                            device.setOnline(true);
                            device.setCreatedTime(new Date());
                            deviceRepository.save(device);
                        } else if (j % 4 == 2) {
                            // IoT数据操作
                            IotTsKv data = new IotTsKv();
                            data.setId("stress_data_" + threadId + "_" + j);
                            data.setDeviceSn("stress_device_" + threadId + "_" + j);
                            data.setPointName("power");
                            data.setPointValue(String.valueOf(Math.random() * 100));
                            data.setTs(new Date());
                            data.setCreatedTime(new Date());
                            iotTsKvRepository.save(data);
                        } else {
                            // 缓存操作
                            String key = "stress_test_" + threadId + "_" + j;
                            redisUtils.add(key, "test_value_" + System.currentTimeMillis(), 60, TimeUnit.SECONDS);
                        }
                        successCount.incrementAndGet();
                    }
                } catch (Exception e) {
                    errorCount.incrementAndGet();
                    System.err.println("压力测试异常: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }
        
        // 等待所有线程完成
        boolean completed = latch.await(5, TimeUnit.MINUTES);
        executor.shutdown();
        
        long endTime = System.currentTimeMillis();
        long totalDuration = endTime - startTime;
        
        // 性能断言
        assertTrue(completed, "所有压力测试任务应该在5分钟内完成");
        assertEquals(threadCount * operationsPerThread, successCount.get() + errorCount.get(), 
                    "所有操作都应该被执行");
        assertTrue(errorCount.get() < threadCount * operationsPerThread * 0.01, 
                  "错误率应该小于1%");
        
        System.out.println("压力测试完成");
        System.out.println("总操作数: " + (threadCount * operationsPerThread));
        System.out.println("成功数: " + successCount.get());
        System.out.println("错误数: " + errorCount.get());
        System.out.println("总耗时: " + totalDuration + "ms");
        System.out.println("TPS: " + String.format("%.2f", 
            (threadCount * operationsPerThread) * 1000.0 / totalDuration));
    }
} 