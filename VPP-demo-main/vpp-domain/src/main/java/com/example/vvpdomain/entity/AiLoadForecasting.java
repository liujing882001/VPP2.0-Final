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

    // 手动添加缺失的getter方法以确保编译通过
    public LocalDateTime getCountDataTime() {
        return countDataTime;
    }

    public String getPredictValue() {
        return predictValue;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }
    public BigDecimal getRealValue() { return realValue; }
    public void setRealValue(BigDecimal realValue) { this.realValue = realValue; }
    public BigDecimal getUltraShortTermForecastValue() { return ultraShortTermForecastValue; }
    public void setUltraShortTermForecastValue(BigDecimal ultraShortTermForecastValue) { this.ultraShortTermForecastValue = ultraShortTermForecastValue; }
    public BigDecimal getCurrentForecastValue() { return currentForecastValue; }
    public void setCurrentForecastValue(BigDecimal currentForecastValue) { this.currentForecastValue = currentForecastValue; }
    public BigDecimal getBaselineLoadValue() { return baselineLoadValue; }
    public void setBaselineLoadValue(BigDecimal baselineLoadValue) { this.baselineLoadValue = baselineLoadValue; }
    public void setCountDataTime(LocalDateTime countDataTime) { this.countDataTime = countDataTime; }
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
    public void setUpdateTime(LocalDateTime updateTime) { this.updateTime = updateTime; }
    public String getBaselineLoadValueOther() { return baselineLoadValueOther; }
    public void setBaselineLoadValueOther(String baselineLoadValueOther) { this.baselineLoadValueOther = baselineLoadValueOther; }
    public void setPredictValue(String predictValue) { this.predictValue = predictValue; }
    public String getPredictAdjustableAmount() { return predictAdjustableAmount; }
    public void setPredictAdjustableAmount(String predictAdjustableAmount) { this.predictAdjustableAmount = predictAdjustableAmount; }
}