package com.example.vvpdomain;

import com.example.vvpdomain.entity.AnalysisLoadDay;
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
 * @description analysis_load_day
 * @date 2022-07-05
 */
@Repository
public interface AnalysisLoadDayRepository extends JpaRepository<AnalysisLoadDay, String>, JpaSpecificationExecutor<AnalysisLoadDay> {

    @Query(value = "SELECT sum(cast(load_value as float8)),a.ts from analysis_load_day_view a WHERE a.ts >=:ts and a.ts <= to_char(now(), 'yyyy-mm-dd HH24:MI:ss') and a.node_id in (:nodeIds) AND system_id <> 'nengyuanzongbiao' group by a.ts", nativeQuery = true)
    List<Object[]> findAllNearlyADayList(@Param("ts") Date ts, @Param("nodeIds") Collection<String> nodeIds);

    @Query(value = "SELECT SUM\n" +
            "\t( CAST ( load_value AS float8 ) )\n" +
            "FROM\n" +
            "\tanalysis_load_day_view A \n" +
            "WHERE\n" +
            "\tA.ts >=:ts \n" +
            "\tAND A.ts <= to_char(now(), 'yyyy-mm-dd HH24:MI:ss') \n" +
            "\tAND A.node_id IN ( :nodeIds ) \n" +
            "\tAND system_id <> 'nengyuanzongbiao' \n" +
            "GROUP BY\n" +
            "\tA.ts \n" +
            "ORDER BY\n" +
            "\tA.ts DESC \n" +
            "\tLIMIT 1", nativeQuery = true)
    double findNowLoad(@Param("ts") Date ts, @Param("nodeIds") Collection<String> nodeIds);

    List<AnalysisLoadDay> findAllByNodeIdAndSystemIdAndTsBetween(String nodeId, String systemId, Date startTime, Date endTime);
}