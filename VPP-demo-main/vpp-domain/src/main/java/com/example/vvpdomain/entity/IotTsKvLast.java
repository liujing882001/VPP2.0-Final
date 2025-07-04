package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description iot_ts_kv_last_view 最新点位数据视图
 * @date 2022-07-01
 */
@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "iot_ts_kv_last_view")
public class IotTsKvLast implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

    /**
     * province_region_id 省所在区域id
     */
    @Column(name = "province_region_id")
    private String provinceRegionId;

    /**
     * province_region_name 省份区域名称
     */
    @Column(name = "province_region_name")
    private String provinceRegionName;

    /**
     * city_region_id 市所在区域id
     */
    @Column(name = "city_region_id")
    private String cityRegionId;

    /**
     * city_region_name 市名称
     */
    @Column(name = "city_region_name")
    private String cityRegionName;

    /**
     * county_region_id 县/区所在区域id
     */
    @Column(name = "county_region_id")
    private String countyRegionId;

    /**
     * county_region_name 县/区名称
     */
    @Column(name = "county_region_name")
    private String countyRegionName;

    /**
     * node_id 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * node_name 节点名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * latitude 节点纬度
     */
    @Column(name = "latitude")
    private double latitude;

    /**
     * node_post_type
     * 节点归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    @Column(name = "node_post_type")
    private String nodePostType;

    /**
     * longitude 节点经度
     */
    @Column(name = "longitude")
    private double longitude;

    /**
     * system_id 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * system_name 系统名称
     */
    @Column(name = "system_name")
    private String systemName;

    /**
     * device_sn 设备序列号
     */
    @Column(name = "device_sn")
    private String deviceSn;

    /**
     * device_name 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * device_config_key 该标签指定设备类型为，默认为其它(other) ，计量设备(metering_device)/其它(other)
     */
    @Column(name = "device_config_key")
    private String deviceConfigKey;

    /**
     * point_sn 点位属性唯一码
     */
    @Column(name = "point_sn")
    private String pointSn;

    /**
     * point_name 点位名称
     */
    @Column(name = "point_name")
    private String pointName;

    /**
     * point_value 上传报文值
     */
    @Column(name = "point_value")
    private String pointValue;

    /**
     * point_desc
     */
    @Column(name = "point_desc")
    private String pointDesc;

    /**
     * point_value_type 上传报文值类型(1 bool 2 数字  3 字符串)
     */
    @Column(name = "point_value_type")
    private String pointValueType;

    /**
     * 数据点单位 电压V  电流 I等等
     */
    @Column(name = "point_unit")
    private String pointUnit;


    /**
     * 消息类型 MSG/ALARM
     */
    @Column(name = "msg_type")
    private String msgType;

    /**
     * ts 消息时间戳 毫秒数转为 年月日 时分秒 毫秒 YYY-MM-dd HH:mm:ss.S
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date ts;

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


    public IotTsKvLast() {
    }

    public Date getTs() { return ts; }
    public String getValue() { return pointValue; }

    public String getPointDesc() { return pointDesc; }
    public String getPointValue() { return pointValue; }
    public String getId() { return id; }
    public String getProvinceRegionId() { return provinceRegionId; }
    public String getProvinceRegionName() { return provinceRegionName; }
    public String getCityRegionId() { return cityRegionId; }
    public String getCityRegionName() { return cityRegionName; }
    public String getCountyRegionId() { return countyRegionId; }
    public String getCountyRegionName() { return countyRegionName; }
    public String getNodeId() { return nodeId; }
    public String getNodeName() { return nodeName; }
    public double getLatitude() { return latitude; }
    public String getNodePostType() { return nodePostType; }
    public double getLongitude() { return longitude; }
    public String getSystemId() { return systemId; }
    public String getSystemName() { return systemName; }
    public String getDeviceSn() { return deviceSn; }
    public String getDeviceName() { return deviceName; }
    public String getDeviceConfigKey() { return deviceConfigKey; }
    public String getPointSn() { return pointSn; }
    public String getPointName() { return pointName; }
    public String getPointValueType() { return pointValueType; }
    public String getPointUnit() { return pointUnit; }
    public String getMsgType() { return msgType; }
    public Date getCreatedTime() { return createdTime; }
    public Date getUpdateTime() { return updateTime; }
}