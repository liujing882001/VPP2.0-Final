package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DemandStatisticsModel {

    @ApiModelProperty("参与需求响应次数")
    private Integer demandNum;

    @ApiModelProperty("需求响应总削减负荷电量（kWh）")
    private Double totalCutPower;

    @ApiModelProperty("需求响应总填谷负荷电量（kWh）")
    private Double totalFillPower;

    @ApiModelProperty("平均申报负荷（kW/次）")
    private Double avgDeclareLoad;

    @ApiModelProperty("平均实际响应负荷（kW/次）")
    private Double avgActualLoad;
    @ApiModelProperty("实际负荷响应率（%）")
    private Double actualComplianceRate;
    @ApiModelProperty("需求响应总收益（元）")
    private Double totalProfit;

    @ApiModelProperty("削峰总收益（元）")
    private Double totalCutProfit;

    @ApiModelProperty("填谷总收益（元）")
    private Double totalFillProfit;

    @ApiModelProperty("容量总收益（元）")
    private Double totalVolumeProfit;

    @ApiModelProperty("用户收益")
    private Double userProfit;

    @ApiModelProperty("平台收益")
    private Double platformProfit;

    @ApiModelProperty("户均收益（元/户）")
    private Double avgProfit;

    @ApiModelProperty("户均用户收益（元/户）")
    private Double avgUserProfit;

    @ApiModelProperty("户均平台收益（元/户）")
    private Double avgPlatformProfit;

    @ApiModelProperty("负荷曲线")
    private List<Map<String, Object>> list;

}
