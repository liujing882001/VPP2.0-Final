package com.example.vvpweb.flexibleresourcemanagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "UseEnergyModel", description = "智能调度负荷/碳减排量/顶峰能力（kW）")
public class UseEnergyModel implements Serializable {

    @ApiModelProperty(value = "负荷调度统计_智能调度负荷", name = "intelligentScheduling", required = true)
    private double intelligentScheduling = 0;

    @ApiModelProperty(value = "负荷调度统计_碳减排量(t)", name = "carbonEmissionReduction", required = true)
    private double carbonEmissionReduction = 0;

    @ApiModelProperty(value = "负荷调度统计_顶峰能力（kWh）", name = "peakCapacity", required = true)
    private double peakCapacity = 0;

}
