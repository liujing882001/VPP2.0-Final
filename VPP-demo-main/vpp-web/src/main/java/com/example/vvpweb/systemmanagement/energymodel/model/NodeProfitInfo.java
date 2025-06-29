package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

@Data
public class NodeProfitInfo {
    //尖
    private double priceHigh;
    //峰
    private double pricePeak;
    //平
    private double priceStable;
    //谷
    private double priceLow;
}
