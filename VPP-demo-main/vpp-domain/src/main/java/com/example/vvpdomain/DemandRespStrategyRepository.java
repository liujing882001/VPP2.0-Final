package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandRespStrategy;
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
 * @author maoyating
 * @description 需求响应策略
 * @date 2022-08-09
 */
@Repository
public interface DemandRespStrategyRepository extends JpaRepository<DemandRespStrategy, String>,
        JpaSpecificationExecutor<DemandRespStrategy> {

    @Query(value = "delete from demand_resp_strategy where resp_id= :respId ", nativeQuery = true)
    @Modifying
    @Transactional
    void deleteRespId(@Param("respId") String respId);

    @Query(value = " select s_id from demand_resp_strategy where resp_id= :respId ", nativeQuery = true)
    List<String> findSIds(@Param("respId") String respId);

    @Query(value = " select DISTINCT strategy_id from demand_resp_strategy where resp_id= :respId ", nativeQuery = true)
    List<String> findStrategyIds(@Param("respId") String respId);
    @Query(value = " select DISTINCT strategy_id from demand_resp_strategy where resp_id= :respId AND s_id in (:sId) ", nativeQuery = true)
    List<String> findStrategyIdsBySId(@Param("respId") String respId,@Param("sId") List<String> sId);
    @Query(value = " select s_id from demand_resp_strategy where resp_id= :respId and create_by =:userId", nativeQuery = true)
    List<String> findSIdsByUserId(@Param("respId") String respId, @Param("userId") String userId);

    @Query(value = "SELECT\n" +
            "\tdrt.resp_id \n" +
            "FROM\n" +
            "\tdemand_resp_task drt,\n" +
            "\tdemand_resp_strategy drs \n" +
            "WHERE\n" +
            "\tdrt.resp_id = drs.resp_id \n" +
            "\tAND drt.d_status in (1,2) \n" +
            "\tAND drs.strategy_id in (:strategyIds)", nativeQuery = true)
    List<String> findBeAssociatedWithStrategyIds(@Param("strategyIds") Collection<String> strategyIds);

    @Query(value = " select s_id from demand_resp_strategy where resp_id= :respId and strategy_id=:platformId", nativeQuery = true)
    List<String> findSIdsByPlatformId(@Param("respId") String respId, @Param("platformId") String platformId);

    @Query(value = "insert into demand_resp_strategy values (:sId,:respId,:strategyId,:createBy)", nativeQuery = true)
    @Transactional
    @Modifying
    void insert(@Param("sId") String sId, @Param("respId") String respId,
                @Param("strategyId") String strategyId, @Param("createBy") String createBy);
}