package com.example.vvpservice.revenue.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RAInfoDTO {
    private String nodeId;
    private String time;
    private BigDecimal Dynamic;
    private BigDecimal Fixed;
}
