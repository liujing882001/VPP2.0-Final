package com.example.vvpdomain;

import com.example.vvpdomain.entity.CfgPhotovoltaicTouPrice;
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
 * @description 光伏-分时电价
 * @date 2022-07-29
 */
@Repository
public interface CfgPhotovoltaicTouPriceRepository extends JpaRepository<CfgPhotovoltaicTouPrice, String>, JpaSpecificationExecutor<CfgPhotovoltaicTouPrice> {
    @Transactional
    @Modifying
    @Query(value = "delete from cfg_photovoltaic_tou_price where node_id= :nodeId and system_id = :systemId ", nativeQuery = true)
    void deleteAllByNodeIdAndSystemId(@Param("nodeId") String nodeId, @Param("systemId") String systemId);


    List<CfgPhotovoltaicTouPrice> findAllByEffectiveDate(Date date);

    @Transactional
    @Modifying
    @Query(value = "delete from cfg_photovoltaic_tou_price where node_id= :nodeId", nativeQuery = true)
    void deleteAllByNodeId(@Param("nodeId") String nodeId);

    List<CfgPhotovoltaicTouPrice> findAllByNodeIdAndSystemIdAndEffectiveDate(String nodeId, String systemId, Date date);

    @Query(value = "SELECT time_frame,price_hour FROM cfg_photovoltaic_tou_price c WHERE c.node_id = :nodeId AND c.effective_date = :date", nativeQuery = true)
    List<CfgPhotovoltaicTouPrice> findAllByNodeIdAndEffectiveDateOld(String nodeId, Date date);

    List<CfgPhotovoltaicTouPrice> findAllByNodeIdAndEffectiveDate(String nodeId, Date date);

    @Query(value = "SELECT * FROM cfg_photovoltaic_tou_price c WHERE c.node_id = :nodeId AND c.effective_date = :date", nativeQuery = true)
    List<CfgPhotovoltaicTouPrice> findAllInfoByNodeIdAndEffectiveDate(@Param("nodeId")String nodeId,@Param("date")Date date);
}