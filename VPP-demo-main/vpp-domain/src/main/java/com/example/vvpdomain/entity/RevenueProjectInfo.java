package com.example.vvpdomain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "revenue_estimation_project_info")
public class RevenueProjectInfo {

	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "name")
	private String name;

	@Column(name = "file_name")
	private String fileName;

	@Column(name = "area")
	private String area;

	@Column(name = "fundamental_parameter")
	private String fundamentalParameter;

	@Column(name = "advance_parameter")
	private String advanceParameter;

	@Column(name = "intermediate_result")
	private String intermediateResult;

	@Column(name = "business_result")
	private String businessResult;

	@Column(name = "electricity_parameter")
	private String electricityParameter;

	@Column(name = "user_id")
	private String userId;
	
	// Add missing methods manually since Lombok might not be working properly
	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	public String getArea() { return area; }
	public void setArea(String area) { this.area = area; }
	public String getFundamentalParameter() { return fundamentalParameter; }
	public void setFundamentalParameter(String fundamentalParameter) { this.fundamentalParameter = fundamentalParameter; }
	public String getElectricityParameter() { return electricityParameter; }
	public void setElectricityParameter(String electricityParameter) { this.electricityParameter = electricityParameter; }
}
