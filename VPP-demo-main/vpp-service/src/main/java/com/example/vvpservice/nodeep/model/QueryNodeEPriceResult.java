package com.example.vvpservice.nodeep.model;

import com.example.vvpcommom.serializer.BigDecimalSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Map;

@Data
public class QueryNodeEPriceResult {

	private String nodeId;

	private YearMonth date;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceSharp = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal pricePeak = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceShoulder = BigDecimal.ZERO;

	@JsonSerialize(using = BigDecimalSerializer.class)
	private BigDecimal priceOffPeak = BigDecimal.ZERO;
//
//	@JsonSerialize(using = BigDecimalSerializer.class)
//	private BigDecimal priceRavine = BigDecimal.ZERO;

	private Map<String,String> timeSlots;
}
