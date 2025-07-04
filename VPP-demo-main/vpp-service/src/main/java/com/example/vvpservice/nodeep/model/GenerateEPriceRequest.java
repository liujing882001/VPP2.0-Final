package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class GenerateEPriceRequest {
	private String nodeId;

	private YearMonth date;

	private String city;

	private String type;

	private String voltage;

	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public YearMonth getDate() { return date; }
	public void setDate(YearMonth date) { this.date = date; }
	public String getCity() { return city; }
	public void setCity(String city) { this.city = city; }
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	public String getVoltage() { return voltage; }
	public void setVoltage(String voltage) { this.voltage = voltage; }
}
