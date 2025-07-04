package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description node
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "node")
public class Node implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * node_id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * node_name
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * node_type_id
     */
    @ManyToOne(targetEntity = SysDictNode.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @JoinColumn(name = "node_type_id", referencedColumnName = "node_type_id")
    private SysDictNode nodeType;


    @OneToMany(mappedBy = "node",
            targetEntity = Device.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @Fetch(FetchMode.SUBSELECT)
    private List<Device> deviceList;


    /**
     * address
     */
    @Column(name = "address")
    private String address;


    /**
     * 表字段： ca_emission_factor.province
     */
    @Column(name = "province")
    private String province;

    /**
     * 默认可见系统列表，仅设备管理时根据该ids 过滤 默认显示的所有系统列表
     */
    @Column(name = "system_ids")
    private String systemIds;


    /**
     * node_post_type
     * 节点归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    @Column(name = "node_post_type")
    private String nodePostType;


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
     * 是否在线
     */
    @Column(name = "online")
    private Boolean online;


    /**
     * 楼宇建筑面积（平方米）
     */
    @Column(name = "node_area")
    private double nodeArea;


    /**
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;


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

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    public Node() {
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public SysDictNode getNodeType() { return nodeType; }
    public void setNodeType(SysDictNode nodeType) { this.nodeType = nodeType; }
    public List<Device> getDeviceList() { return deviceList; }
    public void setDeviceList(List<Device> deviceList) { this.deviceList = deviceList; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getProvince() { return province; }
    public void setProvince(String province) { this.province = province; }
    public String getSystemIds() { return systemIds; }
    public void setSystemIds(String systemIds) { this.systemIds = systemIds; }
    public String getProvinceRegionName() { return provinceRegionName; }
    public String getCountyRegionName() { return countyRegionName; }
    public String getCityRegionName() { return cityRegionName; }
    public String getNodePostType() { return nodePostType; }

    // 手动添加缺失的getter方法以确保编译通过
    public Boolean getOnline() {
        return online;
    }

    public String getProvinceRegionId() { return provinceRegionId; }
    public void setProvinceRegionId(String provinceRegionId) { this.provinceRegionId = provinceRegionId; }
    public void setProvinceRegionName(String provinceRegionName) { this.provinceRegionName = provinceRegionName; }
    public String getCityRegionId() { return cityRegionId; }
    public void setCityRegionId(String cityRegionId) { this.cityRegionId = cityRegionId; }
    public void setCityRegionName(String cityRegionName) { this.cityRegionName = cityRegionName; }
    public String getCountyRegionId() { return countyRegionId; }
    public void setCountyRegionId(String countyRegionId) { this.countyRegionId = countyRegionId; }
    public void setCountyRegionName(String countyRegionName) { this.countyRegionName = countyRegionName; }
    public void setNodePostType(String nodePostType) { this.nodePostType = nodePostType; }
    public void setOnline(Boolean online) { this.online = online; }
    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }
    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }
    public double getNodeArea() { return nodeArea; }
    public void setNodeArea(double nodeArea) { this.nodeArea = nodeArea; }
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    public String getNoHouseholds() { return noHouseholds; }
    public void setNoHouseholds(String noHouseholds) { this.noHouseholds = noHouseholds; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
}