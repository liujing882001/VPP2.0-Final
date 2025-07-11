package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "node_profit_month_forecasting")
public class NodeProfitMonthForecasting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "profit_id")
    private String profitId;

    /**
     * node_id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 收益值
     */
    @Column(name = "profit_value")
    private double profitValue;

    /**
     * 收益值月
     */
    @Column(name = "profit_forecast_value")
    private double profitForecastValue;

    /**
     * 收益月份
     */
    @Column(name = "profit_date_month")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date profitDateMonth;


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


    public NodeProfitMonthForecasting() {
    }

    public String getProfitId() { return profitId; }
    public void setProfitId(String profitId) { this.profitId = profitId; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public double getProfitValue() { return profitValue; }
    public void setProfitValue(double profitValue) { this.profitValue = profitValue; }
    public double getProfitForecastValue() { return profitForecastValue; }
    public void setProfitForecastValue(double profitForecastValue) { this.profitForecastValue = profitForecastValue; }
    public Date getProfitDateMonth() { return profitDateMonth; }
    public void setProfitDateMonth(Date profitDateMonth) { this.profitDateMonth = profitDateMonth; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}