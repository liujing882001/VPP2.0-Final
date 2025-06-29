package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ElectricityInfoModel {
    private BigDecimal price;
    private String property;

    // 构造方法
    public ElectricityInfoModel(BigDecimal price, String property) {
        this.price = price;
        this.property = property;
    }
}
