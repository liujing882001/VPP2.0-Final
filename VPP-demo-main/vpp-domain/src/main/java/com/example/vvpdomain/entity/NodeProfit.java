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

    public String getProfitId() { return this.profitId; }
    public void setProfitId(String profitId) { this.profitId = profitId; }
    public String getNodeId() { return this.nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public double getProfitValue() { return this.profitValue; }
    public void setProfitValue(double profitValue) { this.profitValue = profitValue; }
    public Date getProfitDate() { return this.profitDate; }
    public void setProfitDate(Date profitDate) { this.profitDate = profitDate; }
    public double getPriceHigh() { return this.priceHigh; }
    public void setPriceHigh(double priceHigh) { this.priceHigh = priceHigh; }
    public double getPricePeak() { return this.pricePeak; }
    public void setPricePeak(double pricePeak) { this.pricePeak = pricePeak; }
    public double getPriceStable() { return this.priceStable; }
    public void setPriceStable(double priceStable) { this.priceStable = priceStable; }
    public double getPriceLow() { return this.priceLow; }
    public void setPriceLow(double priceLow) { this.priceLow = priceLow; }
    public double getInElectricityHigh() { return this.inElectricityHigh; }
    public void setInElectricityHigh(double inElectricityHigh) { this.inElectricityHigh = inElectricityHigh; }
    public double getInElectricityPeak() { return this.inElectricityPeak; }
    public void setInElectricityPeak(double inElectricityPeak) { this.inElectricityPeak = inElectricityPeak; }
    public double getInElectricityStable() { return this.inElectricityStable; }
    public void setInElectricityStable(double inElectricityStable) { this.inElectricityStable = inElectricityStable; }
    public double getInElectricityLow() { return this.inElectricityLow; }
    public void setInElectricityLow(double inElectricityLow) { this.inElectricityLow = inElectricityLow; }
    public double getOutElectricityHigh() { return this.outElectricityHigh; }
    public void setOutElectricityHigh(double outElectricityHigh) { this.outElectricityHigh = outElectricityHigh; }
    public double getOutElectricityPeak() { return this.outElectricityPeak; }
    public void setOutElectricityPeak(double outElectricityPeak) { this.outElectricityPeak = outElectricityPeak; }
    public double getOutElectricityStable() { return this.outElectricityStable; }
    public void setOutElectricityStable(double outElectricityStable) { this.outElectricityStable = outElectricityStable; }
    public double getOutElectricityLow() { return this.outElectricityLow; }
    public void setOutElectricityLow(double outElectricityLow) { this.outElectricityLow = outElectricityLow; }
    public Date getCreatedTime() { return this.createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return this.updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }

}