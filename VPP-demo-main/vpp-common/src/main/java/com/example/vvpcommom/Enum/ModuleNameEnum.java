package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ModuleNameEnum {
    storage_tactics("storage_tactics", "储能充放电策略"),
    tactics("tactics", "可调负荷运行策略"),
    response_task("response_task", "需求响应"),
    demand_response_task("DemandResponse","南网需求响应"),
    service_task("service_task", "辅助服务");

    private String moduleId;
    private String moduleName;
}
