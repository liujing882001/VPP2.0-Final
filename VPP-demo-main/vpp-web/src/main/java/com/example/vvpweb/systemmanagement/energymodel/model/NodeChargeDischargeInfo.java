package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

@Data
public class NodeChargeDischargeInfo {

    private String time;

//    private String endTime;

    //索引
    private Integer index;

    //充放电策略类型
    private String type;

    //功率
    private Double power;

    private Integer policyModel;

}
