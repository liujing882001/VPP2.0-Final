package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.time.YearMonth;

@Data
public class CopyNodeEpRequest {

	private String nodeId;

	private YearMonth date;

	// 手动添加缺失的getter/setter方法以确保编译通过
	public String getNodeId() { return nodeId; }
	public void setNodeId(String nodeId) { this.nodeId = nodeId; }
	public YearMonth getDate() { return date; }
	public void setDate(YearMonth date) { this.date = date; }
}
