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
}
