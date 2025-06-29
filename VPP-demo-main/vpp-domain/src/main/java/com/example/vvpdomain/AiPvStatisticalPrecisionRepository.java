package com.example.vvpdomain;


import com.example.vvpdomain.entity.AiPvForecastingStatisticalPrecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author zph
 * @description 光伏发电预测精度统计
 * @date 2022-07-01
 */
@Repository
public interface AiPvStatisticalPrecisionRepository extends JpaRepository<AiPvForecastingStatisticalPrecision, String>, JpaSpecificationExecutor<AiPvForecastingStatisticalPrecision> {


}