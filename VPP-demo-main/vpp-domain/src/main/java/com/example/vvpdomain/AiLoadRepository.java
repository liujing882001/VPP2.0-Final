package com.example.vvpdomain;

import com.example.vvpdomain.entity.AiLoadForecasting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description 可调负荷预测
 * @date 2022-07-01
 */
@Repository
public interface AiLoadRepository extends JpaRepository<AiLoadForecasting, String>, JpaSpecificationExecutor<AiLoadForecasting> {

    @Query(value = " select id,node_id,system_id,real_value,ultra_short_term_forecast_value," +
            "current_forecast_value,baseline_load_value,count_data_time,created_time," +
            "update_time,baseline_load_value_other,predict_value,predict_adjustable_amount from (" +
            "select *,row_number() over (partition by node_id order by count_data_time desc nulls last) max_num from ai_load_forecasting" +
            " where system_id = 'nengyuanzongbiao' and node_id in (:nodeIds) and count_data_time <= now()" +
            ") as alf where alf.max_num = 1 ", nativeQuery = true)
    List<AiLoadForecasting> findByNodeIdsMax(@Param("nodeIds") Collection<String> nodeIds);

    @Query(value = "SELECT * FROM ai_load_forecasting WHERE count_data_time>=:dt and system_id=:systemId and node_id=:nodeId  order by count_data_time ", nativeQuery = true)
    List<AiLoadForecasting> findAllByNodeIdAndSystemIdAndCountDataTime(@Param("nodeId") String nodeId,
                                                                       @Param("systemId") String systemId,
                                                                       @Param("dt") Date dt);

    @Query(value = "select\n" +
            "ailoadfore0_.predict_value as predict_9_0_," +
            "ailoadfore0_.count_data_time as count_da4_0_" +
            "    from\n" +
            "        ai_load_forecasting ailoadfore0_ \n" +
            "    where\n" +
            "        ailoadfore0_.node_id= :nodeId" +
            "        and ailoadfore0_.system_id= :systemId" +
            "        and (" +
            "            ailoadfore0_.count_data_time between :st and :et" +
            "        )", nativeQuery = true
    )
    List<Object[]> findAllByNodeIdAndSystemIdAndCountDataTimeBetween(@Param("nodeId") String nodeId, @Param("systemId") String systemId,
                                                                              @Param("st") Date st, @Param("et") Date et);
    @Query(value = "select\n" +
            "ailoadfore0_.predict_value as predict_9_0_," +
            "ailoadfore0_.count_data_time as count_da4_0_" +
            "    from\n" +
            "        ai_load_forecasting ailoadfore0_ \n" +
            "    where\n" +
            "        ailoadfore0_.node_id= :nodeId" +
            "        and (" +
            "            ailoadfore0_.count_data_time between :st and :et" +
            "        )", nativeQuery = true
    )
    List<Object[]> findAllByNodeIdAndSystemIdAndCountDataTimeBetween(@Param("nodeId") String nodeId,
                                                                     @Param("st") Date st, @Param("et") Date et);
    @Query(value = "SELECT\n" +
            "\tto_char( count_data_time, 'YYYY/MM/DD' ) AS count_data \n" +
            "FROM\n" +
            "\tai_load_forecasting \n" +
            "WHERE\n" +
            "\tcount_data_time >=:dt \n" +
            "\tAND system_id =:systemId \n" +
            "\tAND node_id =:nodeId \n" +
            "GROUP BY\n" +
            "\tto_char( count_data_time, 'YYYY/MM/DD' ) \n" +
            "ORDER BY\n" +
            "\tto_char( count_data_time, 'YYYY/MM/DD' ) ", nativeQuery = true)
    List<String> findCountDateGroupByCountDataTime(@Param("nodeId") String nodeId,
                                                   @Param("systemId") String systemId,
                                                   @Param("dt") Date dt);

    /**
     * 查询‘nengyuanzongbiao’的固定日期 的数据信息
     * @param countDataTimes
     * @return
     */
    @Query(value = "SELECT * FROM ai_load_forecasting WHERE system_id = 'nengyuanzongbiao' " +
            " and to_char(count_data_time,'yyyy-mm-dd') in (:countDataTimes) and real_value is not null and real_value != '-' ", nativeQuery = true)
    List<AiLoadForecasting> findAllByCountDataTimes(@Param("countDataTimes") Collection<String> countDataTimes);

