package com.example.vvpservice.revenue.model;

import com.example.vvpcommom.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class ProjectRevenueResponse {

	private String nodeId;

	private List<Profit> profits = new ArrayList<>();

	// Add missing methods manually since Lombok might not be working properly
	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public List<Profit> getProfits() { return profits; }
	public void setProfits(List<Profit> profits) { this.profits = profits; }

	@Data
	@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
	public static class Profit {
		private String month;

		private String type;

		@JsonSerialize(using = BigDecimalSerializer.class)
		private BigDecimal profitTotal = BigDecimal.ZERO;

		@JsonSerialize(using = BigDecimalSerializer.class)
		private BigDecimal profitActual = BigDecimal.ZERO;

		@JsonSerialize(using = BigDecimalSerializer.class)
		private BigDecimal profitElectricity = BigDecimal.ZERO;

		@JsonSerialize(using = BigDecimalSerializer.class)
		private BigDecimal profitOperator = BigDecimal.ZERO;

		// Add missing methods manually since Lombok might not be working properly
		public String getMonth() { return month; }
		public void setMonth(String month) { this.month = month; }
		public String getType() { return type; }
		public void setType(String type) { this.type = type; }
		public BigDecimal getProfitTotal() { return profitTotal; }
		public void setProfitTotal(BigDecimal profitTotal) { this.profitTotal = profitTotal; }
		public BigDecimal getProfitActual() { return profitActual; }
		public void setProfitActual(BigDecimal profitActual) { this.profitActual = profitActual; }
		public BigDecimal getProfitElectricity() { return profitElectricity; }
		public void setProfitElectricity(BigDecimal profitElectricity) { this.profitElectricity = profitElectricity; }
		public BigDecimal getProfitOperator() { return profitOperator; }
		public void setProfitOperator(BigDecimal profitOperator) { this.profitOperator = profitOperator; }
	}
}
