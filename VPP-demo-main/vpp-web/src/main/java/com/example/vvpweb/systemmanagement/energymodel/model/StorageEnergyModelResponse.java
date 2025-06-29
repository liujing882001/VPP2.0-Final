package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergyModelResponse implements Serializable {

    private String nodeId;
    private String systemId;

    //充电设备编码
    private String charging_device_sn;
    //放电设备编码
    private String discharging_device_sn;
    //电池状态监控设备编码u
    private String battery_status_device_sn;

}
