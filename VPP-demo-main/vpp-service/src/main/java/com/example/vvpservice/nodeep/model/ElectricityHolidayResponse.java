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

	// Manual getter to ensure compilation
	public List<HolidayInfo> getHolidayInfos() {
		return holidayInfos;
	}

	@Data
	public static class HolidayInfo {
		private LocalDate date;

		private LocalTime st;

		private LocalTime et;

		private String cateGory;

		private BigDecimal price;

		// Manual getters to ensure compilation
		public LocalDate getDate() { return date; }
		public LocalTime getSt() { return st; }
		public LocalTime getEt() { return et; }
		public String getCateGory() { return cateGory; }
		public BigDecimal getPrice() { return price; }
	}
}
