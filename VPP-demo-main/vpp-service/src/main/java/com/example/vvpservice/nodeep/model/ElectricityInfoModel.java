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

    // 手动添加getter和setter方法以确保编译通过
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
}
