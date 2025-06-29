package com.example.vvpweb.presalesmodule.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@JsonPropertyOrder({"city", "year", "type_1", "type_2", "voltage_1", "voltage_2"})
public class ElectricityQueryYearlyResponse {

	/**
	 * 市
	 */
	private String city;

	/**
	 * 年份
	 */
	private int year;

	private String type_1;

	private String type_2;

	private String voltage_1;

	private String voltage_2;

	private List<PriceInfo> pricesList = new ArrayList<>();

}
