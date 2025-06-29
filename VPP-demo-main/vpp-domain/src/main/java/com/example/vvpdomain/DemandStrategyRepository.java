package com.example.vvpdomain;


import com.example.vvpdomain.entity.DemandStrategy;
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
 * @author konghao
 * @description 策略
 * @date 2024-03-07
 */
@Repository
public interface DemandStrategyRepository extends JpaRepository<DemandStrategy, String>, JpaSpecificationExecutor<DemandStrategy> {


    /**
     * 根据系统id、节点、统计时间查询相关数据
     * @param respId
     * @return
     */
    @Query(value = "select * from demand_strategy where resp_id = :respId and (state in :states) ", nativeQuery = true)
    List<DemandStrategy> findByRespIdAndState(@Param("respId") String respId,@Param("states") List<Integer> states);
    @Query(value = "select * from demand_strategy where resp_id = :respId " +
            " and node_id = :nodeId" +
            " and state = :state ORDER BY forecast_time desc ", nativeQuery = true)
    List<DemandStrategy> findByRespIdAndNodeIdAndState(@Param("respId") String respId,
                                                       @Param("nodeId") String nodeId,
                                                       @Param("state") Integer state);
    List<DemandStrategy> findByRespId(@Param("respId") String respId);

    /**
     * 批量确认策略
     */
    @Transactional
    @Modifying
    @Query("UPDATE DemandStrategy e SET e.ensure = 1 WHERE e.id IN :ids")
    void updateEnsureByIds(@Param("ids") List<String> ids);

    List<DemandStrategy> findByRespIdAndNodeId(@Param("respId") String respId, @Param("nodeId") String nodeId);

//    /**
//     * 修改策略内容
//     */
//    @Query(value = "UPDATE demand_strategy " +
//            "SET strategyContent = :strategyContent " +
//            "WHERE id =:id", nativeQuery = true)
//    @Modifying
//    @Transactional
//    void updatePrice(@Param("id") String id, @Param("strategyContent") String strategyContent);

    @Query(value = "delete from demand_strategy where resp_id= :respId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteDemandStrategy(@Param("respId") String respId);

    @Transactional
    @Modifying
    @Query(value = "update demand_strategy set state=2 where resp_id= :respId ", nativeQuery = true)
    void updateStateByIds(@Param("respId") String respId);

}
