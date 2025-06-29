package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class GenerateEPriceRequest {
	private String nodeId;

	private YearMonth date;

	private String city;

	private String type;

	private String voltage;

}
