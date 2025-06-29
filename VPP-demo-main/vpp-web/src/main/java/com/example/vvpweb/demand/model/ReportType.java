package com.example.vvpweb.demand.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ReportType {
    reading("读数"),
    usage("用量"),
    demand("需求"),
    setPoint("设定点"),
    deltaUsage("用量偏差"),
    deltaSetPoint("设定点偏差"),
    deltaDemand("需求偏差"),
    baseline("基线"),
    deviation("偏离度"),
    avgUsage("平均用量"),
    avgDemand("平均需求"),
    operatingState("运行状态"),
    upRegulationCapacityAvailable("上调可用容量"),
    downRegulationCapacityAvailable("下调可用容量"),
    regulationSetpoint("设定值规则"),
    storedEnergy("储能值"),
    targetEnergyStorage("目标储能值"),
    availableEnergyStorage("可用储能值"),
    price("价格"),
    level("等级"),
    powerFactor("功率因数"),
    percentUsage("用量百分比"),
    percentDemand("需求百分比"),
    resourceStatus("扩展资源状态");

    private String desc;
}
