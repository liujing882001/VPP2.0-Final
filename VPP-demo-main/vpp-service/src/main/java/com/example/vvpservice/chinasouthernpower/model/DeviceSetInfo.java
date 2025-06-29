package com.example.vvpservice.chinasouthernpower.model;

import java.io.Serializable;

/**
 * 户号下参与需求响应设备
 * 设置温度
 */
@lombok.Data
public class DeviceSetInfo implements Serializable {
    /**
     *deviceSn 设备编号
     */
    private String deviceSn;
    /**
     * deviceSetTemperature 设置温度
     */
    private float deviceSetTemperature;
}
