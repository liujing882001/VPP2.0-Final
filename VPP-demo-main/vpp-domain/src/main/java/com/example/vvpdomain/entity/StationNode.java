package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "station_node")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class StationNode implements Serializable {
    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "station_id")
    private String stationId;

    @Column(name = "station_name")
    private String stationName;

    @Column(name = "station_category")
    private String stationCategory;

    @Column(name = "station_type")
    private String stationType;

    @CreatedDate
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "create_time")
    private Date createTime;

    @LastModifiedDate
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "parent_id")
    private String parentId;
    /**
     * 经度
     */
    @Column(name = "longitude")
    private double longitude;

    /**
     * 纬度
     */
    @Column(name = "latitude")
    private double latitude;
    /**
     * 楼宇建筑面积（平方米）
     */
    @Column(name = "node_area")
    private double nodeArea;
    /**
     * 省所在区域id
     */
    @Column(name = "province_region_id")
    private String provinceRegionId;

    /**
     * 省所在区域名称
     */
    @Column(name = "province_region_name")
    private String provinceRegionName;
    /**
     * 省份
     */
    @Column(name = "province")
    private String province;
    /**
     * 市所在区域id
     */
    @Column(name = "city_region_id")
    private String cityRegionId;

    /**
     * 市所在区域名称
     */
    @Column(name = "city_region_name")
    private String cityRegionName;

    /**
     * 县/区所在区域id
     */
    @Column(name = "county_region_id")
    private String countyRegionId;

    /**
     * 县/区所在区域名称
     */
    @Column(name = "county_region_name")
    private String countyRegionName;

    /**
     * 户号
     */
    @Column(name = "no_households")
    private String noHouseholds;

    @Column(name = "address")
    private String address;

    @Column(name = "system_ids")
    private String systemIds;

    @Column(name = "station_type_id")
    private String stationTypeId;

    @Column(name = "system_names")
    private String systemNames;

    @Column(name = "station_state")
    private String stationState;

    @Column(name = "electricity_type")
    private String eType;

    @Column(name = "voltage")
    private String voltage;

    @Column(name = "basic_bill")
    private String basicBill;

    @Column(name = "electricity_company")
    private String electricityCompany;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getStationId() { return stationId; }
    public void setStationId(String stationId) { this.stationId = stationId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getStationCategory() { return stationCategory; }
    public void setStationCategory(String stationCategory) { this.stationCategory = stationCategory; }
    public String getStationType() { return stationType; }
    public void setStationType(String stationType) { this.stationType = stationType; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getNodeArea() { return nodeArea; }
    public void setNodeArea(double nodeArea) { this.nodeArea = nodeArea; }
    public String getProvinceRegionId() { return provinceRegionId; }
    public void setProvinceRegionId(String provinceRegionId) { this.provinceRegionId = provinceRegionId; }
    public String getProvinceRegionName() { return provinceRegionName; }
    public void setProvinceRegionName(String provinceRegionName) { this.provinceRegionName = provinceRegionName; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getCityRegionId() { return cityRegionId; }
    public void setCityRegionId(String cityRegionId) { this.cityRegionId = cityRegionId; }
    public String getCityRegionName() { return cityRegionName; }
    public void setCityRegionName(String cityRegionName) { this.cityRegionName = cityRegionName; }
    public String getCountyRegionId() { return countyRegionId; }
    public void setCountyRegionId(String countyRegionId) { this.countyRegionId = countyRegionId; }
    public String getCountyRegionName() { return countyRegionName; }
    public void setCountyRegionName(String countyRegionName) { this.countyRegionName = countyRegionName; }
    public String getNoHouseholds() { return noHouseholds; }
    public void setNoHouseholds(String noHouseholds) { this.noHouseholds = noHouseholds; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getSystemIds() { return systemIds; }
    public void setSystemIds(String systemIds) { this.systemIds = systemIds; }
    public String getStationTypeId() { return stationTypeId; }
    public void setStationTypeId(String stationTypeId) { this.stationTypeId = stationTypeId; }
    public String getSystemNames() { return systemNames; }
    public void setSystemNames(String systemNames) { this.systemNames = systemNames; }
    public String getStationState() { return stationState; }
    public void setStationState(String stationState) { this.stationState = stationState; }
    public String getEType() { return eType; }
    public void setEType(String eType) { this.eType = eType; }
    public String getVoltage() { return voltage; }
    public void setVoltage(String voltage) { this.voltage = voltage; }
    public String getBasicBill() { return basicBill; }
    public void setBasicBill(String basicBill) { this.basicBill = basicBill; }
    public String getElectricityCompany() { return electricityCompany; }
    public void setElectricityCompany(String electricityCompany) { this.electricityCompany = electricityCompany; }
}
