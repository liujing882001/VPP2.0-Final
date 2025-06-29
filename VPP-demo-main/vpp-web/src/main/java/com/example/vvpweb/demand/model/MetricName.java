package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MetricName {
    AP("有功功率"),
    AP_E("有功电能"),
    AP_PE("发电量"),
    REGULATE_UP("实时上调能力(KW)"),
    REGULATE_DOWN("实时下调能力(KW)"),
    RESPONSE_TIME("实时响应时间(S)"),
    CONTROL_RATE("实时爬坡速度(KW/min)"),
    DURATION("实时持续时间(min)"),
    F_REGULATE_UP("日前申报上调能力(KW)"),
    F_REGULATE_DOWN("日前申报下调能力(KW)"),
    F_RESPONSE_TIME("日前申报响应时间(s)"),
    F_CONTROL_RATE("日前申报爬坡速度(KW/min)"),
    F_DURATION("日前申报持续时间(min)");

    private String desc;
}
