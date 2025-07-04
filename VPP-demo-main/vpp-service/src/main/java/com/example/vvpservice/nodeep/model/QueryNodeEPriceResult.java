package com.example.vvpservice.nodeep.model;

import com.example.vvpcommom.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Data
public class QueryNodeEPriceResult {

	private String nodeId;

	private YearMonth date;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceSharp = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal pricePeak = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceShoulder = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceOffPeak = BigDecimal.ZERO;
//
//	@JsonSerialize(using = BigDecimalSerializer.class)
//	private BigDecimal priceRavine = BigDecimal.ZERO;

	private Map<String,String> timeSlots;

	// 手动添加缺失的getter/setter方法以确保编译通过
	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public YearMonth getDate() { return date; }
	public void setDate(YearMonth date) { this.date = date; }
	public BigDecimal getPriceSharp() { return priceSharp; }
	public void setPriceSharp(BigDecimal priceSharp) { this.priceSharp = priceSharp; }
	public BigDecimal getPricePeak() { return pricePeak; }
	public void setPricePeak(BigDecimal pricePeak) { this.pricePeak = pricePeak; }
	public BigDecimal getPriceShoulder() { return priceShoulder; }
	public void setPriceShoulder(BigDecimal priceShoulder) { this.priceShoulder = priceShoulder; }
	public BigDecimal getPriceOffPeak() { return priceOffPeak; }
	public void setPriceOffPeak(BigDecimal priceOffPeak) { this.priceOffPeak = priceOffPeak; }
	public Map<String,String> getTimeSlots() { return timeSlots; }
	public void setTimeSlots(Map<String,String> timeSlots) { this.timeSlots = timeSlots; }
}
