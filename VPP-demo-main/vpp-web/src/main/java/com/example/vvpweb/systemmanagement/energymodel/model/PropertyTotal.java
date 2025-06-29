package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PropertyTotal {
    private BigDecimal priceHigh;
    private BigDecimal pricePeak;
    private BigDecimal priceStable;
    private BigDecimal priceLow;
//    private BigDecimal priceRavine;
}
