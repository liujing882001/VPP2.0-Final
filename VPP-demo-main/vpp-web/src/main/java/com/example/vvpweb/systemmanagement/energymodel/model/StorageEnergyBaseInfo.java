package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergyBaseInfo implements Serializable {


    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 储能电站功率 kw
     */
    private double storageEnergyLoad;

    /**
     * 储能电池容量 kwh
     */
    private double storageEnergyCapacity;
    /**
     * 最大可充电量百分比
     */
    private double maxChargePercent;

    /**
     * 最小放电量百分比
     */
    private double minDischargePercent;

    /**
     * 充电设备编码
     */
    private String chargingDeviceSn;

    /**
     * 放电设备编码
     */
    private String dischargingDeviceSn;

    /**
     * 电池状态监控设备编码
     */
    private String batteryStatusDeviceSn;

}
