package com.example.vvpservice.revenue.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@JsonPropertyOrder({"field", "value", "children"})
public class EleNodeInfo {

	private String field;

	private Object value;

	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<EleNodeInfo> children;

	@JSONField(serialize = false)
	@JsonIgnore
	private Map<String, EleNodeInfo> childrenMap;

	public EleNodeInfo() {
		this.children = new ArrayList<>();
		this.childrenMap = new HashMap<>();
	}

	public EleNodeInfo(String field, Object o) {
		this.field = field;
		this.value = o;
		this.children = new ArrayList<>();
		this.childrenMap = new HashMap<>();
	}

	public void addEleNodeInfo(EleNodeInfo nodeInfo) {
		this.children.add(nodeInfo);
		this.childrenMap.put(String.valueOf(nodeInfo.value), nodeInfo);
	}
	
	// Add missing methods manually since Lombok might not be working properly
	public String getField() { return field; }
	public void setField(String field) { this.field = field; }
	public Object getValue() { return value; }
	public void setValue(Object value) { this.value = value; }
	public List<EleNodeInfo> getChildren() { return children; }
	public void setChildren(List<EleNodeInfo> children) { this.children = children; }
}
