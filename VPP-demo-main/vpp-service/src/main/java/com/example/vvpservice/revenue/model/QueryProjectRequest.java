package com.example.vvpservice.revenue.model;
import java.util.Date;

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
		
		// Add missing methods manually since Lombok might not be working properly
		public String getProjectId() { return projectId; }
		public void setProjectId(String projectId) { this.projectId = projectId; }
		public LocalDate getSt() { return st; }
		public void setSt(LocalDate st) { this.st = st; }
		public LocalDate getEt() { return et; }
		public void setEt(LocalDate et) { this.et = et; }
	}
	
	// Add missing methods manually since Lombok might not be working properly
	public String getEnergyType() { return energyType; }
	public void setEnergyType(String energyType) { this.energyType = energyType; }
	public List<ProjectInfo> getInfos() { return infos; }
	public void setInfos(List<ProjectInfo> infos) { this.infos = infos; }
}


