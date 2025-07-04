package com.example.vvpservice.chinasouthernpower.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceInfoModel implements Serializable {

    /**
     *deviceSn 设备编号
     */
    private String deviceSn;
    /**
     * deviceRatedPower 冷机设备额定功率
     */
    private double deviceRatedPower;

    /**
     * deviceNowTemperature 设备当前冷冻出水温度
     */
    private double deviceNowTemperature;

    /**
     * actualDeclare 实际调控负荷
     */
    private double actualDeclare;

    /**
     * 户号
     */
    private String noHouseholds;

    // Manual getters and setters to ensure compilation
    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public double getDeviceRatedPower() { return deviceRatedPower; }
    public void setDeviceRatedPower(double deviceRatedPower) { this.deviceRatedPower = deviceRatedPower; }
    public double getDeviceNowTemperature() { return deviceNowTemperature; }
    public void setDeviceNowTemperature(double deviceNowTemperature) { this.deviceNowTemperature = deviceNowTemperature; }
    public void setDeviceNowTemperature(float deviceNowTemperature) { this.deviceNowTemperature = deviceNowTemperature; }
    public double getActualDeclare() { return actualDeclare; }
    public void setActualDeclare(double actualDeclare) { this.actualDeclare = actualDeclare; }
    public String getNoHouseholds() { return noHouseholds; }
    public void setNoHouseholds(String noHouseholds) { this.noHouseholds = noHouseholds; }
}
