package com.example.vvpdomain;

import com.example.vvpdomain.dto.RAPower96DTO;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategyPower96;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;

@Repository
public interface CfgStorageEnergyStrategyPower96Repository extends JpaRepository<CfgStorageEnergyStrategyPower96, String>, JpaSpecificationExecutor<CfgStorageEnergyStrategyPower96> {
    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate order by time_scope", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllBySystemIdAndNodeIde(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);
    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and effective_date >= :starDate and effective_date <= :endDate order by time_scope", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllByNodeIde(@Param("nodeId") String nodeId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllBySystemIdAndNodeId(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);
    long countBySystemIdAndNodeIdAndEffectiveDateBetween(String nodeId, String systemId, Date startDate, Date endDate);

    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date = :effectiveDate order by s_time", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllByNodeId(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("effectiveDate") Date effectiveDate);
    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id IN :nodeIds and system_id = :systemId and effective_date = :effectiveDate order by s_time", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllByNodeIds(@Param("nodeIds") List<String> nodeIds, @Param("systemId") String systemId, @Param("effectiveDate") Date effectiveDate);
    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id IN :nodeIds and effective_date BETWEEN :sDate AND :eDate ", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllByNodeIdsAndEffDate(@Param("nodeIds") List<String> nodeIds, @Param("sDate") Date sDate,@Param("eDate") Date eDate);

    @Query("SELECT new com.example.vvpdomain.dto.RAPower96DTO(t.nodeId, t.effectiveDate, t.sTime,t.strategy) " +
            "FROM CfgStorageEnergyStrategyPower96 t " +
            "WHERE t.nodeId IN :nodeIds AND t.effectiveDate BETWEEN :sDate AND :eDate")
    List<RAPower96DTO> findRAPower96DTOByNodeIdsAndEffDate(@Param("nodeIds") List<String> nodeIds,
                                                           @Param("sDate") Date sDate,
                                                           @Param("eDate") Date eDate);
    List<CfgStorageEnergyStrategyPower96> findAllByNodeId(String nodeId);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update cfg_storage_energy_strategy_power_96 set distribute_status = 1 where node_id = :nodeId and system_id = :systemId and effective_date = :effectiveDate", nativeQuery = true)
    void updateDistributeStatus(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("effectiveDate") Date effectiveDate);
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "UPDATE cfg_storage_energy_strategy_power_96 " +
            "SET distributeStatus = 1 " +
            "WHERE nodeId IN (:nodeIds) " +
            "AND effectiveDate = :effectiveDate", nativeQuery = true)
    void updateDistributeStatusByNodeIds(@Param("nodeIds") List<String> nodeIds,
                                         @Param("effectiveDate") Date effectiveDate);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update cfg_storage_energy_strategy_power_96 set power = :power, strategy = :strategy where node_id = :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate and s_time >= :startTime and e_time <= :endTime", nativeQuery = true)
    void updatePower(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate, @Param("startTime") String startTime, @Param("endTime") String endTime, @Param("power") Double power, @Param("strategy") String strategy);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update cfg_storage_energy_strategy_power_96 set policy_model = 1 where node_id = :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate and e_time > :startTime and e_time <= :endTime", nativeQuery = true)
    void updatePolicyModel(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "update cfg_storage_energy_strategy_power_96 set policy_model = 0 where node_id = :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate and e_time > :startTime and e_time <= :endTime", nativeQuery = true)
    void updatePolicyModelInit(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate, @Param("startTime") String startTime, @Param("endTime") String endTime);

    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    void deleteAllByNodeIdAndSystemId(@Param("nodeId") String nodeId, @Param("systemId") String systemId);

    @Query(value = "select distinct node_id from cfg_storage_energy_strategy_power_96", nativeQuery = true)
    List<String> findNode();

    @Query(value = "select distinct node_id from cfg_storage_energy_strategy_power_96 where effective_date = :nextDay and distribute_status = 1", nativeQuery = true)
    List<String> findNoDistributionNode(@Param("nextDay") Date nextDay);

    List<CfgStorageEnergyStrategyPower96> findAllByNodeIdAndSystemId(String nodeId, String systemId);

    List<CfgStorageEnergyStrategyPower96> findAllByNodeIdAndSystemIdAndEffectiveDate(String nodeId, String systemId, Date effectiveDate);

    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date = :date order by s_time", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllByNodeIdAndSystemIdorderEffectiveDate(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("date") Date date);

    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date = :date and s_time = :sTime order by s_time", nativeQuery = true)
    CfgStorageEnergyStrategyPower96 findAllBySystemIdAndNodeIdeAndSTime(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("date") Date date, @Param("sTime") String sTime);

    @Query(value = "select strategy from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date = :date and s_time = :sTime ", nativeQuery = true)
    String findStrategyBySystemIdAndNodeIdeAndSTime(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("date") Date date, @Param("sTime") String sTime);

    @Query(value = "select * from cfg_storage_energy_strategy_power_96 where node_id= :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date < :endDate order by time_scope", nativeQuery = true)
    List<CfgStorageEnergyStrategyPower96> findAllBySystemIdAndNodeIdeBlock(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);
    @Query(value = "select node_id,power,effective_date,s_time from cfg_storage_energy_strategy_power_96 where node_id in (:nodeIds) and effective_date >= :starDate and effective_date < :endDate ", nativeQuery = true)
    List<Object[]> findAllBySystemIdAndNodeIds(@Param("nodeIds") List<String> nodeIds, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

}