package com.example.vvpweb.device;

import com.example.vvpcommom.PageModel;
import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.device.DeviceService;
import com.example.vvpweb.device.model.DevicePageByNLCommand;
import com.example.vvpweb.device.model.DevicePageCommand;
import com.example.vvpweb.device.model.DeviceVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 设备控制器单元测试
 * 
 * @author VPP Team
 * @version 2.0.0
 */
@WebMvcTest(DeviceController.class)
@DisplayName("设备控制器测试")
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceService deviceService;

    @Autowired
    private ObjectMapper objectMapper;

    private Device testDevice;
    private Node testNode;
    private SystemType testSystemType;
    private DevicePoint testDevicePoint;

    @BeforeEach
    void setUp() {
        // 创建测试节点
        testNode = new Node();
        testNode.setNodeId("test-node-id");
        testNode.setNodeName("测试节点");

        // 创建测试系统类型
        testSystemType = new SystemType();
        testSystemType.setSystemId("test-system-id");
        testSystemType.setSystemName("测试系统");

        // 创建测试设备点位
        testDevicePoint = new DevicePoint();
        testDevicePoint.setPointId("test-point-id");
        testDevicePoint.setPointSn("test-point-sn");
        testDevicePoint.setPointDesc("测试点位描述");
        testDevicePoint.setPointName("测试点位");

        // 创建测试设备
        testDevice = new Device();
        testDevice.setDeviceId("test-device-id");
        testDevice.setDeviceSn("test-device-sn");
        testDevice.setDeviceBrand("测试品牌");
        testDevice.setDeviceLabel("测试标签");
        testDevice.setDeviceModel("测试型号");
        testDevice.setDeviceName("测试设备");
        testDevice.setDeviceRatedPower(100.0);
        testDevice.setOnline(true);
        testDevice.setLoadType(1);
        testDevice.setLoadProperties(1);
        testDevice.setMecOnline(true);
        testDevice.setMecName("测试MEC");
        testDevice.setConfigKey("test-config-key");
        testDevice.setNode(testNode);
        testDevice.setSystemType(testSystemType);
        testDevice.setDevicePointList(Arrays.asList(testDevicePoint));
    }

    @Test
    @DisplayName("根据节点ID查询设备分页 - 成功")
    void testDevicePage_Success() throws Exception {
        // Given
        DevicePageCommand command = new DevicePageCommand();
        command.setNodeId("test-node-id");
        command.setPageNum(1);
        command.setPageSize(10);

        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice));
        when(deviceService.devicePageByNodeId(anyString(), anyInt(), anyInt()))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].deviceId").value("test-device-id"))
                .andExpect(jsonPath("$.data.content[0].deviceName").value("测试设备"))
                .andExpect(jsonPath("$.data.content[0].nodeName").value("测试节点"))
                .andExpect(jsonPath("$.data.content[0].systemName").value("测试系统"))
                .andExpect(jsonPath("$.data.content[0].deviceRatedPower").value(100.0))
                .andExpect(jsonPath("$.data.content[0].online").value(true));
    }

    @Test
    @DisplayName("根据节点ID列表查询设备分页 - 成功")
    void testDevicePageByNL_Success() throws Exception {
        // Given
        DevicePageByNLCommand command = new DevicePageByNLCommand();
        command.setNodeIds(Arrays.asList("test-node-id-1", "test-node-id-2"));
        command.setPageNum(1);
        command.setPageSize(10);

        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice));
        when(deviceService.devicePageByNodeIds(any(List.class), anyInt(), anyInt()))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(post("/device/devicePageByNL")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(1)))
                .andExpected(jsonPath("$.data.content[0].deviceId").value("test-device-id"));
    }

    @Test
    @DisplayName("设备分页查询 - 空结果")
    void testDevicePage_EmptyResult() throws Exception {
        // Given
        DevicePageCommand command = new DevicePageCommand();
        command.setNodeId("non-existing-node");
        command.setPageNum(1);
        command.setPageSize(10);

        Page<Device> emptyPage = new PageImpl<>(Arrays.asList());
        when(deviceService.devicePageByNodeId(anyString(), anyInt(), anyInt()))
                .thenReturn(emptyPage);

        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content", hasSize(0)))
                .andExpect(jsonPath("$.data.totalElements").value(0));
    }

    @Test
    @DisplayName("设备分页查询 - 无效输入")
    void testDevicePage_InvalidInput() throws Exception {
        // Given
        DevicePageCommand command = new DevicePageCommand();
        command.setNodeId(""); // 空节点ID
        command.setPageNum(0); // 无效页码
        command.setPageSize(-1); // 无效页大小

        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("设备分页查询 - JSON格式错误")
    void testDevicePage_InvalidJson() throws Exception {
        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("设备分页查询 - 服务异常")
    void testDevicePage_ServiceException() throws Exception {
        // Given
        DevicePageCommand command = new DevicePageCommand();
        command.setNodeId("test-node-id");
        command.setPageNum(1);
        command.setPageSize(10);

        when(deviceService.devicePageByNodeId(anyString(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("设备VO转换测试")
    void testDeviceVOConversion() throws Exception {
        // Given
        DevicePageCommand command = new DevicePageCommand();
        command.setNodeId("test-node-id");
        command.setPageNum(1);
        command.setPageSize(10);

        Page<Device> devicePage = new PageImpl<>(Arrays.asList(testDevice));
        when(deviceService.devicePageByNodeId(anyString(), anyInt(), anyInt()))
                .thenReturn(devicePage);

        // When & Then
        mockMvc.perform(post("/device/devicePage")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].pointViewList").isArray())
                .andExpect(jsonPath("$.data.content[0].pointViewList", hasSize(1)))
                .andExpect(jsonPath("$.data.content[0].pointViewList[0].pointId").value("test-point-id"))
                .andExpect(jsonPath("$.data.content[0].pointViewList[0].pointName").value("测试点位"));
    }
} 