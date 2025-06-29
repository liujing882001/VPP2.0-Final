package com.example.vvpdomain;


import com.example.vvpdomain.entity.AiLoadForecastingStatisticalPrecision;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * @author zph
 * @description 负荷预测精度统计
 * @date 2022-07-01
 */
@Repository
public interface AiLoadStatisticalPrecisionRepository extends JpaRepository<AiLoadForecastingStatisticalPrecision, String>, JpaSpecificationExecutor<AiLoadForecastingStatisticalPrecision> {


}