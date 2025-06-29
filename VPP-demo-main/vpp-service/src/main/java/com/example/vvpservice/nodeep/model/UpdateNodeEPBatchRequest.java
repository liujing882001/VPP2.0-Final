package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

@Data
public class UpdateNodeEPBatchRequest {
	private String nodeId;

	private YearMonth date;

	private String city;

	private String vol1;

	private String type1;

	private List<price> prices;

	@Data
	public static class price{
		private String type;

		private BigDecimal price;
	}
}
