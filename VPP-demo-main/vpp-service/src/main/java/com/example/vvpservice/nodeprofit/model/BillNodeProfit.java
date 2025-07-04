package com.example.vvpservice.nodeprofit.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BillNodeProfit {

    @ApiModelProperty("尖电价")
    private double priceHigh;

    @ApiModelProperty("峰电价")
    private double pricePeak ;

    @ApiModelProperty("平电价")
    private double priceStable;

    @ApiModelProperty("谷电价")
    private double priceLow ;

//    @ApiModelProperty("深谷电价")
//    private double priceRavine;

    @ApiModelProperty("充尖本月电量")
    private double inElectricityHigh ;

    @ApiModelProperty("充峰本月电量")
    private double inElectricityPeak ;

    @ApiModelProperty("充平本月电量")
    private double inElectricityStable ;

    @ApiModelProperty("充谷本月电量")
    private double inElectricityLow ;

//    @ApiModelProperty("充深谷本月电量")
//    private double inElectricityRavine;

    @ApiModelProperty("放尖本月电量")
    private double outElectricityHigh ;

    @ApiModelProperty("放峰本月电量")
    private double outElectricityPeak ;

    @ApiModelProperty("放平本月电量")
    private double outElectricityStable ;

    @ApiModelProperty("放平本月电量")
    private double outElectricityLow ;

//    @ApiModelProperty("放深谷本月电量")
//    private double outElectricityRavine;

    @ApiModelProperty("充尖本月电费")
    private double inElectricityHighPrice;

    @ApiModelProperty("充峰本月电费")
    private double inElectricityPeakPrice;

    @ApiModelProperty("充平本月电费")
    private double inElectricityStablePrice;

    @ApiModelProperty("充谷本月电费")
    private double inElectricityLowPrice;

//    @ApiModelProperty("充深谷本月电费")
//    private double inElectricityRavinePrice;

    @ApiModelProperty("放尖本月电费")
    private double outElectricityHighPrice;

    @ApiModelProperty("放峰本月电费")
    private double outElectricityPeakPrice;

    @ApiModelProperty("放平本月电费")
    private double outElectricityStablePrice;

    @ApiModelProperty("放平本月电费")
    private double outElectricityLowPrice;

//    @ApiModelProperty("放深谷本月电费")
//    private double outElectricityRavinePrice;

    public double getPriceHigh() { return priceHigh; }
    public void setPriceHigh(double priceHigh) { this.priceHigh = priceHigh; }
    public double getPricePeak() { return pricePeak; }
    public void setPricePeak(double pricePeak) { this.pricePeak = pricePeak; }
    public double getPriceStable() { return priceStable; }
    public void setPriceStable(double priceStable) { this.priceStable = priceStable; }
    public double getPriceLow() { return priceLow; }
    public void setPriceLow(double priceLow) { this.priceLow = priceLow; }
    public double getInElectricityHigh() { return inElectricityHigh; }
    public void setInElectricityHigh(double inElectricityHigh) { this.inElectricityHigh = inElectricityHigh; }
    public double getInElectricityPeak() { return inElectricityPeak; }
    public void setInElectricityPeak(double inElectricityPeak) { this.inElectricityPeak = inElectricityPeak; }
    public double getInElectricityStable() { return inElectricityStable; }
    public void setInElectricityStable(double inElectricityStable) { this.inElectricityStable = inElectricityStable; }
    public double getInElectricityLow() { return inElectricityLow; }
    public void setInElectricityLow(double inElectricityLow) { this.inElectricityLow = inElectricityLow; }
    public double getOutElectricityHigh() { return outElectricityHigh; }
    public void setOutElectricityHigh(double outElectricityHigh) { this.outElectricityHigh = outElectricityHigh; }
    public double getOutElectricityPeak() { return outElectricityPeak; }
    public void setOutElectricityPeak(double outElectricityPeak) { this.outElectricityPeak = outElectricityPeak; }
    public double getOutElectricityStable() { return outElectricityStable; }
    public void setOutElectricityStable(double outElectricityStable) { this.outElectricityStable = outElectricityStable; }
    public double getOutElectricityLow() { return outElectricityLow; }
    public void setOutElectricityLow(double outElectricityLow) { this.outElectricityLow = outElectricityLow; }
    public double getInElectricityHighPrice() { return inElectricityHighPrice; }
    public void setInElectricityHighPrice(double inElectricityHighPrice) { this.inElectricityHighPrice = inElectricityHighPrice; }
    public double getInElectricityPeakPrice() { return inElectricityPeakPrice; }
    public void setInElectricityPeakPrice(double inElectricityPeakPrice) { this.inElectricityPeakPrice = inElectricityPeakPrice; }
    public double getInElectricityStablePrice() { return inElectricityStablePrice; }
    public void setInElectricityStablePrice(double inElectricityStablePrice) { this.inElectricityStablePrice = inElectricityStablePrice; }
    public double getInElectricityLowPrice() { return inElectricityLowPrice; }
    public void setInElectricityLowPrice(double inElectricityLowPrice) { this.inElectricityLowPrice = inElectricityLowPrice; }
    public double getOutElectricityHighPrice() { return outElectricityHighPrice; }
    public void setOutElectricityHighPrice(double outElectricityHighPrice) { this.outElectricityHighPrice = outElectricityHighPrice; }
    public double getOutElectricityPeakPrice() { return outElectricityPeakPrice; }
    public void setOutElectricityPeakPrice(double outElectricityPeakPrice) { this.outElectricityPeakPrice = outElectricityPeakPrice; }
    public double getOutElectricityStablePrice() { return outElectricityStablePrice; }
    public void setOutElectricityStablePrice(double outElectricityStablePrice) { this.outElectricityStablePrice = outElectricityStablePrice; }
    public double getOutElectricityLowPrice() { return outElectricityLowPrice; }
    public void setOutElectricityLowPrice(double outElectricityLowPrice) { this.outElectricityLowPrice = outElectricityLowPrice; }
}
