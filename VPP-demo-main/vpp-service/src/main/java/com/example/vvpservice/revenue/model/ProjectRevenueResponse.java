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

	}
}
