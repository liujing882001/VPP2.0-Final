package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ElectricityTimeSlotResponse {

	private String province;

	private String city;

	private Integer month;

	private LocalTime startTime;

	private LocalTime endTime;

	private String category;

	// 手动添加缺失的getter/setter方法以确保编译通过
	public String getProvince() { return province; }
	public void setProvince(String province) { this.province = province; }
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	public Integer getMonth() { return month; }
	public void setMonth(Integer month) { this.month = month; }
	public LocalTime getStartTime() { return startTime; }
	public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
	public LocalTime getEndTime() { return endTime; }
	public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
	public String getCategory() { return category; }
	public void setCategory(String category) { this.category = category; }
}
