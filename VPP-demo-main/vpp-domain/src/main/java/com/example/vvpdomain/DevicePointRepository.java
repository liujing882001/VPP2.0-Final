package com.example.vvpdomain;

import com.example.vvpdomain.entity.DevicePoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

/**
 * @author zph
 * @description 设备点位信息表
 * @date 2022-07-01
 */
@Repository
public interface DevicePointRepository extends JpaRepository<DevicePoint, String>, JpaSpecificationExecutor<DevicePoint> {


    DevicePoint findByPointSn(String pointSn);

    DevicePoint findDevicePointByDevice_DeviceSnAndAndPointSn(String deviceSn, String pointSn);


    @Modifying
    @Query(value = "delete from device_point where device_id= :deviceId ", nativeQuery = true)
    void deleteAllByDevice_DeviceId(@Param("deviceId") String deviceId);

    @Query(value = "SELECT * from device_point where device_id in (:deviceIds)", nativeQuery = true)
    List<DevicePoint> findAllByDevice_DeviceIds(@Param("deviceIds") List<String> deviceIds);
    List<DevicePoint> findAllByDevice_DeviceId(String deviceId);

    List<DevicePoint> findAllByDevice_Node_NodeId(String nodeId);

    List<DevicePoint> findAllByDeviceSnIn(List<String> deviceSns);
    @Query(value = "SELECT * from device_point where device_sn in (:deviceSns) and point_key = :pointKey", nativeQuery = true)
    List<DevicePoint> findAllByPointKey(@Param("deviceSns") List<String> deviceSns, @Param("pointKey") String pointKey);
    List<DevicePoint> findAllByDeviceSnInAndPointDesc(List<String> deviceSns, String pointDesc);

    List<DevicePoint> findAllByDeviceSnInAndDeviceConfigKeyAndPointDesc(Collection<String> deviceSnItems, String deviceCfgKey, String pointDesc);

    DevicePoint findByDeviceSnAndPointDesc(String deviceSn, String pointDesc);

    DevicePoint findByDeviceSnAndPointKey(String deviceSn, String pointKey);

    List<DevicePoint> findAllByPointSnIn(Collection<String> pointSnItems);

//    List<DevicePoint> findAllByNodeIdAndPointName(String nodeId, String pointName);
}