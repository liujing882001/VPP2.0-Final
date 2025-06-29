package com.example.vvpweb.electricitybill.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricityBillStorageEnergyResponse {


    @ApiModelProperty("尖峰低谷价差收益")
    private BigDecimal profitValue = BigDecimal.ZERO;

    @ApiModelProperty("虚拟电厂运营商收益")
    private BigDecimal loadProfitValue = BigDecimal.ZERO;

    @ApiModelProperty("电力用户")
    private BigDecimal consumerProfitValue = BigDecimal.ZERO;


    @ApiModelProperty("充电电表名称")
    private String inMeterDeviceName;

    @ApiModelProperty("充电电表编号")
    private String inMeterDeviceNum;

    @ApiModelProperty("放电电表名称")
    private String outMeterDeviceName;

    @ApiModelProperty("放电电表编号")
    private String outMeterDeviceNum;


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

    @ApiModelProperty("充尖本月电量")
    private BigDecimal inElectricityHigh = BigDecimal.ZERO;

    @ApiModelProperty("充峰本月电量")
    private BigDecimal inElectricityPeak = BigDecimal.ZERO;

    @ApiModelProperty("充平本月电量")
    private BigDecimal inElectricityStable = BigDecimal.ZERO;

    @ApiModelProperty("充谷本月电量")
    private BigDecimal inElectricityLow = BigDecimal.ZERO;

//    @ApiModelProperty("充深谷本月电量")
//    private BigDecimal inElectricityRavine = BigDecimal.ZERO;

    @ApiModelProperty("放尖本月电量")
    private BigDecimal outElectricityHigh = BigDecimal.ZERO;

    @ApiModelProperty("放峰本月电量")
    private BigDecimal outElectricityPeak = BigDecimal.ZERO;

    @ApiModelProperty("放平本月电量")
    private BigDecimal outElectricityStable = BigDecimal.ZERO;

    @ApiModelProperty("放平本月电量")
    private BigDecimal outElectricityLow = BigDecimal.ZERO;

//    @ApiModelProperty("放深谷本月电量")
//    private BigDecimal outElectricityRavine = BigDecimal.ZERO;

    @ApiModelProperty("充尖本月电费")
    private BigDecimal inElectricityHighPrice = BigDecimal.ZERO;

    @ApiModelProperty("充峰本月电费")
    private BigDecimal inElectricityPeakPrice = BigDecimal.ZERO;

    @ApiModelProperty("充平本月电费")
    private BigDecimal inElectricityStablePrice = BigDecimal.ZERO;

    @ApiModelProperty("充谷本月电费")
    private BigDecimal inElectricityLowPrice = BigDecimal.ZERO;

//    @ApiModelProperty("充深谷本月电费")
//    private BigDecimal inElectricityRavinePrice = BigDecimal.ZERO;

    @ApiModelProperty("放尖本月电费")
    private BigDecimal outElectricityHighPrice = BigDecimal.ZERO;

    @ApiModelProperty("放峰本月电费")
    private BigDecimal outElectricityPeakPrice = BigDecimal.ZERO;

    @ApiModelProperty("放平本月电费")
    private BigDecimal outElectricityStablePrice = BigDecimal.ZERO;

    @ApiModelProperty("放谷本月电费")
    private BigDecimal outElectricityLowPrice = BigDecimal.ZERO;

//    @ApiModelProperty("放深谷本月电费")
//    private BigDecimal outElectricityRavinePrice = BigDecimal.ZERO;

}
