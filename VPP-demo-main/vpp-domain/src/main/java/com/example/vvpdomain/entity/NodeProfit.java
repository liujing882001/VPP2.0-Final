package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "node_profit")
public class NodeProfit implements Serializable {

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
     * 收益值
     */
    @Column(name = "profit_date")
    private Date profitDate;


    //@ApiModelProperty("尖电价")
    @Column(name = "price_high")
    private double priceHigh;

    //@ApiModelProperty("峰电价")
    @Column(name = "price_peak")
    private double pricePeak ;

    //@ApiModelProperty("平电价")
    @Column(name = "price_stable")
    private double priceStable;

    //@ApiModelProperty("谷电价")
    @Column(name = "price_low")
    private double priceLow ;

//    @Column(name = "price_ravine")
//    private double priceRavine;

    //@ApiModelProperty("充尖本月电量")
    @Column(name = "in_electricity_high")
    private double inElectricityHigh ;

    //@ApiModelProperty("充峰本月电量")
    @Column(name = "in_electricity_peak")
    private double inElectricityPeak ;

    //@ApiModelProperty("充平本月电量")
    @Column(name = "in_electricity_stable")
    private double inElectricityStable ;

    //@ApiModelProperty("充谷本月电量")
    @Column(name = "in_electricity_low")
    private double inElectricityLow ;

    //@ApiModelProperty("充深谷本月电量")
//    @Column(name = "in_electricity_ravine")
//    private double inElectricityRavine;

    //@ApiModelProperty("放尖本月电量")
    @Column(name = "out_electricity_high")
    private double outElectricityHigh ;

    //@ApiModelProperty("放峰本月电量")
    @Column(name = "out_electricity_peak")
    private double outElectricityPeak ;

    //@ApiModelProperty("放平本月电量")
    @Column(name = "out_electricity_stable")
    private double outElectricityStable ;

    //@ApiModelProperty("放谷本月电量")
    @Column(name = "out_electricity_low")
    private double outElectricityLow ;

    //@ApiModelProperty("深谷平本月电量")
//    @Column(name = "out_electricity_ravine")
//    private double outElectricityRavine;


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


    public NodeProfit() {
    }

    public NodeProfit(String profitId, String nodeId, Date profitDate) {
        this.profitId = profitId;
        this.nodeId = nodeId;
        this.profitDate = profitDate;
        this.profitValue = BigDecimal.ZERO.doubleValue();

    }

}