package com.example.vvpdomain.view;

import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author zph
 * @description 储能节点基本信息视图
 * @date 2022-08-12
 */
@Entity
@Getter
@Table(name = "storage_energy_node_info_view")
@EntityListeners(AuditingEntityListener.class)
public class StorageEnergyNodeInfoView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

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
     * 电站名称id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;

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
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;


    /**
     * 电站状态 在线离线
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 充电设备编码
     */
    @Column(name = "charging_device_sn")
    private String chargingDeviceSn;

    /**
     * 放电设备编码
     */
    @Column(name = "discharging_device_sn")
    private String dischargingDeviceSn;

    /**
     * 电池状态监控设备编码
     */
    @Column(name = "battery_status_device_sn")
    private String batteryStatusDeviceSn;

    /**
     * 储能装机容量
     */
    @Column(name = "storage_energy_capacity")
    private Double storageEnergyCapacity;

    /**
     * 储能电站功率
     */
    @Column(name = "storage_energy_load")
    private Double storageEnergyLoad;
}