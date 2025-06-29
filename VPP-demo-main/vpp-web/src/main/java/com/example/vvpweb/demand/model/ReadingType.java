package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReadingType {
    Direct_Read("直接读数(常用于电表)"),
    Net("净值"),
    Allocated("分配值"),
    Estimated("估计值"),
    Summed("求和"),
    Derived("推断值"),
    Mean("平均值"),
    Peak("最高值"),
    Hybrid("混合值"),
    Contract("合同值"),
    Projected("预测值"),
    RMS("均方根"),
    notApplicable("不适用");

    private String desc;
}
