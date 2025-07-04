package com.example.vvpcommom.Enum;

public enum ModuleNameEnum {
    storage_tactics("storage_tactics", "储能充放电策略"),
    tactics("tactics", "可调负荷运行策略"),
    response_task("response_task", "需求响应"),
    demand_response_task("DemandResponse","南网需求响应"),
    service_task("service_task", "辅助服务");

    private String moduleId;
    private String moduleName;

    ModuleNameEnum(String moduleId, String moduleName) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
    }

    public String getModuleId() {
        return moduleId;
    }

    public String getModuleName() {
        return moduleName;
    }
}
