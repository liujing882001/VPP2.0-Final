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
public class ProjectRevenueDetailResponse {

	private String nodeId;

	private List<Detail> esDetail = new ArrayList<>();

	private List<Detail> pvDetail = new ArrayList<>();

	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public List<Detail> getEsDetail() { return esDetail; }
	public void setEsDetail(List<Detail> esDetail) { this.esDetail = esDetail; }
	public List<Detail> getPvDetail() { return pvDetail; }
	public void setPvDetail(List<Detail> pvDetail) { this.pvDetail = pvDetail; }

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

		public String getChargingType() { return chargingType; }
		public void setChargingType(String chargingType) { this.chargingType = chargingType; }
		public String getMonth() { return month; }
		public void setMonth(String month) { this.month = month; }
		public Double getTotalPower() { return totalPower; }
		public void setTotalPower(Double totalPower) { this.totalPower = totalPower; }
		public Double getTotalAmount() { return totalAmount; }
		public void setTotalAmount(Double totalAmount) { this.totalAmount = totalAmount; }
		public List<EleInfo> getData() { return data; }
		public void setData(List<EleInfo> data) { this.data = data; }
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

		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		public Double getPrice() { return price; }
		public void setPrice(Double price) { this.price = price; }
		public Double getPower() { return power; }
		public void setPower(Double power) { this.power = power; }
		public Double getAmount() { return amount; }
		public void setAmount(Double amount) { this.amount = amount; }
	}
}
