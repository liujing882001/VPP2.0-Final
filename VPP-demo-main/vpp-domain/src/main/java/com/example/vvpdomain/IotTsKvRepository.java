package com.example.vvpdomain;

import com.example.vvpdomain.entity.IotTsKv;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description iot_ts_kv
 * @date 2022-07-01
 */
@Repository
public interface IotTsKvRepository extends JpaRepository<IotTsKv, String>, JpaSpecificationExecutor<IotTsKv> {

    @Query(value = "SELECT * FROM iot_ts_kv WHERE node_id = :nodeId AND created_time BETWEEN :start AND :end"
            , nativeQuery = true)
    List<IotTsKv> findAllForAlgorithm(
            @Param("nodeId") String nodeId,
            @Param("start") Date start,
            @Param("end") Date end);

    List<IotTsKv> findAllByPointSnAndTsBetweenOrderByTsAsc(String pointSn, Date start, Date end);

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tiot_ts_kv \n" +
            "WHERE\n" +
            "\tnode_id =:node_id \n" +
            "\tAND system_id =:system_id \n" +
            "\tAND device_sn =:device_sn \n" +
            "\tAND point_sn =:point_sn \n" +
            "\tAND device_config_key =:device_config_key \n" +
            "\tAND point_desc =:point_desc \n" +
            "\tAND ts <=:ts \n" +
            "ORDER BY\n" +
            "\tts DESC \n" +
            "\tLIMIT 1", nativeQuery = true)
    IotTsKv findNode_System_IotTsKvLastInfo(@Param("node_id") String node_id
            , @Param("system_id") String system_id
            , @Param("device_sn") String device_sn
            , @Param("point_sn") String point_sn
            , @Param("device_config_key") String device_config_key
            , @Param("point_desc") String point_desc
            , @Param("ts") Date ts);

    @Query(value = "delete from iot_ts_kv where device_sn= :deviceSn ", nativeQuery = true)
    @Modifying
    void deleteByDeviceSn(@Param("deviceSn") String deviceSn);


    List<IotTsKv> findAllByNodeIdAndSystemIdAndTsBetweenOrderByTsAsc(String nodeId, String systemId, Date start, Date end);

    List<IotTsKv> findAllByNodeIdAndPointNameAndTsBetweenOrderByTsAsc(String nodeId, String pointName, Date start, Date end);

    @Query(value = "select * from iot_ts_kv where node_id= :nodeId and node_post_type= :nodePostType and ts between :start and :end order by ts", nativeQuery = true)
    List<IotTsKv> findAllByNodeIdAndNodePostTypeeAndTsBetweenOrderByTs(@Param("nodeId") String nodeId, @Param("nodePostType") String nodePostType, @Param("start") Date start, @Param("end") Date end);

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tiot_ts_kv \n" +
            "WHERE\n" +
            "\tnode_id =:node_id \n" +
            "\tAND device_sn =:device_sn \n" +
            "\tAND point_desc = 'host_running_status'\n" +
            "\tand ts >=:s\n" +
            "\tand ts <=:e", nativeQuery = true)
    List<IotTsKv> findAllDeviceRunStatus(@Param("node_id") String node_id
                                            , @Param("device_sn") String device_sn
                                            , @Param("s") Date s
                                            , @Param("e") Date e);

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tiot_ts_kv \n" +
            "WHERE\n" +
            "\tnode_id =:nodeId \n" +
            "\tAND point_desc =:pointDesc \n" +
            "\tand ts >=:s \n" +
            "\tand ts <=:e \n" +
            "\tORDER BY ts", nativeQuery = true)
    List<IotTsKv> findAllByPointDesc(@Param("nodeId") String nodeId
            , @Param("pointDesc") String pointDesc
            , @Param("s") Date start
            , @Param("e") Date end);

    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tiot_ts_kv \n" +
            "WHERE\n" +
            "\tnode_id =:node_id \n" +
            "\tAND device_sn =:device_sn \n" +
            "\tAND point_desc = 'host_running_status'\n" +
            "\tand ts >=:s\n" +
            "\tand ts <=:e\n" +
            "\tand point_value='On'",nativeQuery = true)
    List<IotTsKv> findAllDeviceStatusStatus_On(@Param("node_id") String node_id
                                                , @Param("device_sn") String device_sn
                                                , @Param("s") Date s
                                                , @Param("e") Date e);

    @Query(value = "SELECT * FROM iot_ts_kv WHERE device_sn = :deviceSn AND point_name = :pointName And ts >= :start And ts <= :end", nativeQuery = true)
    List<IotTsKv> findPointValueByDeviceSnAndPointName(
            @Param("start") Date start,
            @Param("end") Date end,
            @Param("deviceSn") String deviceSn,
            @Param("pointName") String pointName);

    List<IotTsKv> findAllByPointDescInAndTsBetweenOrderByTsDesc(List<String> pointDescList, Date start, Date end);

    List<IotTsKv> findAllByPointDescInAndTsBetween(List<String> pointDescList, Date start, Date end);




    @Query(value ="SELECT\n" +
            "    * \n" +
            "FROM\n" +
            "    iot_ts_kv \n" +
            "WHERE\n" +
            "    point_desc = 'load' \n" +
            "    AND device_sn =:device_sn \n" +
            "    AND point_sn =:point_sn \n" +
            "ORDER BY\n" +
            "    ts \n" +
            "LIMIT 1;",nativeQuery = true)
    IotTsKv findFirstLoadDeivcePointInfo(@Param("device_sn") String device_sn, @Param("point_sn") String point_sn);

    List<IotTsKv> findAllByNodeIdAndPointDescAndDeviceSnOrderByTsDesc(String nodeId, String pointDesc,String DeviceSn, Pageable pageable);

    List<IotTsKv> findAllByNodeIdAndPointDescAndTsBetweenOrderByTsDesc(String nodeId,String pointDesc,Date st,Date et);

    List<IotTsKv> findTop30ByOrderByUpdateTimeDesc();

}
