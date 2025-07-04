package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateProjectReq {

	String projectId;

	Map<String,String> baseInfo;

	Map<String,String> advanceInfo;
	
	// Add missing methods manually since Lombok might not be working properly
	public String getProjectId() { return projectId; }
	public void setProjectId(String projectId) { this.projectId = projectId; }
	public Map<String,String> getBaseInfo() { return baseInfo; }
	public void setBaseInfo(Map<String,String> baseInfo) { this.baseInfo = baseInfo; }
}
