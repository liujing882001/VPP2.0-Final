package com.example.vvpservice.nodeep.model;
import java.util.Date;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class ElectricityHolidayResponse {

	private List<HolidayInfo> holidayInfos = new ArrayList<>();

	@Data
	public static class HolidayInfo {
		private LocalDate date;

		private LocalTime st;

		private LocalTime et;

		private String cateGory;

		private BigDecimal price;
	}
}
