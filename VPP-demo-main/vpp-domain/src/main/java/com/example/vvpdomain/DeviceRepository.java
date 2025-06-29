package com.example.vvpdomain;

import com.example.vvpdomain.entity.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

/**
 * @author zph
 * @description 设备表
 * @date 2022-07-01
 */
@Repository
public interface DeviceRepository extends JpaRepository<Device, String>, JpaSpecificationExecutor<Device> {


    @Modifying
    @Query(value = "delete from device where device_id= :deviceId and node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    void deleteByDeviceIdAndNode_NodeIdAndSystemType_SystemId(@Param("deviceId") String device_id,
                                                              @Param("nodeId") String node_id,
                                                              @Param("systemId") String system_id);


    Device findByDeviceSn(String deviceSn);

    Device findByDeviceId(String deviceId);


    List<Device> findAllByNode_NodeIdAndSystemType_SystemId(String nodeId, String systemId);

    List<Device> findAllByNode_NodeIdAndSystemType_SystemIdAndConfigKey(String nodeId, String systemId, String configKey);

    List<Device> findAllByNodeNodeIdAndConfigKey(String nodeId, String configKey);


    List<Device> findAllByNode_NodeId(String nodeId);

    List<Device> findAllByNode_NodeIdIn(List<String> nodeIds);

    List<Device> findAllByMecId(String mecId);


    @Query(value = "select COUNT(*)  from device WHERE  node_id=:nodeId AND online ='t'", nativeQuery = true)
    List<Object[]> findNodeOnlineStatus(@Param("nodeId") String nodeId);

    @Query(value = "SELECT\n" +
            "\tnode_id,\n" +
            "\tCOALESCE ( SUM ( device_rated_power ), 0 ) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id =:nodeId \n" +
            "\tAND system_id <> 'nengyuanzongbiao' \n" +
            "GROUP BY node_id", nativeQuery = true)
    List<Object[]> findAllKWByNodeId(@Param("nodeId") String nodeId);


    @Query(value = "SELECT \n" +
            "   COALESCE(SUM(device_rated_power), 0) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN (:nodeIds) \n" +
            "\tAND system_id <> 'nengyuanzongbiao'", nativeQuery = true)
    List<Object[]> findAllKWByNodeIds(@Param("nodeIds") Collection<String> nodeIds);


    @Query(value = "SELECT \n" +
            "   COALESCE(SUM(device_rated_power), 0) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN (:nodeIds) \n" +
            "\tAND system_id <> 'nengyuanzongbiao'", nativeQuery = true)
    double findAllLoadByNodeIds(@Param("nodeIds") Collection<String> nodeIds);


    @Query(value = "SELECT \n" +
            "   COALESCE(SUM(device_rated_power), 0) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN (:nodeIds) \n" +
            "\tAND system_id <> 'nengyuanzongbiao' and load_properties=:loadProperties", nativeQuery = true)
    double findAllLoadByNodeIdsAndLoadProper(@Param("nodeIds") Collection<String> nodeIds
            ,@Param("loadProperties")String loadProperties);

    @Query(value = "SELECT\n" +
            "\tsystem_id,\n" +
            "\tCOALESCE ( SUM ( device_rated_power ), 0 ) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN (:nodeIds) \n" +
            "\tAND system_id <> 'nengyuanzongbiao' \n" +
            "GROUP BY system_id", nativeQuery = true)
    List<Object[]> findSystemTotalPowerGroupBySystemIdAndNodeItems(@Param("nodeIds") Collection<String> nodeIds);


    @Query(value = "SELECT COALESCE\n" +
            "\t( SUM ( device_rated_power ), 0 ) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'load' ) \n" +
            "\tAND system_id IN ( SELECT system_id FROM sys_dict_type WHERE system_name LIKE :systemName)", nativeQuery = true)
    double findAllKWByContainSystemName(@Param("systemName") String systemName);


    @Query(value = "SELECT COALESCE\n" +
            "\t( SUM ( device_rated_power ), 0 ) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'load' )", nativeQuery = true)
    double findAllLoadKW();

    @Query(value = "SELECT COALESCE\n" +
            "\t( SUM ( device_rated_power ), 0 ) AS device_rated_power \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN (:nodeIds)", nativeQuery = true)
    double findAllLoadKWByNodeIds(@Param("nodeIds") List<String> nodeIds);


    @Query(value = "UPDATE device set online= true WHERE node_id=:nodeId  and device_id=:deviceId ", nativeQuery = true)
    @Transactional
    @Modifying
    void updateDeviceStatus(@Param("nodeId") String nodeId, @Param("deviceId") String deviceId);

    @Query(value = "UPDATE  device set online=false WHERE device_sn NOT in (SELECT device_sn FROM iot_ts_kv_last_view)", nativeQuery = true)
    @Transactional
    @Modifying
    void updateDeviceStatusNotInIotView();


    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tdevice \n" +
            "WHERE\n" +
            "\tnode_id IN ( SELECT node_id FROM node WHERE no_households =:no_households ) \n" +
            "\tAND config_key = 'other' \n"
//            + "\tAND online = TRUE"测试情况设备都不在线临时注释
            , nativeQuery = true)
    List<Device> findAllOnlineDeviceByNode_NoHouseholds(@Param("no_households") String no_households);


    @Query(value = "UPDATE  device set mec_online=:mecOnline , mec_name=:mecName  WHERE mec_id  =:mecId", nativeQuery = true)
    @Transactional
    @Modifying
    void updateDeviceMecOnlineAndMecName(@Param("mecOnline") boolean mecOnline,@Param("mecId") String mecId,@Param("mecName") String mecName);

}
