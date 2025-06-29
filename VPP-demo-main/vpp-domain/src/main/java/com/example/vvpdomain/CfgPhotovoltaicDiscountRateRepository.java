package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgPhotovoltaicDiscountRate;
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
 * @description 光伏-电力用户购电比例
 * @date 2022-07-29
 */
@Repository
public interface CfgPhotovoltaicDiscountRateRepository extends JpaRepository<CfgPhotovoltaicDiscountRate, String>, JpaSpecificationExecutor<CfgPhotovoltaicDiscountRate> {


    @Query(value = "delete from cfg_photovoltaic_discount_rate where node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteAllByNodeIdAndSystemId(@Param("nodeId") String nodeId, @Param("systemId") String systemId);

    @Query(value = "delete from cfg_photovoltaic_discount_rate where node_id= :nodeId ", nativeQuery = true)
    @Transactional
    @Modifying
    void deleteAllByNodeId(@Param("nodeId") String nodeId);

    List<CfgPhotovoltaicDiscountRate> findAllByOrderBetween(int st,int et);

    CfgPhotovoltaicDiscountRate findByNodeIdAndSystemIdAndOrder(String nodeId, String systemId, Integer order);

	List<CfgPhotovoltaicDiscountRate> findAllByNodeIdAndOrderBetween(String nodeId, Integer st, Integer et);
}