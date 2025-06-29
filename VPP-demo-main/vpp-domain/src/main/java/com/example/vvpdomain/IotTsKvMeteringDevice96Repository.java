package com.example.vvpdomain;

import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
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
 * @description 节点系统 24点 用电数据  非累计
 * @date 2022-08-19
 */
@Repository
public interface IotTsKvMeteringDevice96Repository extends JpaRepository<IotTsKvMeteringDevice96, String>,
        JpaSpecificationExecutor<IotTsKvMeteringDevice96> {


	@Query(value = "SELECT\n" +
			"\tnode_id,\n" +
			"\tsystem_id,\n" +
			"\tcount_data_time,\n" +
			"\ttotal_power_energy,\n" +
			"\tenergy,\n" +
			"\tload \n" +
			"FROM\n" +
			"\t(\n" +
			"\tSELECT \n" +
			"\t  A.node_id,\n" +
			"\t\tA.system_id,\n" +
			"\t\tA.count_data_time,\n" +
			"\t\tSUM(A.total_power_energy) as total_power_energy ,\n" +
			"\t\tMAX ( CASE point_desc WHEN 'energy' THEN h_total_use ELSE 0 END ) as energy,\n" +
			"\t\tMAX ( CASE point_desc WHEN 'load' THEN h_total_use ELSE 0 END ) AS load \n" +
			"\tFROM\n" +
			"\t\tiot_ts_kv_metering_device_96 AS A \n" +
			"\tWHERE\t\t\n" +
			"\t\tA.config_key = 'metering_device' \n" +
			"\t\tAND A.system_id = 'nengyuanzongbiao' \n" +
			"\t\tAND A.node_id =:nodeId \n" +
			"\tGROUP BY\n" +
			"\t\tnode_id,\n" +
			"\t\tsystem_id,\n" +
			"\t\tcount_data_time \n" +
			"\t) AS b \n" +
			"ORDER BY\n" +
			"\tb.count_data_time DESC \n" +
			"\tLIMIT 1", nativeQuery = true)
	List<Object[]> findLastNodeInfo(@Param("nodeId") String nodeId);


	List<IotTsKvMeteringDevice96> findAllByNodeIdAndSystemIdAndConfigKeyAndPointDescAndCountDate(String node_id
			, String system_id
			, String config_key
			, String pointDesc
			, Date count_date);


	@Query(value = "SELECT\n" +
			"\tto_char( count_data_time, 'yyyy-mm' ) AS s,\n" +
			"\tSUM ( h_total_use ) AS h_total_use \n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id =:nodeId \n" +
			"\tAND device_sn =:device_sn \n" +
			"\tAND point_desc = 'energy' \n" +
			"\tAND count_data_time <= now() \n" +
			"\tAND to_char( count_data_time, 'yyyy-mm' ) >=:dt_ym \n" +
			"GROUP BY\n" +
			"\tdevice_sn,\n" +
			"\tto_char( count_data_time, 'yyyy-mm' )", nativeQuery = true)
	List<Object[]> findInOutEnergyGroupYearMonth(@Param("nodeId") String nodeId
			, @Param("device_sn") String device_sn
			, @Param("dt_ym") String dt_ym);


	@Query(value = "SELECT\n" +
			"\tto_char( count_data_time, 'yyyy-mm' ) AS s,\n" +
			"\tSUM ( h_total_use ) AS h_total_use \n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id =:nodeId \n" +
			"\tAND device_sn =:device_sn \n" +
			"\tAND point_desc = 'energy' \n" +
			"\tAND to_char( count_data_time, 'yyyy-mm' ) <=:dt_ym \n" +
			"GROUP BY\n" +
			"\tdevice_sn,\n" +
			"\tto_char( count_data_time, 'yyyy-mm' )", nativeQuery = true)
	List<Object[]> findInOutEnergyEGroupYearMonth(@Param("nodeId") String nodeId
			, @Param("device_sn") String device_sn
			, @Param("dt_ym") String dt_ym);


	List<IotTsKvMeteringDevice96> findAllByNodeIdAndSystemIdAndPointDescAndCountDateBetween(String nodeId, String systemId, String pointDesc,
	                                                                                        Date startDate, Date endDate);

	@Query(value = "SELECT * FROM iot_ts_kv_metering_device_96 WHERE node_id = :nodeId AND point_desc = :pointDesc AND count_date >= :startDate",
            nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllByNodeIdAndPointDescAndCountDateGreaterThanEqual(@Param("nodeId") String nodeId,
                                                                                          @Param("pointDesc") String pointDesc,
                                                                                          @Param("startDate") Date startDate);


	@Query(value = "select\n" +
			"        iottskvmet0_.h_total_use as h_total13_51_,\n" +
			"        iottskvmet0_.count_data_time as time_sc26_51_\n" +
			"    from\n" +
			"        iot_ts_kv_metering_device_96 iottskvmet0_ \n" +
			"    where\n" +
			"        iottskvmet0_.node_id= :nodeId" +
			"        and iottskvmet0_.device_sn= :deviceSn" +
			"        and iottskvmet0_.point_sn= :pointSn" +
			"        and (\n" +
			"            iottskvmet0_.count_date between :st and :et" +
			"        )", nativeQuery = true)
	List<Object[]> findHTotalUseAndTimeScope(@Param("nodeId") String nodeId, @Param("deviceSn") String deviceSn, @Param("pointSn") String pointSn,
	                                         @Param("st") Date start, @Param("et") Date end);


	List<IotTsKvMeteringDevice96> findAllByNodeIdAndDeviceSnAndPointSnAndCountDateGreaterThanEqual(String nodeId, String deviceSn, String pointSn,
                                                                                                   Date start);


	List<IotTsKvMeteringDevice96> findAllByNodePostTypeAndPointDescAndConfigKeyAndCountDateIsAfter(String nodePostType, String pointDesc,
                                                                                                   String configKey, Date CountDate);


	@Query(value = "select\n" +
			"        iottskvmet0_.h_total_use as h_total13_51_,\n" +
			"        iottskvmet0_.count_data_time as time_sc26_51_\n" +
			"    from\n" +
			"        iot_ts_kv_metering_device_96 iottskvmet0_ \n" +
			"    where\n" +
			"        iottskvmet0_.node_id= :nodeId" +
			"        and iottskvmet0_.point_desc= :pointDesc" +
			"        and (\n" +
			"            iottskvmet0_.count_date between :st and :et" +
			"        )", nativeQuery = true)
	List<Object[]> findAllByNodeIdAndPointDescAndCountDataTime(@Param("nodeId") String nodeId, @Param("pointDesc") String PointDesc,
	                                                           @Param("st") Date start, @Param("et") Date end);

	List<IotTsKvMeteringDevice96> findAllByNodeIdAndPointDescAndCountDataTimeBetween(String nodeId, String PointDesc, Date start, Date end);

	@Query(value = "SELECT\n" +
			"\tCOALESCE ( SUM ( h_total_use ), 0 )\n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'pv' ) \n" +
			"\tAND system_id = 'nengyuanzongbiao' \n" +
			"\tAND point_desc = 'energy'\n" +
			"\tAND config_key='metering_device'\n" +
			"\tand count_date=:dt", nativeQuery = true)
	double findALLPVNodeEnergyCountByDate(@Param("dt") Date dt);


	@Query(value = "SELECT\n" +
			"\ttotal_power_energy \n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'pv' ) \n" +
			"\tAND system_id = 'nengyuanzongbiao' \n" +
			"\tAND point_desc = 'energy' \n" +
			"\tAND config_key = 'metering_device' \n" +
			"\tAND count_date <= :dt \n" +
			"ORDER BY\n" +
			"\tcount_data_time DESC \n" +
			"\tLIMIT 1", nativeQuery = true)
	double findALLPVNodeTotalEnergyCountByDate(@Param("dt") Date dt);


	@Query(value = "SELECT\n" +
			"\tCOALESCE ( SUM ( h_total_use ), 0 )\n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'load' ) \n" +
			"\tAND system_id = 'nengyuanzongbiao' \n" +
			"\tAND point_desc = 'energy'\n" +
			"\tAND config_key='metering_device'\n" +
			"\tand count_date=:dt", nativeQuery = true)
	double findALLLoadNodeEnergyCountByDate(@Param("dt") Date dt);


	@Query(value = "SELECT\n" +
			"\tCOALESCE ( SUM ( h_total_use ), 0 )\n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'load' ) \n" +
			"\tAND system_id = 'nengyuanzongbiao' \n" +
			"\tAND point_desc = 'energy'\n" +
			"\tAND config_key='metering_device'\n" +
			"\tand count_data_time>=:s_dt and count_data_time <=:e_dt", nativeQuery = true)
	double findALLLoadNodeEnergyCountByDateBetween(@Param("s_dt") Date s_dt, @Param("e_dt") Date e_dt);


	@Query(value = "delete from iot_ts_kv_metering_device_96 where device_sn= :deviceSn ", nativeQuery = true)
	@Modifying
	void deleteByDeviceSn(@Param("deviceSn") String deviceSn);

	@Query(value = "SELECT\n" +
			"\t* \n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96\n" +
			"WHERE\n" +
			"\tnode_id IN ( SELECT node_id FROM node WHERE no_households=:no_household) \n" +
			"\tAND iot_ts_kv_metering_device_96.system_id='nengyuanzongbiao'\n" +
			"\tand iot_ts_kv_metering_device_96.config_key='metering_device'\n" +
			"\tand iot_ts_kv_metering_device_96.point_desc='load'\n" +
			"\tand count_date >= now() - interval '1' day\n" +
			"\tand count_date <= now()\n" +
			"\tORDER BY count_data_time asc", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAll96LoadData(@Param("no_household") String no_household);


	@Query(value = "SELECT\n" +
			"\t*\n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tsystem_id = 'nengyuanzongbiao' \n" +
			"\tAND config_key = 'metering_device' \n" +
			"\tAND point_desc = 'energy' \n" +
			"\tAND count_date >= :s_dt\n" +
			"\tand node_id in(SELECT node_id FROM node where node_post_type='pv')", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findNowYearPVEnergyPower(@Param("s_dt") Date s_dt);

	@Query(value = "SELECT\n" +
			"\t*\n" +
			"FROM\n" +
			"\tiot_ts_kv_metering_device_96 \n" +
			"WHERE\n" +
			"\tsystem_id = 'chuneng' \n" +
			"\tAND node_id = :nodeId \n" +
			"\tAND count_date >= :startDate\n" +
			"\tAND count_date <= :endDate\n" +
			"\tORDER BY count_date asc", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllBySystemIdAndNodeId1(@Param("nodeId") String nodeId, @Param("startDate") Date startDate,
                                                              @Param("endDate") Date endDate);

	@Query(value = "select * from iot_ts_kv_metering_device_96 where count_date = '2024-01-14' and province_region_id = '1' order by count_time " +
            "Limit 96", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllBySystemIdAndNodeIde();

	@Query(value = "select sum(h_total_use) as hTotalUse, count_data_time as countDataTime from iot_ts_kv_metering_device_96 where node_id= :nodeId " +
            "and system_id = :systemId and count_date >= :starDate and count_date <= :endDate and node_post_type = :nodePostType and point_desc = " +
            ":energy group by count_data_time order by count_data_time", nativeQuery = true)
	List<Object[]> findAllBySystemIdAndNodeIdeAndNodePostType(@Param("nodeId") String nodeId, @Param("systemId") String systemId,
                                                              @Param("starDate") Date starDate, @Param("endDate") Date endDate, @Param(
                                                                      "nodePostType") String nodePostType, @Param("energy") String energy);

	@Query(value = "SELECT * FROM iot_ts_kv_metering_device_96 WHERE node_id = '488067feec453899dcbe8d2660e39c7c' AND system_id = 'nengyuanzongbiao'" +
            " AND device_sn = :deviceSn AND node_post_type = :nodePostType AND count_data_time >= :s_dt AND count_data_time <= :e_dt order by " +
            "count_data_time", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findHTotalUseByDeviceSnAndNodePostType(@Param("s_dt") Date s_dt, @Param("e_dt") Date e_dt,
                                                                         @Param("nodePostType") String nodePostType,
                                                                         @Param("deviceSn") String deviceSn);

	@Query(value = "select * from iot_ts_kv_metering_device_96 where node_id= :nodeId and system_id = :systemId and point_sn = :pointSn and " +
            "point_desc = :pointDesc and count_data_time >= :starDate and count_data_time < :endDate order by count_data_time", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllBySystemIdAndNodeIde(@Param("nodeId") String nodeId, @Param("systemId") String systemId,
                                                              @Param("pointSn") String pointSn, @Param("pointDesc") String pointDesc, @Param(
                                                                      "starDate") Date starDate, @Param("endDate") Date endDate);

	@Query(value = "select h_total_use,count_data_time" +
			" from iot_ts_kv_metering_device_96 " +
			"where node_id= :nodeId " +
			"and system_id = :systemId " +
			"and point_sn = :pointSn " +
			"and point_desc = :pointDesc " +
			"and count_data_time >= :starDate " +
			"and count_data_time < :endDate " +
			"order by count_data_time", nativeQuery = true)
	List<Object[]> findHTotalUseAndTimeBySystemIdAndNodeIde(@Param("nodeId") String nodeId, @Param("systemId") String systemId,
	                                                        @Param("pointSn") String pointSn, @Param("pointDesc") String pointDesc, @Param("starDate"
    ) Date starDate, @Param("endDate") Date endDate);

	@Query(value = "select node_id,h_total_use,count_data_time" +
			" from iot_ts_kv_metering_device_96 " +
			"where node_id in (:nodeIds) " +
			"and system_id = :systemId " +
			"and point_desc = :pointDesc " +
			"and count_data_time >= :starDate " +
			"and count_data_time < :endDate ", nativeQuery = true)
	List<Object[]> findHTotalUseAndTimeBySystemIdAndNodeIds(@Param("nodeIds") List<String> nodeIds, @Param("systemId") String systemId,
	                                                        @Param("pointDesc") String pointDesc, @Param("starDate") Date starDate,
                                                            @Param("endDate") Date endDate);

	@Query(value = "select node_id,h_total_use,count_data_time" +
			" from iot_ts_kv_metering_device_96 " +
			"where node_id in (:nodeIds) " +
			"and point_desc = :pointDesc " +
			"and count_data_time >= :starDate " +
			"and count_data_time < :endDate ", nativeQuery = true)
	List<Object[]> findHTotalUseAndTimeByNodeIds(@Param("nodeIds") List<String> nodeIds, @Param("pointDesc") String pointDesc,
	                                             @Param("starDate") Date starDate, @Param("endDate") Date endDate);

	@Query(value = "select * from iot_ts_kv_metering_device_96 where point_desc = :pointDesc and count_data_time >= :starDate and count_data_time < " +
            ":endDate order by count_data_time", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllByPointDescAndDate(@Param("pointDesc") String pointDesc, @Param("starDate") Date starDate,
                                                            @Param("endDate") Date endDate);

	@Query(value = "select * from iot_ts_kv_metering_device_96 where node_id= :nodeId and system_id = :systemId and  point_desc = :pointDesc and " +
            "count_data_time >= :starDate and count_data_time < :endDate order by count_data_time", nativeQuery = true)
	List<IotTsKvMeteringDevice96> findAllBySystemIdAndNodeIdAndDesc(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param(
            "pointDesc") String pointDesc, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

	List<IotTsKvMeteringDevice96> findAllByNodeIdAndPointSnAndPointDescAndCountDataTimeBetweenOrderByCountDataTimeDesc(String nodeId,
	                                                                                                                   String deviceSn,
	                                                                                                                   String pointDesc, Date st,
	                                                                                                                   Date et);
	@Query("SELECT d.countDataTime,d.hTotalUse FROM IotTsKvMeteringDevice96 d " +
			"WHERE d.nodeId = :nodeId " +
			"AND d.pointSn = :pointSn " +
			"AND d.pointDesc = :pointDesc " +
			"AND d.countDataTime BETWEEN :st AND :et ")
	List<Object[]> findAllValue(
			@Param("nodeId") String nodeId,
			@Param("pointSn") String pointSn,
			@Param("pointDesc") String pointDesc,
			@Param("st") Date st,
			@Param("et") Date et);
	@Query(value = "SELECT * FROM iot_ts_kv_metering_device_96 " +
			"WHERE node_id = :nodeId " +
			"and device_sn = :deviceSn " +
			"and point_sn = :pointSn " +
			"and point_desc = :pointDesc " +
			"and count_data_time >= :st ORDER BY count_data_time ASC limit 1", nativeQuery = true)
	IotTsKvMeteringDevice96 findFirst(@Param("nodeId") String nodeId, @Param("deviceSn") String deviceSn,
	                                  @Param("pointSn") String pointSn, @Param("pointDesc") String pointDesc,
	                                  @Param("st") Date st);

	@Query(value = "SELECT * FROM iot_ts_kv_metering_device_96 " +
			"WHERE node_id = :nodeId " +
			"and device_sn = :deviceSn " +
			"and point_sn = :pointSn " +
			"and point_desc = :pointDesc " +
			"and count_data_time <= :et ORDER BY count_data_time DESC limit 1", nativeQuery = true)
	IotTsKvMeteringDevice96 findLast(@Param("nodeId") String nodeId, @Param("deviceSn") String deviceSn, @Param("pointSn") String pointSn, @Param(
            "pointDesc") String pointDesc, @Param("et") Date et);
}