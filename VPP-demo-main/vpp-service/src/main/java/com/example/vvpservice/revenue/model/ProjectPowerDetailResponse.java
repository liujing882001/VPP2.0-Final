package com.example.vvpservice.revenue.model;
import java.util.Date;

import com.example.vvpcommom.serializer.DoubleTwoDecimalSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProjectPowerDetailResponse {
	private String nodeId;

	private List<DetailInfo> infos = new ArrayList<>();

	// Add missing methods manually since Lombok might not be working properly
	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public List<DetailInfo> getInfos() { return infos; }
	public void setInfos(List<DetailInfo> infos) { this.infos = infos; }

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class DetailInfo {
		private String month;

		private List<DeviceInfo> deviceInfo = new ArrayList<>();

		public String getMonth() { return month; }
		public void setMonth(String month) { this.month = month; }
		public List<DeviceInfo> getDeviceInfo() { return deviceInfo; }
		public void setDeviceInfo(List<DeviceInfo> deviceInfo) { this.deviceInfo = deviceInfo; }
	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class DeviceInfo {
		private String deviceType;

		private String deviceName;

		// 电量类型：充电 or 放电
		private String type;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double start = 0.0;
		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double end = 0.0;

		// 抄见电量
		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double meteredConsumption = 0.0;

		//损耗
		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double loss = 0.0;

		// 计费电量
		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double billingConsumption = 0.0;

		public String getDeviceType() { return deviceType; }
		public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
		public String getDeviceName() { return deviceName; }
		public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		public Double getStart() { return start; }
		public void setStart(Double start) { this.start = start; }
		public Double getEnd() { return end; }
		public void setEnd(Double end) { this.end = end; }
		public Double getMeteredConsumption() { return meteredConsumption; }
		public void setMeteredConsumption(Double meteredConsumption) { this.meteredConsumption = meteredConsumption; }
		public Double getLoss() { return loss; }
		public void setLoss(Double loss) { this.loss = loss; }
		public Double getBillingConsumption() { return billingConsumption; }
		public void setBillingConsumption(Double billingConsumption) { this.billingConsumption = billingConsumption; }
	}
}
