package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 数据质量类型
 */
@Getter
@AllArgsConstructor
public enum DataQuality {
    good("完整"),
    failure("失效"),
    oldData("旧数据"),
    outOfRange("超出范围"),
    overFlow("溢出"),
    suspect("可疑的"),
    estimatorReplaced("估计值(非实测,用于代替监测值)"),
    operatorBlocked("操作受限"),
    oscillatory("震荡"),
    test("测试数据");

    private String desc;
}
