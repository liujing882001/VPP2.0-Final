package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

@Data
public class FundamentalParameter {
    //评估年限、更换电池占总成本比例、电池衰减系数、寿命年限、客户分享比例、平台服务费、电站设计容量、
    // 前5年维保比例、5-10年维保比例、10-25年维保比例、保险费率、设备费用占比、工程费用占比
    private Integer assessPeriod;
    private Double batteryReplRatio;
    private Double batteryDegCoeff;
    private Integer lifespan;
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
    private Double designPower;
    private Double usableDepth;
    private Double systemEfficiency;
}
