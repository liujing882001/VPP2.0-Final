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

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class DetailInfo {
		private String month;

		private List<DeviceInfo> deviceInfo = new ArrayList<>();

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
	}
}
