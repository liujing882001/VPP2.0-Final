package com.example.vvpweb.electricitybill.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricityBillPvResponse {


    @ApiModelProperty("尖峰低谷价差收益")
    private BigDecimal profitValue = BigDecimal.ZERO;

    @ApiModelProperty("负荷集成商收益")
    private BigDecimal loadProfitValue = BigDecimal.ZERO;

    @ApiModelProperty("电力用户")
    private BigDecimal consumerProfitValue = BigDecimal.ZERO;

    @ApiModelProperty("电表名称")
    private String meterDeviceName;

    @ApiModelProperty("电表编号")
    private String meterDeviceNum;


    @ApiModelProperty("尖电价")
    private BigDecimal priceHigh = BigDecimal.ZERO;

    @ApiModelProperty("峰电价")
    private BigDecimal pricePeak = BigDecimal.ZERO;

    @ApiModelProperty("平电价")
    private BigDecimal priceStable = BigDecimal.ZERO;

    @ApiModelProperty("谷电价")
    private BigDecimal priceLow = BigDecimal.ZERO;

//    @ApiModelProperty("深谷电价")
//    private BigDecimal priceRavine = BigDecimal.ZERO;


    @ApiModelProperty("尖本月电量")
    private BigDecimal electricityHigh = BigDecimal.ZERO;

    @ApiModelProperty("峰本月电量")
    private BigDecimal electricityPeak = BigDecimal.ZERO;

    @ApiModelProperty("平本月电量")
    private BigDecimal electricityStable = BigDecimal.ZERO;

    @ApiModelProperty("谷本月电量")
    private BigDecimal electricityLow = BigDecimal.ZERO;

//    @ApiModelProperty("深谷本月电量")
//    private BigDecimal electricityRavine = BigDecimal.ZERO;


    @ApiModelProperty("尖本月电费")
    private BigDecimal electricityHighPrice = BigDecimal.ZERO;

    @ApiModelProperty("峰本月电费")
    private BigDecimal electricityPeakPrice = BigDecimal.ZERO;

    @ApiModelProperty("平本月电费")
    private BigDecimal electricityStablePrice = BigDecimal.ZERO;

    @ApiModelProperty("谷本月电费")
    private BigDecimal electricityLowPrice = BigDecimal.ZERO;

//    @ApiModelProperty("深谷本月电费")
//    private BigDecimal electricityRavinePrice = BigDecimal.ZERO;

}
