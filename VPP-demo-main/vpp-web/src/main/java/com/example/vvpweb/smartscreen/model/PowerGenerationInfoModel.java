package com.example.vvpweb.smartscreen.model;

import lombok.Data;

import java.io.Serializable;
/**
 * 发电信息统计
 */
@Data
public class PowerGenerationInfoModel implements Serializable {

    private double TodayPowerGeneration;
    private double thisYearPowerGeneration;
}
