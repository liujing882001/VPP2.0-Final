package com.example.vvpweb.electricityTrading.model;

import lombok.Data;

import java.util.Date;

/**
 * @author yym
 */
@Data
public class PriceChartRequest {

	private String nodeId;

	private Date st;

	private Date et;
}
