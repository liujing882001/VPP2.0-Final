package com.example.vvpservice.revenue;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode
public class ExcelData {

	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private Date date;

	private Double power;
	
	// Add missing method manually since Lombok might not be working properly
	public Date getDate() { return date; }
	public void setDate(Date date) { this.date = date; }
	public Double getPower() { return power; }
	public void setPower(Double power) { this.power = power; }
}
