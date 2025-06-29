package com.example.vvpdomain.entity;

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
 * @description 储能配置基本信息
 * @date 2022-07-24
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_storage_energy_base_info")
public class CfgStorageEnergyBaseInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=节点id系统id
     */
    @Column(name = "id")
    private String id;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;


    /**
     * 系统数据类型（baseInfo，strategy，shareProportion）
     */
    @Column(name = "data_type")
    private String dataType;

    /**
     * 储能电站功率 kw
     */
    @Column(name = "storage_energy_load")
    private double storageEnergyLoad;

    /**
     * 储能电池容量 kwh
     */
    @Column(name = "storage_energy_capacity")
    private double storageEnergyCapacity;

    /**
     * 最大可充电量百分比
     */
    @Column(name = "max_charge_percent")
    private double maxChargePercent;

    /**
     * 最小放电量百分比
     */
    @Column(name = "min_discharge_percent")
    private double minDischargePercent;

    /**
     * 充放电策略开始时间 yyyy-MM
     */
    @Column(name = "strategy_start_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date strategyStartTime;
    /**
     * 充放电策略年限
     */
    @Column(name = "strategy_expiry_date")
    private int strategyExpiryDate;


    /**
     * 分层比例开始时间 yyyy
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @Column(name = "share_proportion_start_time")
    private Date shareProportionStartTime;
    /**
     * 分层比例年限
     */
    @Column(name = "share_proportion_expiry_date")
    private int shareProportionExpiryDate;


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
     * 同步储能信息配置
     */
    @Column(name = "syn_storage_energy_cfg")
    private String synStorageEnergyCfg;


    /**
     * 下发储能信息配置
     */
    @Column(name = "distribute_storage_energy_cfg")
    private String distributeStorageEnergyCfg;

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

    public CfgStorageEnergyBaseInfo() {
    }

}