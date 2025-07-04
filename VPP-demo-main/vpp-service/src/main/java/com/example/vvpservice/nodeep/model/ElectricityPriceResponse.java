package com.example.vvpservice.nodeep.model;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class ElectricityPriceResponse {
    public String getSTime() { return sTime; }
    public void setSTime(String sTime) { this.sTime = sTime; }
    public String getETime() { return eTime; }
    public void setETime(String eTime) { this.eTime = eTime; }

    private String sTime;
    private String eTime;

	private String city;

	/**
	 * 用电类型
	 */
	private String electricityCategory;


	/**
	 * 月份
	 */
	private int year;

	/**
	 * 月份
	 */
	private int month;

	/**
	 * 电压
	 */
	private String voltage;


	private List<priceData> priceList = new ArrayList<>();

	// 手动添加缺失的getter/setter方法以确保编译通过
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	public String getElectricityCategory() { return electricityCategory; }
	public void setElectricityCategory(String electricityCategory) { this.electricityCategory = electricityCategory; }
	public int getYear() { return year; }
	public void setYear(int year) { this.year = year; }
	public int getMonth() { return month; }
	public void setMonth(int month) { this.month = month; }
	public String getVoltage() { return voltage; }
	public void setVoltage(String voltage) { this.voltage = voltage; }
	public List<priceData> getPriceList() { return priceList; }
	public void setPriceList(List<priceData> priceList) { this.priceList = priceList; }

	@Data
	public static class priceData {

		/**
		 * 开始时间:结束时间
		 */
		private String startDate;

		/**
		 * 时段分类：尖、峰、平、谷、深谷
		 */
		private String timeCategory;

		/**
		 * 价格
		 */
		private BigDecimal price;

		/**
		 * 用电量
		 */
		private String consumption;

		// 手动添加缺失的getter/setter方法以确保编译通过
		public String getStartDate() { return startDate; }
		public void setStartDate(String startDate) { this.startDate = startDate; }
		public String getTimeCategory() { return timeCategory; }
		public void setTimeCategory(String timeCategory) { this.timeCategory = timeCategory; }
		public BigDecimal getPrice() { return price; }
		public void setPrice(BigDecimal price) { this.price = price; }
		public String getConsumption() { return consumption; }
		public void setConsumption(String consumption) { this.consumption = consumption; }
	}
}
