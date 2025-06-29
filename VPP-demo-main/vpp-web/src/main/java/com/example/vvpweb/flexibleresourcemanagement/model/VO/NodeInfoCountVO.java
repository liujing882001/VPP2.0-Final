package com.example.vvpweb.flexibleresourcemanagement.model.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value = "NodeInfoCountVO", description = "当日资源情况-节点信息模型-根据项目节点列表")
public class NodeInfoCountVO {
    @ApiModelProperty(value = "当日资源情况_储能电站数量", name = "storageEnergyCount", required = true)
    private int storageEnergyCount = 0;

    @ApiModelProperty(value = "当日资源情况_储能电站容量（kWh）", name = "storageEnergyCapacity", required = true)
    private double storageEnergyCapacity = 0;

    @ApiModelProperty(value = "当日资源情况_充电功率（kW）", name = "storageEnergyPower", required = true)
    private double storageEnergyPower = 0;

    @ApiModelProperty(value = "当日资源情况_光伏电站数量", name = "pvCount", required = true)
    private int pvCount = 0;

    @ApiModelProperty(value = "当日资源情况_装机容量之和(kW)", name = "pvCapacity", required = true)
    private double pvCapacity = 0;

    @ApiModelProperty(value = "当日资源情况_负荷节点数量", name = "loadCount", required = true)
    private int loadCount = 0;
    @ApiModelProperty(value = "当日资源情况_接入负荷之和（kW）", name = "loadJieRu", required = true)
    private double loadJieRu = 0;
    @ApiModelProperty(value = "当日资源情况_可调负荷之和（kW）", name = "loadKeTiao", required = true)
    private double loadKeTiao = 0;
    @ApiModelProperty(value = "智算中心", name = "zszx", required = true)
    private int zszx = 0;
    @ApiModelProperty(value = "智算中心", name = "zszxPer", required = true)
    private double zszxPer = 0.0;

    @ApiModelProperty(value = "智能交通", name = "znjt", required = true)
    private int znjt = 0;
    @ApiModelProperty(value = "智能交通", name = "znjtPer", required = true)
    private double znjtPer = 0.0;

    @ApiModelProperty(value = "智能制造", name = "znzz", required = true)
    private int znzz = 0;
    @ApiModelProperty(value = "智能制造", name = "znzzPer", required = true)
    private double znzzPer = 0.0;

    @ApiModelProperty(value = "楼宇园区", name = "lyyq", required = true)
    private int lyyq = 0;
    @ApiModelProperty(value = "楼宇园区", name = "lyyqPer", required = true)
    private double lyyqPer = 0.0;

    @ApiModelProperty(value = "当日风场资源情况_接入负荷之和（kW）", name = "windCount", required = true)
    private int windCount = 0;
    @ApiModelProperty(value = "当日风场资源情况_可调负荷之和（kW）", name = "windCapacity", required = true)
    private double windCapacity = 0;
}
