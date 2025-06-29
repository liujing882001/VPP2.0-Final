package com.example.vvpdomain;

import com.example.vvpdomain.entity.AnalysisEnergyMonth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description analysis_energy_month
 * @date 2022-07-05
 */
@Repository
public interface AnalysisEnergyMonthRepository extends JpaRepository<AnalysisEnergyMonth, String>, JpaSpecificationExecutor<AnalysisEnergyMonth> {


    @Query(value = "SELECT * from analysis_energy_month_view where ts BETWEEN  :startTime and :endTime and node_id =:nodeId and system_id=:systemId ", nativeQuery = true)
    List<AnalysisEnergyMonth> findAllByNodeIdAndSystemIdAndTsBetween(@Param("nodeId") String nodeId,
                                                                     @Param("systemId") String systemId,
                                                                     @Param("startTime") String startTime,
                                                                     @Param("endTime") String endTime);

}