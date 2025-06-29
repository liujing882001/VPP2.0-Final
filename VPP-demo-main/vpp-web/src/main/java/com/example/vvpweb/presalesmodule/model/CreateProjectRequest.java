package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

@Data
public class CreateProjectRequest {

	private String projectId;

	private String projectName;

	private String area;

	private Double volume;

	private Double power;
}
