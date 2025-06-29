package com.example.vvpweb.systemmanagement.stationnode.model;

import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.point.service.model.MappingView;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Data
public class StationPageQueryModel {

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
	/**
	 * 经度
	 */
	@JsonProperty(index = 9)
	private double longitude;

	/**
	 * 纬度
	 */
	@JsonProperty(index = 10)
	private double latitude;
	/**
	 * 楼宇建筑面积（平方米）
	 */
	@JsonProperty(index = 11)
	private double nodeArea;
	/**
	 * 省所在区域id
	 */
	@JsonProperty(index = 12)
	private String provinceRegionId;

	/**
	 * 省所在区域名称
	 */
	@JsonProperty(index = 13)
	private String provinceRegionName;
	/**
	 * 省份
	 */
	@JsonProperty(index = 14)
	private String province;
	/**
	 * 市所在区域id
	 */
	@JsonProperty(index = 15)
	private String cityRegionId;

	/**
	 * 市所在区域名称
	 */
	@JsonProperty(index = 16)
	private String cityRegionName;

	/**
	 * 县/区所在区域id
	 */
	@JsonProperty(index = 17)
	private String countyRegionId;

	/**
	 * 县/区所在区域名称
	 */
	@JsonProperty(index = 18)
	private String countyRegionName;

	/**
	 * 户号
	 */
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
	private String eType;

	@JsonProperty(index = 26)
	private String vol;

	@JsonProperty(index = 27)
	private String basicBill;

	@JsonProperty(index = 28)
	private String electricityCompany;

	@JsonProperty(index = 29)
	private List<MappingView> mappings;

	@JsonProperty(index = 30)
	@JsonInclude(JsonInclude.Include.NON_EMPTY)
	private List<StationNodeView> children;

	public StationPageQueryModel() {

	}

	public StationPageQueryModel(StationNode node, List<MappingView> mappings) {
		this.id = node.getId();
		this.stationId = node.getStationId();
		this.stationName = node.getStationName();
		this.stationCategory = node.getStationCategory();
		this.stationType = node.getStationType();
		this.createTime = node.getCreateTime();
		this.updateTime = node.getUpdateTime();
		this.parentId = node.getParentId();
		this.longitude = node.getLongitude();
		this.latitude = node.getLatitude();
		this.nodeArea = node.getNodeArea();
		this.provinceRegionId = node.getProvinceRegionId();
		this.provinceRegionName = node.getProvinceRegionName();
		this.province = node.getProvince();
		this.cityRegionId = node.getCityRegionId();
		this.cityRegionName = node.getCityRegionName();
		this.countyRegionId = node.getCountyRegionId();
		this.countyRegionName = node.getCountyRegionName();
		this.noHouseholds = node.getNoHouseholds();
		this.address = node.getAddress();
		this.systemIds = node.getSystemIds();
		this.stationTypeId = node.getStationTypeId();
		this.systemNames = node.getSystemNames();
		this.stationState = node.getStationState();
		this.children = new ArrayList<>();
		this.mappings = mappings;
		this.eType = node.getEType();
		this.vol = node.getVoltage();
		this.basicBill = node.getBasicBill();
		this.electricityCompany = node.getElectricityCompany();
	}

}
