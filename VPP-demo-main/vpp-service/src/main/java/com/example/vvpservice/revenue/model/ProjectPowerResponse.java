package com.example.vvpservice.revenue.model;

import com.example.vvpcommom.serializer.DoubleTwoDecimalSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProjectPowerResponse {
	private String nodeId;

	private List<PowerInfo> infos = new ArrayList<>();

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class PowerInfo {
		private String month;

//		private Double gridVolume;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double pvVolume = 0.0;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double esChargeVolume = 0.0;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double esDischargeVolume = 0.0;

//		private Double enterpriseConsumption;
	}
}
