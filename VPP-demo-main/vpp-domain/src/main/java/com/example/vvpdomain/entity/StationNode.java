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
}
