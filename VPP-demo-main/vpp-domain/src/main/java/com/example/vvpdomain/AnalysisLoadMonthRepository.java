package com.example.vvpdomain;

import com.example.vvpdomain.entity.AnalysisLoadMonth;
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
 * @description analysis_load_month
 * @date 2022-07-05
 */
@Repository
public interface AnalysisLoadMonthRepository extends JpaRepository<AnalysisLoadMonth, String>, JpaSpecificationExecutor<AnalysisLoadMonth> {


    @Query(value = "SELECT sum(cast(load_value as float8)),a.ts from analysis_load_month_view a WHERE a.ts BETWEEN :ts_s and :ts_e and a.node_id in (:nodeIds) AND system_id <> 'nengyuanzongbiao' group by a.ts", nativeQuery = true)
    List<Object[]> findAllAutoMonthList(@Param("nodeIds") Collection<String> nodeIds, @Param("ts_s") Date ts_s, @Param("ts_e") Date ts_e);

}