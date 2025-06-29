package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgStorageEnergyStrategyPower96Ai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface CfgStorageEnergyStrategyPower96AiRepository extends JpaRepository<CfgStorageEnergyStrategyPower96Ai, String>, JpaSpecificationExecutor<CfgStorageEnergyStrategyPower96Ai> {

    @Query(value = "select * from cfg_storage_energy_strategy_power_96_ai where node_id= :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate order by time_scope", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96Ai> findAllBySystemIdAndNodeIde(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

    @Query(value = "select node_id,power,effective_date,s_time from cfg_storage_energy_strategy_power_96_ai where node_id in (:nodeIds) and effective_date >= :starDate and effective_date < :endDate ", nativeQuery = true)
    List<Object[]> findAllBySystemIdAndNodeIds(@Param("nodeIds") List<String> nodeIds, @Param("starDate") Date starDate, @Param("endDate") Date endDate);
}