package com.example.vvpdomain;

import com.example.vvpdomain.dto.FindPointValueAndTs;
import com.example.vvpdomain.entity.IotTsKvLast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description iot_ts_kv_last
 * @date 2022-07-01
 */
@Repository
public interface IotTsKvLastRepository extends JpaRepository<IotTsKvLast, String>, JpaSpecificationExecutor<IotTsKvLast> {


    List<IotTsKvLast> findAllByPointSnIn(List<String> pointSns);


	@Query(value = "select temp.rowid,\n" +
			"    temp.id,\n" +
			"    temp.province_region_id,\n" +
			"    temp.province_region_name,\n" +
			"    temp.city_region_id,\n" +
			"    temp.city_region_name,\n" +
			"    temp.county_region_id,\n" +
			"    temp.county_region_name,\n" +
			"    temp.node_id,\n" +
			"    temp.node_name,\n" +
			"    temp.latitude,\n" +
			"    temp.longitude,\n" +
			"    temp.system_id,\n" +
			"    temp.system_name,\n" +
			"    temp.device_sn,\n" +
			"    temp.device_name,\n" +
			"    temp.device_config_key,\n" +
			"    temp.point_sn,\n" +
			"    temp.point_name,\n" +
			"    temp.point_value,\n" +
			"    temp.point_desc,\n" +
			"    temp.point_value_type,\n" +
			"    temp.ts,\n" +
			"    temp.created_time,\n" +
			"    temp.update_time,\n" +
			"    temp.msg_type,\n" +
			"    temp.point_unit,\n" +
			"    temp.node_post_type\n" +
			"from (SELECT row_number() OVER (PARTITION BY itk.device_sn, itk.point_sn ORDER BY itk.ts DESC) AS rowid,\n" +
			"            itk.id,\n" +
			"            itk.province_region_id,\n" +
			"            itk.province_region_name,\n" +
			"            itk.city_region_id,\n" +
			"            itk.city_region_name,\n" +
			"            itk.county_region_id,\n" +
			"            itk.county_region_name,\n" +
			"            itk.node_id,\n" +
			"            itk.node_name,\n" +
			"            itk.latitude,\n" +
			"            itk.longitude,\n" +
			"            itk.system_id,\n" +
			"            itk.system_name,\n" +
			"            itk.device_sn,\n" +
			"            itk.device_name,\n" +
			"            itk.device_config_key,\n" +
			"            itk.point_sn,\n" +
			"            itk.point_name,\n" +
			"            itk.point_value,\n" +
			"            itk.point_desc,\n" +
			"            itk.point_value_type,\n" +
			"            itk.ts,\n" +
			"            itk.created_time,\n" +
			"            itk.update_time,\n" +
			"            itk.msg_type,\n" +
			"            itk.point_unit,\n" +
			"            itk.node_post_type\n" +
			"            from iot_ts_kv itk where itk.system_id=:systemId and itk.point_desc =:pointDesc and itk.msg_type = :megType" +
			" " +
			"and itk.ts > now() - interval '30' minute )temp\n" +
			"             WHERE temp.rowid = 1 and temp.node_id in (:nodeIds)",
			nativeQuery = true)
	List<IotTsKvLast> findLatestPointValue(@Param("nodeIds") Collection<String> nodeIds, @Param("systemId") String systemId,
	                                       @Param("pointDesc") String pointDesc,
	                                       @Param("megType") String megType);


	@Query(value = "SELECT temp.point_value " +
			"FROM (SELECT row_number() OVER (PARTITION BY itk.device_sn, itk.point_sn ORDER BY itk.ts DESC) AS rowid, " +
			"itk.node_id, " +
			"itk.point_value " +
			"FROM iot_ts_kv itk " +
			"WHERE itk.system_id = :systemId AND itk.point_desc = :pointDesc " +
			"AND itk.msg_type = :megType " +
			"AND itk.ts > :startTime) temp " +
			"WHERE temp.rowid = 1 AND temp.node_id IN (:nodeIds)",
			nativeQuery = true)
	List<String> findLatestPointValues(@Param("nodeIds") Collection<String> nodeIds,
									   @Param("systemId") String systemId,
									   @Param("pointDesc") String pointDesc,
									   @Param("megType") String megType,
									   @Param("startTime") Date startTime);

	@Query(value = "SELECT SUM\n" +
            "\t( CAST ( COALESCE ( NULLIF ( point_value, '' ), '0' ) AS FLOAT ) ) \n" +
            "FROM\n" +
            "\tiot_ts_kv_last_view \n" +
            "WHERE\n" +
            "\tsystem_id = 'nengyuanzongbiao' \n" +
            "\tAND point_desc = 'load' \n" +
            "\tAND msg_type = 'MSG' \n" +
            "\tAND node_id IN ( :nodeIds )", nativeQuery = true)
    double findJieRuLoadByNodeIds(@Param("nodeIds") Collection<String> nodeIds);

	@Query(value = "SELECT DISTINCT ON (node_id) node_id, point_value, ts\n" +
			"FROM iot_ts_kv\n" +
			"where\n" +
			"node_id IN (:nodeIds) AND \n" +
			"system_id = 'nengyuanzongbiao'\n" +
			"  AND point_desc = 'load'\n" +
			"  AND msg_type = 'MSG'\n" +
			"  AND ts >= NOW() - INTERVAL '7 day'\n" +
			"ORDER BY node_id, ts DESC;",nativeQuery = true)
	List<Object[]> findJieRuLoadByNodeIdsNew(@Param("nodeIds") Collection<String> nodeIds);


    IotTsKvLast findAllByNodeIdAndDeviceSnAndPointDesc(String nodeId, String deviceSn, String pointDesc);
	@Query(value = "SELECT itkv.pointValue FROM IotTsKvLast itkv WHERE itkv.nodeId = :nodeId AND itkv.deviceSn = :deviceSn AND itkv.pointDesc = :pointDesc")
	String findPointValueByNodeIdAndDeviceSnAndPointDesc(
			@Param("nodeId") String nodeId,
			@Param("deviceSn") String deviceSn,
			@Param("pointDesc") String pointDesc
	);

	@Query(value = "SELECT itkv.pointValue FROM IotTsKvLast itkv WHERE itkv.nodeId = :nodeId AND itkv.deviceSn = :deviceSn AND itkv.pointSn = :pointSn AND itkv.pointDesc = :pointDesc")
	String findLastValue(
			@Param("nodeId") String nodeId,
			@Param("deviceSn") String deviceSn,
			@Param("pointSn") String pointSn,
			@Param("pointDesc") String pointDesc
	);

	@Query(value = "SELECT new com.example.vvpdomain.dto.FindPointValueAndTs(p.pointValue, p.ts) FROM IotTsKvLast p WHERE p.nodeId = :nodeId AND p.deviceSn = :deviceSn AND p.pointDesc = :pointDesc")
	FindPointValueAndTs findPointValueAndTsByNodeIdAndDeviceSnAndPointDesc(
			@Param("nodeId") String nodeId,
			@Param("deviceSn") String deviceSn,
			@Param("pointDesc") String pointDesc
	);

	List<IotTsKvLast> findAllByNodeIdAndPointDesc(String nodeId, String pointDesc);


	List<IotTsKvLast> findAllByNodeId(String nodeId);

	IotTsKvLast findAllByNodeIdAndDeviceSnAndAndPointSnAndPointDesc(String nodeId, String deviceSn, String pointSn, String pointDesc);

}