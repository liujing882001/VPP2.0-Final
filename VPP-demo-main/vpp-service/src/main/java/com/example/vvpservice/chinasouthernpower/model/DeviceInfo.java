package com.example.vvpservice.chinasouthernpower.model;

import java.io.Serializable;

@lombok.Data
public class DeviceInfo implements Serializable {

    /**
     *deviceSn 设备编号
     */
    private String deviceSn;
    /**
     * deviceRatedPower 设备额定功率
     */
    private double deviceRatedPower;
    /**
     * freezingWaterInletTemperature 冷冻进水温度
     */
    private float inletTemperature;

    /**
     * deviceNowTemperature 当前冷冻出水温度
     */
    private float outletTemperature;

    /**
     * freezingWaterOutletTemperatureSetting 设备当前设定的冷冻出水温度
     */
    private float outletTemperatureSetting;

    // 手动添加缺失的getter/setter方法以确保编译通过
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public double getDeviceRatedPower() { return deviceRatedPower; }
    public void setDeviceRatedPower(double deviceRatedPower) { this.deviceRatedPower = deviceRatedPower; }
    public float getInletTemperature() { return inletTemperature; }
    public void setInletTemperature(float inletTemperature) { this.inletTemperature = inletTemperature; }
    public void setInletTemperature(int inletTemperature) { this.inletTemperature = inletTemperature; }
    public float getOutletTemperature() { return outletTemperature; }
    public void setOutletTemperature(float outletTemperature) { this.outletTemperature = outletTemperature; }
    public void setOutletTemperature(int outletTemperature) { this.outletTemperature = outletTemperature; }
    public float getOutletTemperatureSetting() { return outletTemperatureSetting; }
    public void setOutletTemperatureSetting(float outletTemperatureSetting) { this.outletTemperatureSetting = outletTemperatureSetting; }
    public void setOutletTemperatureSetting(int outletTemperatureSetting) { this.outletTemperatureSetting = outletTemperatureSetting; }
}
