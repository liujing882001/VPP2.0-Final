package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class UpdateStorageEnergyStrategyModel implements Serializable {

    private String id;

    /**
     * 属性 尖，峰，平，谷
     */
    private String property;

    /**
     * 每小时价格
     */
    private BigDecimal priceHour;

    /**
     * 充放电策略
     */
    private String strategy;
//    private String nodeId;
//    private String systemId;
//    private String timeScope;
}
