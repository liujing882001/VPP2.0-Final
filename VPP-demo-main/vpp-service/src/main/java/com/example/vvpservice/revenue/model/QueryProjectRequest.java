package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class QueryProjectRequest {

	private String energyType;

	private List<ProjectInfo> infos;

	@Data
	public static class ProjectInfo{
		private String projectId;

		private LocalDate st;

		private LocalDate et;
	}
}


