package com.example.vvpservice.energymodel.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceData {
	private String timeFrame;

	private BigDecimal price;

	public PriceData() {}

	public PriceData(String timeFrame, BigDecimal price) {
		this.timeFrame = timeFrame;
		this.price = price;
	}

	public String getTimeFrame() { return timeFrame; }

	public void setTimeFrame(String timeFrame) { this.timeFrame = timeFrame; }

	public BigDecimal getPrice() { return price; }

	public void setPrice(BigDecimal price) { this.price = price; }
}
