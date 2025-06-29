package com.example.vvpdomain.view;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description iot_ts_kv 中以load为维度的
 * 最近一周的切片事实表视图
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "iot_ts_kv_load_lastweek_view")
public class IotTsKvLoadLastWeekView implements Serializable {

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
     * longitude 节点经度
     */
    @Column(name = "longitude")
    private double longitude;

    /**
     * node_post_type
     * 节点归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    @Column(name = "node_post_type")
    private String nodePostType;

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
     * msg_type 消息类型 MSG/ALARM（消息或者报警）
     */
    @Column(name = "msg_type")
    private String msgType;

    /**
     * 数据点单位 电压V  电流 I等等
     */
    @Column(name = "point_unit")
    private String pointUnit;

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

    public IotTsKvLoadLastWeekView() {
    }

}
