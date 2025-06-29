package com.example.vvpservice.usernode.service;


import com.example.vvpdomain.entity.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface IPageableService {

    Page<User> getUserLikeUserName(String userName, int number, int pageSize);

    Page<Node> getNodeLikeNodeName(String userName, int number, int pageSize);

    Page<DevicePoint> getPointByNodeIdOrSystemIdOrDeviceId(String nodeId, String systemId, String deviceId, int number, int pageSize);


    List<Node> getPermissionNodes();


    Node getPermissionNode(String nodeId);

    Page<ScheduleStrategy> getStrategyLikeStrategyName(String strategyName, int number, int pageSize);


    Page<ScheduleStrategyView> getStrategyDeviceByStrategyId(String strategyId, String deviceName, int number, int pageSize);

    Page<ScheduleStrategyView> getStrategyDeviceByStrategyId(String strategyId, String deviceName, int number, int pageSize, boolean on);

    Page<Device> getDeviceByNodeIdAndSystemId(String nodeId, String systemId, int number, int pageSize);

    Page<Device> getDeviceLikeDeviceName(String deviceName, int number, int pageSize);

    Page<Role> getRoleLikeRoleName(String roleName, int number, int pageSize);
}
