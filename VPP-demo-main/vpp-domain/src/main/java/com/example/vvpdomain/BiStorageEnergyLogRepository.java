package com.example.vvpdomain;


import com.example.vvpdomain.dto.RAEnergySocDTO;
import com.example.vvpdomain.entity.BiStorageEnergyLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author kh
 * @description 储能资源-电站日志信息
 * @date 2024-04-09
 */
@Repository
public interface BiStorageEnergyLogRepository extends JpaRepository<BiStorageEnergyLog, String>, JpaSpecificationExecutor<BiStorageEnergyLog> {


    @Query(value = "select soc,ts " +
            "from bi_storage_energy_log " +
            "where node_id= :nodeId " +
            "and ts >= :starDate " +
            "and ts < :endDate " +
            "order by ts", nativeQuery = true)
    List<Object[]> findSocByNodeId(@Param("nodeId") String nodeId,
                                   @Param("starDate") Date starDate,
                                   @Param("endDate") Date endDate);

    @Query(value = "select soh,ts " +
            "from bi_storage_energy_log " +
            "where node_id= :nodeId " +
            "and ts >= :starDate " +
            "and ts < :endDate " +
            "order by ts", nativeQuery = true)
    List<Object[]> findSohByNodeId(@Param("nodeId") String nodeId,
                                   @Param("starDate") Date starDate,
                                   @Param("endDate") Date endDate);

    @Query("SELECT new com.example.vvpdomain.dto.RAEnergySocDTO(t.nodeId, t.soc, t.ts, MIN(t.createdTime)) " +
            "FROM BiStorageEnergyLog t " +
            "WHERE t.nodeId IN :nodeIds " +
            "AND t.ts IN :dateList " +
            "GROUP BY t.nodeId, t.ts, t.soc")
    List<RAEnergySocDTO> findRAEnergySocDTOByNodeIdsAndDateList(@Param("nodeIds") List<String> nodeIds,
                                                                @Param("dateList") List<Date> dateList);



    @Query(value = "select * from bi_storage_energy_log where node_id =:nodeId order by ts DESC LIMIT 1", nativeQuery = true)
    BiStorageEnergyLog getLatestLog(@Param("nodeId") String node);

    BiStorageEnergyLog findBiStorageEnergyLogByNodeIdAndTs(String node,Date ts);

}
