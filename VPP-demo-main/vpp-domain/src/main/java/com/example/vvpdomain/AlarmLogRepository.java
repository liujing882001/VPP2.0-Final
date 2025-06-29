package com.example.vvpdomain;

import com.example.vvpdomain.entity.AlarmLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 报警记录
 * @date 2022-07-01
 */
@Repository
public interface AlarmLogRepository extends JpaRepository<AlarmLog, String>, JpaSpecificationExecutor<AlarmLog> {


    @Query(value = "SELECT\n" +
            "\tseverity,\n" +
            "\tCOUNT (*) AS count \n" +
            "FROM\n" +
            "\talarm_log \n" +
            "WHERE\n" +
            "\tnode_id =:nodeId \n" +
            "\tAND to_char ( start_ts, 'yyyy-mm-dd' ) =:ymd \n" +
            "GROUP BY\n" +
            "\tseverity", nativeQuery = true)
    List<Object[]> getAlarmLogByYMD(@Param("nodeId") String nodeId, @Param("ymd") String ymd);


    @Query(value = "SELECT\n" +
            "\tsystem_name,\n" +
            "\tCOUNT ( * ) AS COUNT \n" +
            "FROM\n" +
            "\talarm_log \n" +
            "WHERE\n" +
            "\tnode_id =:nodeId \n" +
            "\tAND to_char( start_ts, 'yyyy-mm-dd' ) =:ymd \n" +
            "GROUP BY\n" +
            "\tsystem_name", nativeQuery = true)
    List<Object[]> getAlarmLogInfoByYMD(@Param("nodeId") String nodeId, @Param("ymd") String ymd);

    @Query(value = "select e from AlarmLog e where e.alarmId= ?1")
    AlarmLog findByAlarmId(@Param("alarmId") String alarmId);

    @Query(value = "select e from AlarmLog e where e.deviceId= ?1")
    List<AlarmLog> findByDeviceIdList(@Param("deviceId") String deviceId);


    @Query(value = "select e from AlarmLog e where e.deviceId= ?1")
    AlarmLog findByDeviceId(@Param("deviceId") String deviceId);


}