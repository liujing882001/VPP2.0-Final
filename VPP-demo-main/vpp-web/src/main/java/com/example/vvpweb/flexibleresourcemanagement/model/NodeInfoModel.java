package com.example.vvpweb.flexibleresourcemanagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "NodeInfoModel", description = "当日资源情况-节点信息模型")
public class NodeInfoModel implements Serializable {


    @ApiModelProperty(value = "当日资源情况_储能电站数量", name = "storageEnergyCount", required = true)
    private double storageEnergyCount = 0;

    @ApiModelProperty(value = "当日资源情况_储能电站容量（kWh）", name = "storageEnergyCapacity", required = true)
    private double storageEnergyCapacity = 0;

    @ApiModelProperty(value = "当日资源情况_充电功率（kW）", name = "storageEnergyPower", required = true)
    private double storageEnergyPower = 0;

    @ApiModelProperty(value = "当日资源情况_光伏电站数量", name = "pvCount", required = true)
    private double pvCount = 0;

    @ApiModelProperty(value = "当日资源情况_装机容量之和(kW)", name = "pvCapacity", required = true)
    private double pvCapacity = 0;

    @ApiModelProperty(value = "当日资源情况_负荷节点数量", name = "loadCount", required = true)
    private double loadCount = 0;

    @ApiModelProperty(value = "当日资源情况_接入负荷之和（kW）", name = "loadJieRu", required = true)
    private double loadJieRu = 0;
    @ApiModelProperty(value = "当日资源情况_可调负荷之和（kW）", name = "loadKeTiao", required = true)
    private double loadKeTiao = 0;
}
