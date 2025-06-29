package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class EnergyStorageProperty {
    //时间范围(一个小时)
    private String timeFrame;

    //属性尖，峰，平，谷
    private String property;

    //每小时价格
    private BigDecimal priceHour;
}
