package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgPhotovoltaicBaseInfo;
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
 * @description 光伏基本配置
 * @date 2022-07-24
 */
@Repository
public interface CfgPhotovoltaicBaseInfoRepository extends JpaRepository<CfgPhotovoltaicBaseInfo, String>, JpaSpecificationExecutor<CfgPhotovoltaicBaseInfo> {

    List<CfgPhotovoltaicBaseInfo> findAllByNodeIdIn(Collection<String> nodeIds);

    @Query(value = "SELECT * FROM cfg_photovoltaic_base_info WHERE system_id='nengyuanzongbiao' and node_id=:nodeId", nativeQuery = true)
    CfgPhotovoltaicBaseInfo findCfgPhotovoltaicBaseInfoByNodeId(@Param("nodeId") String nodeId);


    @Transactional
    @Modifying
    @Query(value = "delete from cfg_photovoltaic_base_info where node_id= :nodeId ", nativeQuery = true)
    void deleteAllByNodeId(@Param("nodeId") String nodeId);


    CfgPhotovoltaicBaseInfo findAllByNodeIdAndSystemId(String nodeId, String systemId);


    @Query(value = "SELECT SUM\n" +
            "\t( photovoltaic_installed_capacity ) \n" +
            "FROM\n" +
            "\tcfg_photovoltaic_base_info \n" +
            "WHERE\n" +
            "\tnode_id IN ( SELECT node_id FROM node WHERE node_post_type = 'pv' ) \n" +
            "\tAND system_id = 'nengyuanzongbiao'", nativeQuery = true)
    double sumPhotovoltaicPowers();

    @Query(value = "SELECT COALESCE(SUM(photovoltaic_installed_capacity), 0) " +
            "FROM cfg_photovoltaic_base_info " +
            "WHERE node_id IN (:nodeIds) " +
            "AND system_id = 'nengyuanzongbiao'",
            nativeQuery = true)
    double sumPhotovoltaicPowersByNodeIds(@Param("nodeIds") List<String> nodeIds);

}