package com.example.vvpweb.electricitybill.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 *
 * 充电桩电费账单
 * @author yym
 */
@Data
public class ElectricityBillChargingPileResponse {

	@ApiModelProperty("电表安装地点")
	private String position;

	@ApiModelProperty("尖峰低谷价差收益")
	private BigDecimal profitValue = BigDecimal.ZERO;

	@ApiModelProperty("虚拟电厂运营商收益")
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

//	@ApiModelProperty("深谷电价")
//	private BigDecimal priceRavine = BigDecimal.ZERO;


	@ApiModelProperty("尖本月电量")
	private BigDecimal electricitySharp = BigDecimal.ZERO;

	@ApiModelProperty("峰本月电量")
	private BigDecimal electricityPeak = BigDecimal.ZERO;

	@ApiModelProperty("平本月电量")
	private BigDecimal electricityShoulder = BigDecimal.ZERO;

	@ApiModelProperty("谷本月电量")
	private BigDecimal electricityOffPeak = BigDecimal.ZERO;

//	@ApiModelProperty("深谷本月电量")
//	private BigDecimal electricityRavine = BigDecimal.ZERO;


	@ApiModelProperty("尖本月电费")
	private BigDecimal sharpEnergyCharge = BigDecimal.ZERO;

	@ApiModelProperty("峰本月电费")
	private BigDecimal peakEnergyCharge = BigDecimal.ZERO;

	@ApiModelProperty("平本月电费")
	private BigDecimal shoulderEnergyCharge = BigDecimal.ZERO;

	@ApiModelProperty("谷本月电费")
	private BigDecimal offPeakEnergyCharge = BigDecimal.ZERO;

//	@ApiModelProperty("谷本月电费")
//	private BigDecimal RavineEnergyCharge = BigDecimal.ZERO;

}
