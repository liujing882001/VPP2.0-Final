package com.example.vvpdomain;

import com.example.vvpdomain.entity.AiPvForecasting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 光伏发电预测
 * @date 2022-07-01
 */
@Repository
public interface AiPvRepository extends JpaRepository<AiPvForecasting, String>, JpaSpecificationExecutor<AiPvForecasting> {


    @Query(value = "SELECT\n" +
            "\t( SUM ( to_number( REPLACE ( real_value, '-', '0' ), '999999999999.9999' ) ) ) AS actualValue,\n" +
            "\t( SUM ( to_number( REPLACE ( ultra_short_term_forecast_value, '-', '0' ), '999999999999.9999' ) ) ) AS predictedValue,\n" +
            "\tcount_data_time as countDataTime\n" +
            "FROM\n" +
            "\tai_pv_forecasting \n" +
            "WHERE\n" +
            "\tsystem_id = 'nengyuanzongbiao' and count_data_time >= now() - interval '5' hour  \n" +
            "GROUP BY\n" +
            "\tcount_data_time \n" +
            "ORDER BY\n" +
            "\tcount_data_time ASC", nativeQuery = true)
    List<Object[]> findGenerationLoadCurve();
}