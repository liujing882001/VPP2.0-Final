package com.example.vvpscheduling.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RAInfoVO {
    private String time;
    private BigDecimal Dynamic;
    private BigDecimal Fixed;

    public RAInfoVO(){}
    public RAInfoVO(String time,BigDecimal Dynamic,BigDecimal Fixed){
        this.time = time;
        this.Dynamic = Dynamic;
        this.Fixed = Fixed;

    }
}
