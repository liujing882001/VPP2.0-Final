package com.example.gateway.model;

import lombok.Data;

@Data
public class Every15MinuteModel {

    /**
     * 充电 -1
     * 放电 1
     * 待机 0
     */
    private int strategyType = 0;

    /**
     * 对应充放电的输入输出功率
     */
    private int strategyLoad = 0;

}
