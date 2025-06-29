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
}
