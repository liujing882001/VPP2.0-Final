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

	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public List<PowerInfo> getInfos() { return infos; }
	public void setInfos(List<PowerInfo> infos) { this.infos = infos; }

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

		public String getMonth() { return month; }
		public void setMonth(String month) { this.month = month; }
		public Double getPvVolume() { return pvVolume; }
		public void setPvVolume(Double pvVolume) { this.pvVolume = pvVolume; }
		public Double getEsChargeVolume() { return esChargeVolume; }
		public void setEsChargeVolume(Double esChargeVolume) { this.esChargeVolume = esChargeVolume; }
		public Double getEsDischargeVolume() { return esDischargeVolume; }
		public void setEsDischargeVolume(Double esDischargeVolume) { this.esDischargeVolume = esDischargeVolume; }
	}
}
