package com.example.vvpweb.point.model;


import lombok.Data;

import java.util.List;

@Data
public class BindRequest {
	private String stationId;

	private String system;

	private String pointKey;

	private List<String> deviceList;
}
