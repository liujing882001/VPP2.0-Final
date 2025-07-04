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

    // Manual getters and setters to ensure compilation
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public float getDeviceSetTemperature() { return deviceSetTemperature; }
    public void setDeviceSetTemperature(float deviceSetTemperature) { this.deviceSetTemperature = deviceSetTemperature; }
    public void setDeviceSetTemperature(long deviceSetTemperature) { this.deviceSetTemperature = deviceSetTemperature; }
}
