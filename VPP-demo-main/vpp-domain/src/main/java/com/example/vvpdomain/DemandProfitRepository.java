package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandProfit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author maoyating
 * @description 需求响应收益信息
 * @date 2024-03-19
 */
@Repository
public interface DemandProfitRepository extends JpaRepository<DemandProfit, String>,
        JpaSpecificationExecutor<DemandProfit> {

    /**
     * 根据日期范围、节点id去查询每天的总收益
     * @return
     */
    @Query(value = "SELECT node_id, node_name,CAST((profits) AS TEXT) AS profits,total " +
            "FROM (" +
            "SELECT dp.node_id,n.node_name, json_agg(jsonb_build_object('profitDate', profit_date, 'totalProfit', total_profit)) AS profits,sum(dp.total_profit) as total " +
            " FROM demand_profit dp,node n where dp.profit_date >=:startDate and dp.profit_date <=:endDate and dp.node_id in (:nodeIds) and n.node_id=dp.node_id " +
            " GROUP BY dp.node_id,n.node_name order by n.node_name asc LIMIT :pageSize OFFSET :pageNum ) AS dp;", nativeQuery = true)
    List<Object[]> findByRsDateNodeIds(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                       @Param("nodeIds") Collection<String> nodeIds,
                                       @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据日期范围、节点id去查询每天的总收益--导出全部
     * @return
     */
    @Query(value = "select dp.node_id,n.node_name, dp.profit_date,dp.total_profit as profits from demand_profit dp,node n " +
            " where dp.profit_date>=:startDate and dp.profit_date<=:endDate and dp.node_id in (:nodeIds) " +
            " and n.node_id = dp.node_id ORDER BY profit_date asc", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsExport(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                       @Param("nodeIds") Collection<String> nodeIds);

    /**
     * 根据日期范围、节点id去查询每天的总收益  总条数
     * @return
     */
    @Query(value = "SELECT count(distinct dp.node_id)  " +
            " FROM demand_profit dp where dp.profit_date >=:startDate and dp.profit_date <=:endDate and dp.node_id in (:nodeIds) ", nativeQuery = true)
    Integer countByRsDateNodeIds(@Param("startDate") Date startDate, @Param("endDate") Date endDate, @Param("nodeIds") Collection<String> nodeIds);


    /**
     * 根据日期范围、节点id去查询每天的总收益（总收益的系数）
     * @return
     */
    @Query(value = "SELECT node_id, node_name,CAST((profits) AS TEXT) AS profits,total " +
            "FROM (" +
            "SELECT dp.node_id,n.node_name, json_agg(jsonb_build_object('profitDate', profit_date, 'totalProfit', total_profit * :ratio)) AS profits,sum(dp.total_profit) * :ratio as total " +
            " FROM demand_profit dp,node n where dp.profit_date >=:startDate and dp.profit_date <=:endDate and dp.node_id in (:nodeIds) and n.node_id=dp.node_id " +
            " GROUP BY dp.node_id,n.node_name order by n.node_name asc LIMIT :pageSize OFFSET :pageNum ) AS dp", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsRatio(@Param("startDate") Date startDate, @Param("endDate") Date endDate,
                                            @Param("nodeIds") Collection<String> nodeIds,
                                            @Param("ratio") double ratio,@Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);


    /**
     * 根据日期范围、节点id去查询每月的总收益
     * @return
     */
    @Query(value = "SELECT node_id, node_name," +
            "       CAST((profits) AS TEXT) AS profits,total" +
            " FROM (" +
            "    SELECT dp.node_id, n.node_name, json_agg(jsonb_build_object('profitDate', profit_year_month_str, 'totalProfit', total_profit)) AS profits, sum(dp.total_profit) as total " +
            "    FROM " +
            " (select dp.node_id, dp.profit_year_month,dp.profit_year_month_str, sum(dp.total_profit) as total_profit from demand_profit dp where dp.profit_year_month>=:startDate and dp.profit_year_month<=:endDate and dp.node_id in (:nodeIds)  group by dp.node_id,dp.profit_year_month,dp.profit_year_month_str ) " +
            " dp, node n " +
            "    WHERE  " +
            "  n.node_id = dp.node_id " +
            "    GROUP BY dp.node_id, n.node_name " +
            "    ORDER BY n.node_name ASC LIMIT :pageSize OFFSET :pageNum) AS dp", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsMonth(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                       @Param("nodeIds") Collection<String> nodeIds,
                                       @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据日期范围、节点id去查询每月的总收益--导出全部
     * @return
     */
    @Query(value = "select dp.node_id,n.node_name, dp.profit_year_month,dp.profit_year_month_str, " +
            "sum(dp.total_profit) as profits from demand_profit dp,node n " +
            "where dp.profit_year_month>=:startDate and dp.profit_year_month<=:endDate and dp.node_id in (:nodeIds) " +
            "and n.node_id = dp.node_id  group by dp.node_id,n.node_name,dp.profit_year_month,dp.profit_year_month_str " +
            "ORDER BY profit_year_month asc", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsMonthExport(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                            @Param("nodeIds") Collection<String> nodeIds);

    /**
     * 根据日期范围、节点id去查询每月的总收益（总收益的系数）
     * @return
     */
    @Query(value = "SELECT node_id, node_name," +
            "       CAST((profits) AS TEXT) AS profits,total" +
            " FROM (" +
            "    SELECT dp.node_id, n.node_name, json_agg(jsonb_build_object('profitDate', profit_year_month_str, 'totalProfit', total_profit * :ratio)) AS profits, sum(dp.total_profit) * :ratio as total " +
            "    FROM " +
            " (select dp.node_id, dp.profit_year_month,dp.profit_year_month_str, sum(dp.total_profit) as total_profit from demand_profit dp where dp.profit_year_month>=:startDate and dp.profit_year_month<=:endDate and dp.node_id in (:nodeIds)  group by dp.node_id,dp.profit_year_month,dp.profit_year_month_str ) " +
            " dp, node n " +
            "    WHERE  " +
            "  n.node_id = dp.node_id " +
            "    GROUP BY dp.node_id, n.node_name " +
            "    ORDER BY n.node_name ASC LIMIT :pageSize OFFSET :pageNum) AS dp", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsRatioMonth(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                            @Param("nodeIds") Collection<String> nodeIds, @Param("ratio") double ratio,
                                            @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据日期范围、节点id去查询每月的总收益  总条数
     * @return
     */
    @Query(value = "SELECT count(distinct dp.node_id)  " +
            " FROM demand_profit dp where dp.profit_year_month >=:startDate and dp.profit_year_month <=:endDate and dp.node_id in (:nodeIds) ", nativeQuery = true)
    Integer countByRsDateNodeIdsMonth(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate, @Param("nodeIds") Collection<String> nodeIds);


    /**
     * 根据日期范围、节点id去查询每年的总收益
     * @return
     */
    @Query(value = "SELECT node_id, node_name," +
            "       CAST((profits) AS TEXT) AS profits,total FROM (" +
            "    SELECT dp.node_id, n.node_name, json_agg(jsonb_build_object('profitDate', profit_year, 'totalProfit', total_profit)) AS profits, sum(dp.total_profit) as total " +
            "    FROM " +
            " (select dp.node_id, dp.profit_year, sum(dp.total_profit) as total_profit from demand_profit dp where dp.profit_year>=:startDate and dp.profit_year<=:endDate  and dp.node_id in (:nodeIds)  group by dp.node_id,dp.profit_year ) " +
            " dp, node n " +
            "    WHERE " +
            "    n.node_id = dp.node_id " +
            "    GROUP BY dp.node_id, n.node_name " +
            "    ORDER BY n.node_name ASC  " +
            "    LIMIT :pageSize OFFSET :pageNum " +
            ") AS dp ", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsYear(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                            @Param("nodeIds") Collection<String> nodeIds,
                                            @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据日期范围、节点id去查询每年的总收益--导出全部
     * @return
     */
    @Query(value = "select dp.node_id,n.node_name, dp.profit_year, sum(dp.total_profit) as total " +
            "from demand_profit dp,node n where dp.profit_year>=:startDate and dp.profit_year<=:endDate and dp.node_id in (:nodeIds) " +
            "and n.node_id = dp.node_id  group by dp.node_id,n.node_name,dp.profit_year " +
            "ORDER BY profit_year asc", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsYearExport(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                           @Param("nodeIds") Collection<String> nodeIds);

    /**
     * 根据日期范围、节点id去查询每年的总收益（总收益的系数）
     * @return
     */
    @Query(value = "SELECT node_id, node_name," +
            "       CAST((profits) AS TEXT) AS profits,total FROM (" +
            "    SELECT dp.node_id, n.node_name, json_agg(jsonb_build_object('profitDate', profit_year, 'totalProfit', total_profit * :ratio)) AS profits, sum(dp.total_profit) * :ratio as total " +
            "    FROM " +
            " (select dp.node_id, dp.profit_year, sum(dp.total_profit) as total_profit from demand_profit dp where dp.profit_year>=:startDate and dp.profit_year<=:endDate  and dp.node_id in (:nodeIds)  group by dp.node_id,dp.profit_year ) " +
            " dp, node n " +
            "    WHERE " +
            "    n.node_id = dp.node_id " +
            "    GROUP BY dp.node_id, n.node_name " +
            "    ORDER BY n.node_name ASC  " +
            "    LIMIT :pageSize OFFSET :pageNum " +
            ") AS dp ", nativeQuery = true)
    List<Object[]> findByRsDateNodeIdsRatioYear(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate,
                                                 @Param("nodeIds") Collection<String> nodeIds, @Param("ratio") double ratio,
                                                 @Param("pageSize") Integer pageSize, @Param("pageNum") Integer pageNum);

    /**
     * 根据日期范围、节点id去查询每年的总收益  总条数
     * @return
     */
    @Query(value = "SELECT count(distinct dp.node_id)  " +
            " FROM demand_profit dp where dp.profit_year >=:startDate and dp.profit_year <=:endDate and dp.node_id in (:nodeIds) ", nativeQuery = true)
    Integer countByRsDateNodeIdsYear(@Param("startDate") Integer startDate, @Param("endDate") Integer endDate, @Param("nodeIds") Collection<String> nodeIds);

}