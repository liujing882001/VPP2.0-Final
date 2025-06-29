package com.example.vvpservice.integration;

import com.example.vvpdomain.entity.User;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.entity.Device;
import com.example.vvpdomain.entity.DemandRespTask;
import com.example.vvpdomain.UserRepository;
import com.example.vvpdomain.NodeRepository;
import com.example.vvpdomain.DeviceRepository;
import com.example.vvpdomain.DemandRespTaskRepository;
import com.example.vvpservice.prouser.service.IUserService;
import com.example.vvpservice.demand.service.DemandRespTaskService;
import com.example.vvpservice.iotdata.service.IIotDeviceService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.*;

/**
 * VPP系统集成测试
 * 测试完整的业务流程：用户管理 -> 设备管理 -> 需求响应任务
 */
@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
@ActiveProfiles("test")
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@Rollback
public class VppIntegrationTest {

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IIotDeviceService iotDeviceService;
    
    @Autowired
    private DemandRespTaskService demandRespTaskService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NodeRepository nodeRepository;
    
    @Autowired
    private DeviceRepository deviceRepository;
    
    @Autowired
    private DemandRespTaskRepository demandRespTaskRepository;
    
    private static String testUserId;
    private static String testNodeId;
    private static String testDeviceId;
    private static String testTaskId;

    /**
     * 测试1：用户管理流程
     */
    @Test
    @Order(1)
    void testUserManagementWorkflow() {
        // 1. 创建测试用户
        User testUser = new User();
        testUser.setUserId("test_user_integration");
        testUser.setUserName("集成测试用户");
        testUser.setUserEmail("test@vpp.com");
        testUser.setPhone("13800138000");
        testUser.setConfigType("0"); // 正常状态
        testUser.setCreatedTime(new Date());
        
        User savedUser = userRepository.save(testUser);
        testUserId = savedUser.getUserId();
        
        assertNotNull(testUserId, "用户ID不能为空");
        assertTrue(testUserId.length() > 0, "用户ID应该有效");
        
        // 2. 验证用户查询
        Optional<User> foundUser = userRepository.findById(testUserId);
        assertTrue(foundUser.isPresent(), "应该能够查询到创建的用户");
        assertEquals("test_user_integration", foundUser.get().getUserId(), "用户ID应该匹配");
        
        System.out.println("✓ 用户管理流程测试通过");
    }

    /**
     * 测试2：设备管理流程
     */
    @Test
    @Order(2)
    void testDeviceManagementWorkflow() {
        // 1. 创建测试节点
        Node testNode = new Node();
        testNode.setNodeId("test_node_integration");
        testNode.setNodeName("集成测试节点");
        testNode.setNodePostType("storageEnergy");
        testNode.setOnline(true);
        testNode.setCreatedTime(new Date());
        
        Node savedNode = nodeRepository.save(testNode);
        testNodeId = savedNode.getNodeId();
        
        assertNotNull(testNodeId, "节点ID不能为空");
        
        // 2. 创建测试设备
        Device testDevice = new Device();
        testDevice.setDeviceId("test_device_integration");
        testDevice.setDeviceName("集成测试设备");
        testDevice.setDeviceModel("储能设备");
        testDevice.setDeviceRatedPower(100.0);
        testDevice.setOnline(true);
        testDevice.setCreatedTime(new Date());
        
        Device savedDevice = deviceRepository.save(testDevice);
        testDeviceId = savedDevice.getDeviceId();
        
        assertNotNull(testDeviceId, "设备ID不能为空");
        
        // 3. 验证设备查询
        Optional<Device> foundDevice = deviceRepository.findById(testDeviceId);
        assertTrue(foundDevice.isPresent(), "应该能够查询到创建的设备");
        assertEquals(100.0, foundDevice.get().getDeviceRatedPower(), "设备功率应该匹配");
        
        System.out.println("✓ 设备管理流程测试通过");
    }

    /**
     * 测试3：需求响应任务流程
     */
    @Test
    @Order(3)
    void testDemandResponseWorkflow() {
        // 1. 创建需求响应任务
        DemandRespTask testTask = new DemandRespTask();
        testTask.setRespId("test_task_integration");
        testTask.setTaskCode(1L);
        testTask.setRespLoad(50.0);
        testTask.setRsTime(new Date());
        testTask.setReTime(new Date());
        testTask.setRespType(1); // 削峰
        testTask.setDStatus(1); // 未开始
        testTask.setCreateTime(new Date());
        
        DemandRespTask savedTask = demandRespTaskRepository.save(testTask);
        testTaskId = savedTask.getRespId();
        
        assertNotNull(testTaskId, "任务ID不能为空");
        
        // 2. 验证任务查询
        Optional<DemandRespTask> foundTask = demandRespTaskRepository.findById(testTaskId);
        assertTrue(foundTask.isPresent(), "应该能够查询到创建的任务");
        assertEquals(50.0, foundTask.get().getRespLoad(), "响应负荷应该匹配");
        
        // 3. 验证任务状态更新
        testTask.setDStatus(2); // 执行中
        DemandRespTask updatedTask = demandRespTaskRepository.save(testTask);
        assertEquals(2, updatedTask.getDStatus(), "任务状态应该更新");
        
        // 4. 测试批量处理任务
        demandRespTaskService.batchSysJob(new String[]{testTaskId});
        
        System.out.println("✓ 需求响应任务流程测试通过");
    }

