package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.util.Map;

@Data
public class UpdateProjectReq {

	String projectId;

	Map<String,String> baseInfo;

	Map<String,String> advanceInfo;
}
