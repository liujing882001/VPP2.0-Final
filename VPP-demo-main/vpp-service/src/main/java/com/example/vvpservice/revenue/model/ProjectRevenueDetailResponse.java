package com.example.vvpservice.revenue.model;

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
public class ProjectRevenueDetailResponse {

	private String nodeId;

	private List<Detail> esDetail = new ArrayList<>();

	private List<Detail> pvDetail = new ArrayList<>();

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Detail{

		private String chargingType;

		private String month;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double totalPower = 0.0;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double totalAmount = 0.0;

		private List<EleInfo> data = new ArrayList<>();
	}

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class EleInfo{

		private String type;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double price;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double power;

		@JsonSerialize(using = DoubleTwoDecimalSerializer.class)
		private Double amount;

		public EleInfo(String type){
			this.type = type;
			this.power = 0.0;
			this.amount = 0.0;
		}

		public EleInfo(String type, Double price, Double power) {
			this.type = type;
			this.price = price;
			this.power = power;
		}
	}
}
