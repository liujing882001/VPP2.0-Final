package com.example.vvpdomain;

import com.example.vvpdomain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;

@Repository
public interface PointModelMappingRepository extends JpaRepository<PointModelMapping, String>, JpaSpecificationExecutor<PointModelMapping> {

	PointModelMapping findByStationAndPointModelAndDeviceList(StationNode nodeId, PointModel model_id,String device);

	@Query(value = "SELECT * FROM point_model_mapping WHERE mapping_id IN :mappings", nativeQuery = true)
	List<PointModelMapping> findAllByMappings(@Param("mappings") List<String> mappings);
	@Query(value = "SELECT * FROM point_model_mapping pm WHERE pm.model_id IN (SELECT id FROM point_model WHERE \"key\" = :key) AND pm.station_id IN (:nodeIds)", nativeQuery = true)
	List<PointModelMapping> findByKeyAndNodeIdNative(@Param("key") String key, @Param("nodeIds") List<String> nodeIds);

	@Query(value = "SELECT * FROM point_model_mapping WHERE ( :nodeIds IS NULL OR station_id IN :nodeIds ) AND mapping_type = :mappingType", nativeQuery = true)
	List<PointModelMapping> findAllByStationIdAndMappingType(
			@Param("nodeIds") List<String> nodeIds,
			@Param("mappingType") String mappingType
	);

	List<PointModelMapping> findAllByStation_StationId(String stationId);
	@Query("SELECT p FROM PointModelMapping p " +
			"JOIN p.pointModel pm " +
			"WHERE p.station.stationId LIKE %:stationId% " +
			"AND pm.pointDesc = :pointDesc")
	PointModelMapping findByStationIdContainingAndPointDesc(@Param("stationId") String stationId,
															@Param("pointDesc") String pointDesc);

	List<PointModelMapping> findAllByStation_StationIdAndPointModel_PointDesc(String stationId,String pointDesc);

	PointModelMapping findByMappingId(String mappingId);

	List<PointModelMapping> findAllByPointModel_Key(String pointKey);

	@Query(value = "SELECT * FROM ai_load_forecasting WHERE system_id = 'nengyuanzongbiao' " +
			" and to_char(count_data_time,'yyyy-mm-dd') in (:countDataTimes) and real_value is not null and real_value != '-' ", nativeQuery = true)
	List<AiLoadForecasting> findAllByCountDataTimes(@Param("countDataTimes") Collection<String> countDataTimes);
	@Transactional
	default void saveOrUpdateAll(List<PointModelMapping> entities) {
		for (PointModelMapping entity : entities) {
			saveOrUpdate(entity);
		}
	}

	@Transactional
	default void saveOrUpdate(PointModelMapping entity) {
		// 根据 column1 和 column2 查找是否有相同记录
		PointModelMapping existingEntity = findByStationAndPointModelAndDeviceList(entity.getStation(), entity.getPointModel(),entity.getDeviceList());
		if (existingEntity != null) {
			// 如果存在则更新已有记录
			existingEntity.setMappingType(entity.getMappingType());
			existingEntity.setDeviceList(entity.getDeviceList());
			// 需要更新的其他字段
			save(existingEntity);
		} else {
			// 如果不存在则直接保存新记录
			save(entity);
		}

	}


}
