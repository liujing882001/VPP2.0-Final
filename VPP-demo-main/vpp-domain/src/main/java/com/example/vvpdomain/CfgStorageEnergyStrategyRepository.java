package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgStorageEnergyStrategy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.List;


/**
 * @author zph
 * @description 充放电比例
 * @date 2022-07-26
 */
@Repository
public interface CfgStorageEnergyStrategyRepository extends JpaRepository<CfgStorageEnergyStrategy, String>, JpaSpecificationExecutor<CfgStorageEnergyStrategy> {


    @Transactional
    @Query(value = "SELECT\n" +
            "\t* \n" +
            "FROM\n" +
            "\tcfg_storage_energy_strategy \n" +
            "WHERE\n" +
            "\tnode_id =:nodeId \n" +
            "\tAND system_id =:systemId \n" +
            "\tAND to_char(effective_date,'yyyy-mm')  =:ym ", nativeQuery = true)
    List<CfgStorageEnergyStrategy> findAllByNodeIdAndSystemIdAndEffectiveDate(@Param("nodeId") String nodeId
            , @Param("systemId") String systemId
            , @Param("ym") String ym);

//    @Transactional
//    @Query(value = "SELECT\n" +
//            "\t* \n" +
//            "FROM\n" +
//            "\tcfg_storage_energy_strategy \n" +
//            "WHERE\n" +
//            "\tnode_id =:nodeId \n" +
//            "\tAND system_id =:systemId \n" +
//            "\tAND effective_date >= :starDate \n" +
//            "\tAND effective_date <= :endDate", nativeQuery = true)
//    List<CfgStorageEnergyStrategy> findAllByNodeIdAndSystemIdAndEffectiveDate(@Param("nodeId") String nodeId
//            , @Param("systemId") String systemId
//            , @Param("startDate") Date startDate
//            , @Param("endDate") Date endDate);



    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_strategy where node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    void deleteAllByNodeIdAndSystemId(@Param("nodeId") String nodeId, @Param("systemId") String systemId);


    List<CfgStorageEnergyStrategy> findAllByEffectiveDate(Date date);

    List<CfgStorageEnergyStrategy> findAllByNodeIdAndEffectiveDate(String nodeId,Date date);

    CfgStorageEnergyStrategy findCfgStorageEnergyStrategyByNodeIdAndSystemIdAndEffectiveDateAndTimeFrame(String nodeId, String systemId, Date effectiveDate, String time_frame);

    @Transactional
    @Modifying
    @Query(value = "select * from cfg_storage_energy_strategy where node_id= :nodeId and system_id = :systemId and effective_date >= :starDate and effective_date <= :endDate order by effective_date,s_time", nativeQuery = true)
    List<CfgStorageEnergyStrategy> findCfgStorageEnergyStrategyByNodeIdAndSystemId(@Param("nodeId") String nodeId, @Param("systemId") String systemId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

    @Query(value = "select * from cfg_storage_energy_strategy where node_id= :nodeId and effective_date >= :starDate and effective_date <= :endDate ", nativeQuery = true)
    List<CfgStorageEnergyStrategy> findCfgStorageEnergyStrategyByNodeId(@Param("nodeId") String nodeId, @Param("starDate") Date starDate, @Param("endDate") Date endDate);

    List<CfgStorageEnergyStrategy> findAllByNodeIdAndSystemIdAndEffectiveDate(String nodeId, String systemId, Date date);

    List<CfgStorageEnergyStrategy> findAllByNodeIdAndSystemIdAndEffectiveDateOrderByTimeFrameAsc(String nodeId, String systemId, Date date);



    @Transactional
    @Modifying
    @Query(value = "delete from cfg_storage_energy_strategy where node_id= :nodeId ", nativeQuery = true)
    void deleteAllByNodeId(@Param("nodeId") String nodeId);
}