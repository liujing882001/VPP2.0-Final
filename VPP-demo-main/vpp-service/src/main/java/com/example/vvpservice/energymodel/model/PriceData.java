package com.example.vvpservice.energymodel.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class PriceData {
	private String timeFrame;

	private BigDecimal price;
}
