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

}