    /**
     * 查询‘nengyuanzongbiao’的固定日期 的数据信息
     * @param countDataTimes
     * @return
     */
    @Query(value = "SELECT * FROM ai_load_forecasting WHERE system_id = 'nengyuanzongbiao' " +
            " and to_char(count_data_time,'yyyy-mm-dd') in (:countDataTimes) and real_value is not null and real_value != '-' " +
            " and node_id in (:nodeIds)", nativeQuery = true)
    List<AiLoadForecasting> findAllByCountDataTimesAndNodes(@Param("countDataTimes") Collection<String> countDataTimes,
                                                            @Param("nodeIds") Collection<String> nodeIds);

    /**
     * 根据系统id、节点、统计时间查询相关数据
     * @param nodeIds
     * @param systemId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "select * from ai_load_forecasting where system_id =:systemId and node_id in (:nodeIds) " +
            " and count_data_time >= :startDate and count_data_time < :endDate", nativeQuery = true)
    List<AiLoadForecasting> findByDateNodeIdsSystemId(@Param("nodeIds") Collection<String> nodeIds,
                                                      @Param("systemId") String systemId,
                                                      @Param("startDate") Date startDate,
                                                      @Param("endDate") Date endDate);
    /**
     * 根据系统id、节点、统计时间查询相关数据
     * @param nodeId
     * @param systemId
     * @param startDate
     * @param endDate
     * @return
     */
    @Query(value = "select * from ai_load_forecasting where system_id =:systemId and node_id = :nodeId " +
            " and count_data_time >= :startDate and count_data_time < :endDate order by count_data_time desc", nativeQuery = true)
    List<AiLoadForecasting> findByDateNodeIdSystemId(@Param("nodeId") String nodeId,
                                                     @Param("systemId") String systemId,
                                                     @Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate);

    @Query(value = "select predict_value,count_data_time " +
            "from ai_load_forecasting " +
            "where system_id =:systemId " +
            "and node_id = :nodeId " +
            " and count_data_time >= :startDate " +
            "and count_data_time < :endDate " +
            "order by count_data_time desc", nativeQuery = true)
    List<Object[]> findPredictValueAndTimeByDateNodeIdSystemId(@Param("nodeId") String nodeId,
                                                     @Param("systemId") String systemId,
                                                     @Param("startDate") Date startDate,
                                                     @Param("endDate") Date endDate);

    @Query(value = "select node_id,predict_value,count_data_time " +
            "from ai_load_forecasting " +
            "where node_id in (:nodeIds) " +
            "and count_data_time >= :startDate " +
            "and count_data_time < :endDate " , nativeQuery = true)
    List<Object[]> findPredictValueAndTimeByDateNodeIds(@Param("nodeIds") List<String> nodeIds,
                                                               @Param("startDate") Date startDate,
                                                               @Param("endDate") Date endDate);


    @Query(value = "SELECT\n" +
            "\t( SUM ( to_number( REPLACE ( real_value, '-', '0' ), '999999999999.9999' ) ) ) AS actualValue,\n" +
            "\t( SUM ( to_number( REPLACE ( ultra_short_term_forecast_value, '-', '0' ), '999999999999.9999' ) ) ) AS predictedValue,\n" +
            "\tcount_data_time AS countDataTime \n" +
            "FROM\n" +
            "\tai_load_forecasting \n" +
            "WHERE\n" +
            "\tsystem_id = 'nengyuanzongbiao' and count_data_time >= now() - interval '5' hour  \n" +
            "GROUP BY\n" +
            "\tcount_data_time \n" +
            "ORDER BY\n" +
            "\tcount_data_time ASC", nativeQuery = true)
    List<Object[]> findElectricityLoadCurve();

    @Query(value = "select * from ai_load_forecasting where system_id =:systemId and node_id in (:nodeIds) " +
            " and count_data_time >= :startDate and count_data_time <= :endDate", nativeQuery = true)
    List<AiLoadForecasting> findPredictValueByCountDataTime(@Param("nodeIds") String nodeIds,
                                                            @Param("systemId") String systemId,
                                                            @Param("startDate") Date startDate,
                                                            @Param("endDate") Date endDate);

    List<AiLoadForecasting> findAllByNodeId(String nodeId);
}