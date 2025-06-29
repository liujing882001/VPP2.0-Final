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
}