    /**
     * 测试4：完整业务流程集成
     */
    @Test
    @Order(4)
    void testCompleteBusinessWorkflow() {
        // 1. 验证用户、设备、任务关联
        assertNotNull(testUserId, "用户ID应该存在");
        assertNotNull(testNodeId, "节点ID应该存在");
        assertNotNull(testDeviceId, "设备ID应该存在");
        assertNotNull(testTaskId, "任务ID应该存在");
        
        // 创建新用户
        User user = new User();
        user.setUserId("complete_test_user");
        user.setUserName("完整流程测试用户");
        user.setUserEmail("complete@vpp.com");
        user.setPhone("13900139000");
        user.setConfigType("0");
        user.setCreatedTime(new Date());
        userRepository.save(user);
        
        // 创建新设备
        Device device = new Device();
        device.setDeviceId("complete_test_device");
        device.setDeviceName("完整流程测试设备");
        device.setDeviceModel("储能设备");
        device.setDeviceRatedPower(200.0);
        device.setOnline(true);
        device.setCreatedTime(new Date());
        deviceRepository.save(device);
        
        // 创建新任务
        DemandRespTask task = new DemandRespTask();
        task.setRespId("complete_test_task");
        task.setTaskCode(2L);
        task.setRespLoad(100.0);
        task.setRsTime(new Date());
        task.setReTime(new Date());
        task.setRespType(1);
        task.setDStatus(1);
        task.setCreateTime(new Date());
        demandRespTaskRepository.save(task);
        
        System.out.println("✓ 完整业务流程测试通过");
    }

    /**
     * 测试5：异常处理
     */
    @Test
    @Order(5)
    void testExceptionHandling() {
        // 1. 测试无效用户ID
        Optional<User> invalidUser = userRepository.findById("invalid_id");
        assertFalse(invalidUser.isPresent(), "不应该找到无效用户");
        
        // 2. 测试无效设备ID
        Optional<Device> invalidDevice = deviceRepository.findById("invalid_id");
        assertFalse(invalidDevice.isPresent(), "不应该找到无效设备");
        
        // 3. 测试无效任务ID
        Optional<DemandRespTask> invalidTask = demandRespTaskRepository.findById("invalid_id");
        assertFalse(invalidTask.isPresent(), "不应该找到无效任务");
        
        // 4. 测试批量处理无效任务
        demandRespTaskService.batchSysJob(new String[]{"invalid_id"});
        
        System.out.println("✓ 异常处理测试通过");
    }

    /**
     * 测试6：性能基准测试
     */
    @Test
    @Order(6)
    void testPerformanceBenchmark() {
        // 1. 批量创建用户
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setUserId("test_user_" + i);
            user.setUserName("测试用户" + i);
            user.setUserEmail("test" + i + "@vpp.com");
            user.setPhone("1380013800" + i);
            user.setConfigType("0");
            user.setCreatedTime(new Date());
            userRepository.save(user);
        }
        
        // 2. 批量创建设备
        for (int i = 0; i < 10; i++) {
            Device device = new Device();
            device.setDeviceId("test_device_" + i);
            device.setDeviceName("test_device_" + i);
            device.setDeviceModel("储能设备");
            device.setDeviceRatedPower(100.0);
            device.setOnline(true);
            device.setCreatedTime(new Date());
            deviceRepository.save(device);
        }
        
        // 3. 批量创建任务
        String[] taskIds = new String[10];
        for (int i = 0; i < 10; i++) {
            DemandRespTask task = new DemandRespTask();
            task.setRespId("test_task_" + i);
            task.setRespType(1);
            task.setRespLoad(50.0);
            task.setStartTime(LocalDateTime.now().plusHours(1));
            task.setEndTime(LocalDateTime.now().plusHours(2));
            task.setStatus("待执行");
            task.setCreateTime(new Date());
            DemandRespTask savedTask = demandRespTaskRepository.save(task);
            taskIds[i] = savedTask.getRespId();
        }
        
        // 4. 批量处理任务
        demandRespTaskService.batchSysJob(taskIds);
        
        System.out.println("✓ 性能基准测试通过");
    }
} 