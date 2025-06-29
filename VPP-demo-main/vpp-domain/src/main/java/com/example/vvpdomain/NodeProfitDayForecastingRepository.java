package com.example.vvpdomain;

import com.example.vvpdomain.entity.NodeProfitDayForecasting;
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
public interface NodeProfitDayForecastingRepository extends JpaRepository<NodeProfitDayForecasting, String>, JpaSpecificationExecutor<NodeProfitDayForecasting> {

    List<NodeProfitDayForecasting> findAllByNodeIdAndProfitDateDayBetween(String nodeId, Date startMonth, Date endMonth);


    List<NodeProfitDayForecasting> findAllByNodeIdInAndProfitDateDayBetween(List<String> nodeIds, Date startDay, Date endDay);

}