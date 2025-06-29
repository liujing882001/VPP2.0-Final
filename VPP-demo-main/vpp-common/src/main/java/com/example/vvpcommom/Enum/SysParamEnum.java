package com.example.vvpcommom.Enum;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SysParamEnum {


    DemandResponse(1, "电网省公司需求响应地址"),
    IOTParam(2, "IOT平台"),
    ResourceOverviewNodeTypeOrder(3, "资源概览节点类型排序"),
    SalesPerSquareMeter(4, "节点标准坪效设定"),
    SmartEnergy(5, "第三方智慧能源平台"),
    ThirdPartyEnergyPlat(12, "是否对接第三方能源平台"),

    PeakCapacityParamCfg(6, "顶峰能力参数配置"),

    LoGoCfg(7, "LOGO配置"),

    BaseLineForecastCfg(9, "基线及预测算法配置"),

    DemandResponsePlatFormCfg(10, "各地电网省公司需求平台配置"),

    DemandResponsePriceCfg(11, "自动需求响应价格"),

    BaseLineCfg(8, "基线负荷配置");
    /**
     * key
     */
    private Integer id;
    /**
     * 系统参数字典key描述
     * 1：电网省公司需求响应地址
     * 2：IOT平台
     * 3：资源概览节点类型排序
     * 4：节点标准坪效设定
     * 5：三方智慧能源平台
     */
    private String desc;
}
