package com.example.vvpweb.tradepower.model;

import lombok.Data;

@Data
public class SchedulingStrategyInfo {
    private String time;

    //索引
    private Integer index;

    //充放电策略类型
    private String type;

    //功率
    private Double power;
}
