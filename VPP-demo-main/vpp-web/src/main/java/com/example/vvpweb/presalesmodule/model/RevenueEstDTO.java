package com.example.vvpweb.presalesmodule.model;

import lombok.Data;


@Data
public class RevenueEstDTO {
    private Double firstPeakValleyIncome;
    private Double batteryReplRatio;
    private Double batteryDegCoeff;
    private Double customerShare;
    private Double platformRate;
    private Double designCapacity;
    private Double purchasePrice;
    private Double mainTRatio5Y;
    private Double mainTRatio5_10Y;
    private Double mainTRatio10_25Y;
    private Double insuranceRate;
    private Double equipmentCostRatio;
    private Double engineeringCostRatio;
    private Integer assessPeriod;
    private Integer lifespan;
    public RevenueEstDTO() {
    }
    public RevenueEstDTO(Double firstPeakValleyIncome,FundamentalParameter info) {
        this.firstPeakValleyIncome = firstPeakValleyIncome;
        this.batteryReplRatio = info.getBatteryReplRatio();
        this.batteryDegCoeff = info.getBatteryDegCoeff();
        this.customerShare = info.getCustomerShare();
        this.platformRate = info.getPlatformRate();
        this.designCapacity = info.getDesignCapacity();
        this.purchasePrice = info.getPurchasePrice();
        this.mainTRatio5Y = info.getMainTRatio5Y();
        this.mainTRatio5_10Y = info.getMainTRatio5_10Y();
        this.mainTRatio10_25Y = info.getMainTRatio10_25Y();
        this.insuranceRate = info.getInsuranceRate();
        this.equipmentCostRatio = info.getEquipmentCostRatio();
        this.engineeringCostRatio = info.getEngineeringCostRatio();
        this.assessPeriod = info.getAssessPeriod();
        this.lifespan = info.getLifespan();
    }
}
