package com.example.vvpweb.systemmanagement.stationnode.model;

import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.point.service.model.MappingView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class StationNodeView {

	@JsonProperty(index = 1)
	private String id;

	@JsonProperty(index = 2)
	private String stationId;

	@JsonProperty(index = 3)
	private String stationName;

	@JsonProperty(index = 4)
	private String stationCategory;

	@JsonProperty(index = 5)
	private String stationType;

	@JsonProperty(index = 6)
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;

	@JsonProperty(index = 7)
	@JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date updateTime;

	@JsonProperty(index = 8)
	private String parentId;

	@JsonProperty(index = 9)
	private double longitude;

	@JsonProperty(index = 10)
	private double latitude;

	@JsonProperty(index = 11)
	private double nodeArea;

	@JsonProperty(index = 12)
	private String provinceRegionId;

	@JsonProperty(index = 13)
	private String provinceRegionName;

	@JsonProperty(index = 14)
	private String province;

	@JsonProperty(index = 15)
	private String cityRegionId;

	@JsonProperty(index = 16)
	private String cityRegionName;

	@JsonProperty(index = 17)
	private String countyRegionId;

	@JsonProperty(index = 18)
	private String countyRegionName;

	@JsonProperty(index = 19)
	private String noHouseholds;

	@JsonProperty(index = 20)
	private String address;

	@JsonProperty(index = 21)
	private String systemIds;

	@JsonProperty(index = 22)
	private String stationTypeId;

	@JsonProperty(index = 23)
	private String systemNames;

	@JsonProperty(index = 24)
	private String stationState;

	@JsonProperty(index = 25)
	private List<MappingView> mappings;

	@JsonProperty(index = 26)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<StationNodeView> children = new ArrayList<>();

	public StationNodeView(StationNode stationNode, List<MappingView> mappings) {
		this.id = stationNode.getId();
		this.stationId = stationNode.getStationId();
		this.stationName = stationNode.getStationName();
		this.stationCategory = stationNode.getStationCategory();
		this.stationType = stationNode.getStationType();
		this.createTime = stationNode.getCreateTime();
		this.updateTime = stationNode.getUpdateTime();
		this.parentId = stationNode.getParentId();
		this.longitude = stationNode.getLongitude();
		this.latitude = stationNode.getLatitude();
		this.nodeArea = stationNode.getNodeArea();
		this.provinceRegionId = stationNode.getProvinceRegionId();
		this.provinceRegionName = stationNode.getProvinceRegionName();
		this.province = stationNode.getProvince();
		this.cityRegionId = stationNode.getCityRegionId();
		this.cityRegionName = stationNode.getCityRegionName();
		this.countyRegionId = stationNode.getCountyRegionId();
		this.countyRegionName = stationNode.getCountyRegionName();
		this.noHouseholds = stationNode.getNoHouseholds();
		this.address = stationNode.getAddress();
		this.systemIds = stationNode.getSystemIds();
		this.stationTypeId = stationNode.getStationTypeId();
		this.systemNames = stationNode.getSystemNames();
		this.stationState = stationNode.getStationState();
		this.mappings = mappings;
	}
}
