package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.LocalTime;

@Data
public class ElectricityTimeSlotResponse {

	private String province;

	private String city;

	private Integer month;

	private LocalTime startTime;

	private LocalTime endTime;

	private String category;
}
