package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * AI Load Forecasting Entity
 * Represents AI-based load forecasting data for adjustable loads
 *
 * @author zph
 * @since 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ai_load_forecasting")
public class AiLoadForecasting implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id")
    private String id;

    /**
     * Node identifier
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * System identifier
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * Actual load value
     */
    @Column(name = "real_value", precision = 20, scale = 2)
    private BigDecimal realValue;

    /**
     * Ultra-short term AI forecast value
     */
    @Column(name = "ultra_short_term_forecast_value", precision = 20, scale = 2)
    private BigDecimal ultraShortTermForecastValue;

    /**
     * Current AI forecast value
     */
    @Column(name = "current_forecast_value", precision = 20, scale = 2)
    private BigDecimal currentForecastValue;

    /**
     * Baseline load value from AI
     */
    @Column(name = "baseline_load_value", precision = 20, scale = 2)
    private BigDecimal baselineLoadValue;

    /**
     * Data collection timestamp
     */
    @Column(name = "count_data_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime countDataTime;

    /**
     * Record creation timestamp
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdTime;

    /**
     * Record last update timestamp
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * AI基线负荷值（商汤外，自行预测值）
     */
    @Column(name = "baseline_load_value_other")
    private String baselineLoadValueOther;

    /**
     * 预测负荷
     */
    @Column(name = "predict_value")
    private String predictValue;

    /**
     * 预测可调节负荷
     */
    @Column(name = "predict_adjustable_amount")
    private String predictAdjustableAmount;

    /**
     * Default constructor
     */
    public AiLoadForecasting() {
    }
}