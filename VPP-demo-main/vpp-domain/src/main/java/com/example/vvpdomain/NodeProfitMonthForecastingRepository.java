package com.example.vvpdomain;

import com.example.vvpdomain.entity.NodeProfit;
import com.example.vvpdomain.entity.NodeProfitMonthForecasting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


/**
 * @author zph
 * @description node
 * @date 2022-07-01
 */
@Repository
public interface NodeProfitMonthForecastingRepository extends JpaRepository<NodeProfitMonthForecasting, String>, JpaSpecificationExecutor<NodeProfitMonthForecasting> {

    List<NodeProfitMonthForecasting> findAllByNodeIdAndProfitDateMonthBetween(String nodeId, Date startMonth, Date endMonth);

    @Query(value = "SELECT node_id FROM node_profit_month_forecasting GROUP BY node_id",nativeQuery = true)
    List<String> findAllGroupByNodeId();

    @Query(value = "SELECT * FROM node_profit_month_forecasting WHERE node_id=:nodeId and  profit_date_month <=:endMonth ORDER BY profit_date_month ",nativeQuery = true)
    List<NodeProfitMonthForecasting> findAllByNodeId(@Param("nodeId")String nodeId,@Param("endMonth") Date endMonth);

    List<NodeProfitMonthForecasting> findAllByNodeIdInAndProfitDateMonth(List<String> nodeIds,Date profitDateMonth);

    List<NodeProfitMonthForecasting> findAllByNodeIdInAndProfitDateMonthBetween(List<String> nodeIds, Date profitDateMonthStart, Date profitDateMonthEnd);

}