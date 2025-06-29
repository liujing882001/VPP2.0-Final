package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgStorageEnergyShareProportion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

/**
 * @author zph
 * @description 参数配置表-逻辑中需根据模块id和配置代码查询配置项,根据不同的配置值做出相应的处理.
 * @date 2022-07-27
 */
@Repository
public interface CfgStorageEnergyShareProportionRepository extends JpaRepository<CfgStorageEnergyShareProportion, String>, JpaSpecificationExecutor<CfgStorageEnergyShareProportion> {

    CfgStorageEnergyShareProportion findByNodeIdAndSystemIdAndOrder(String nodeId, String systemId, int order);

    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_share_proportion where node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    void deleteAllByNodeIdAndSystemId(@Param("nodeId") String node_id, @Param("systemId") String system_id);

    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_share_proportion where node_id= :nodeId", nativeQuery = true)
    void deleteAllByNodeId(@Param("nodeId") String nodeId);

    List<CfgStorageEnergyShareProportion> findAllByNodeIdAndOrderBetween(String nodeId,Integer st,Integer et);
}