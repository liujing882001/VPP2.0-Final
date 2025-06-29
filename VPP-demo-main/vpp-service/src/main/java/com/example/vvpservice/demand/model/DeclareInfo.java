/**
 * Copyright 2023 json.cn
 */
package com.example.vvpservice.demand.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeclareInfo implements Serializable {

    /**
     * 需求唯一标识
     */
    private String demandId;
    /**
     * 可响应负荷
     */
    private Double availableValue;
    /**
     * 响应价格
     */
    private Double demandPrice;
    /**
     * 电表户号
     */
    private String meterAccountNumber;
}