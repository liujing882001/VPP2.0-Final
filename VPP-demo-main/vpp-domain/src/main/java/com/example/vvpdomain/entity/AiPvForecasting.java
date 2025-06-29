package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @author zph
 * @description 光伏发电预测
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ai_pv_forecasting")
public class AiPvForecasting {
    @Id
    @Column(name = "id")
    private String id;
    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;
    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 功率
     */
    @Column(name = "real_value")
    private String realValue;


    /**
     * AI超短期预测值
     */
    @Column(name = "ultra_short_term_forecast_value")
    private String ultraShortTermForecastValue;

    /**
     * AI目前预测值
     */
    @Column(name = "current_forecast_value")
    private String currentForecastValue;

    /**
     * AI中期预测值
     */
    @Column(name = "medium_term_forecast_value")
    private String mediumTermForecastValue;


    /**
     * yyy-mm-dd hh:mm:ss
     */
    @Column(name = "count_data_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date countDataTime;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}