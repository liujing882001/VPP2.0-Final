package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class CopyNodeEpRequest {

	private String nodeId;

	private YearMonth date;
}
