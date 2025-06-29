package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class QueryNodeEPriceRequest {
	private String nodeId;

	private YearMonth date;
}
