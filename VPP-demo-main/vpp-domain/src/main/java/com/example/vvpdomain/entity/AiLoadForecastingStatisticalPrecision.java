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
 * @description 负荷预测精度统计
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ai_load_forecasting_statistical_precision")
public class AiLoadForecastingStatisticalPrecision {
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
     * 超短期预测准确率
     */
    @Column(name = "ultra_short_term_forecast")
    private String ultraShortTermForecast;

    /**
     * 目前预测准确率
     */
    @Column(name = "current_forecast")
    private String currentForecast;


    /**
     * 统计日期（年-月-日）
     */
    @Column(name = "count_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date countDate;


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