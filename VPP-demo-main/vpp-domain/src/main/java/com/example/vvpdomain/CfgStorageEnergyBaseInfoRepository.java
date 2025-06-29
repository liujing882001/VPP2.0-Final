package com.example.vvpdomain;

import com.example.vvpdomain.dto.RAEnergyBaseDTO;
import com.example.vvpdomain.entity.CfgStorageEnergyBaseInfo;
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
 * @description 储能 配置基本信息
 * @date 2022-07-24
 */
@Repository
public interface CfgStorageEnergyBaseInfoRepository extends JpaRepository<CfgStorageEnergyBaseInfo, String>, JpaSpecificationExecutor<CfgStorageEnergyBaseInfo> {

    List<CfgStorageEnergyBaseInfo> findAllByNodeIdIn(Collection<String> nodeIds);

    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_base_info where node_id= :nodeId ", nativeQuery = true)
    void deleteAllByNodeId(@Param("nodeId") String nodeId);
    @Query("SELECT new com.example.vvpdomain.dto.RAEnergyBaseDTO(t.nodeId, t.storageEnergyCapacity, t.maxChargePercent, t.minDischargePercent ) " +
            "FROM CfgStorageEnergyBaseInfo t " +
            "WHERE t.nodeId IN :nodeIds")
    List<RAEnergyBaseDTO> findRAEnergyBaseDTOByNodeIds(@Param("nodeIds") List<String> nodeIds);
    @Query(value = "SELECT * FROM cfg_storage_energy_base_info WHERE system_id='nengyuanzongbiao' and node_id=:nodeId", nativeQuery = true)
    CfgStorageEnergyBaseInfo findCfgStorageEnergyBaseInfoByNodeId(@Param("nodeId") String nodeId);


    @Query(value = "SELECT Sum(storage_energy_load)  AS device_rated_power from cfg_storage_energy_base_info  WHERE node_id in (SELECT node_id FROM node WHERE node_post_type='storageEnergy') AND system_id ='nengyuanzongbiao'", nativeQuery = true)
    double sumStorageEnergyPowers();
    @Query(value = "SELECT " +
            "COALESCE(SUM(storage_energy_load), 0) AS sum_storage_energy_load, " +
            "COALESCE(SUM(storage_energy_capacity), 0) AS sum_storage_energy_capacity " +
            "FROM cfg_storage_energy_base_info " +
            "WHERE node_id IN (:nodeIds) " +
            "AND system_id = 'nengyuanzongbiao'",
            nativeQuery = true)
    Object[] sumStorageEnergyByNodeIds(@Param("nodeIds") List<String> nodeIds);

    @Query(value = "SELECT Sum(storage_energy_capacity)  from cfg_storage_energy_base_info  WHERE node_id in (SELECT node_id FROM node WHERE node_post_type='storageEnergy') AND system_id ='nengyuanzongbiao'", nativeQuery = true)
    double sumStorageEnergyCapacity();
}