package com.example.vvpweb.systemmanagement.energymodel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "storageEnergyBaseInfoResponse", description = "储能基本信息")
@Data
public class StorageEnergyBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;

    /**
     * 储能电站功率 kw
     */
    @ApiModelProperty(value = "储能电站功率 kw", name = "storageEnergyLoad", required = true)
    private double storageEnergyLoad;

    /**
     * 储能电池容量 kwh
     */
    @ApiModelProperty(value = "储能电池容量", name = "storageEnergyCapacity", required = true)
    private double storageEnergyCapacity;

    /**
     * 最大可充电量百分比
     */
    @ApiModelProperty(value = "最大可充电量百分比", name = "maxChargePercent", required = true)
    private double maxChargePercent;

    /**
     * 最小放电量百分比
     */
    @ApiModelProperty(value = "最小放电量百分比", name = "minDischargePercent", required = true)
    private double minDischargePercent;
    /**
     * 充电设备编码
     */
    @ApiModelProperty(value = "充电设备编码", name = "chargingDeviceSn", required = true)
    private String chargingDeviceSn;

    /**
     * 放电设备编码
     */
    @ApiModelProperty(value = "放电设备编码", name = "dischargingDeviceSn", required = true)
    private String dischargingDeviceSn;

    /**
     * 电池状态监控设备编码
     */
    @ApiModelProperty(value = "电池状态监控设备编码", name = "batteryStatusDeviceSn", required = true)
    private String batteryStatusDeviceSn;

}
