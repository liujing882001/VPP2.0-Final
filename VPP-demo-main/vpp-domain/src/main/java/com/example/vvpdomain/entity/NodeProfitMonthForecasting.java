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

}