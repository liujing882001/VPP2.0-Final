package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DemandRespModel {

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("实际响应负荷(kW)")
    private Double realLoad;

    @ApiModelProperty("实际响应电量(kW)")
    private Double realPower;

    @ApiModelProperty("占比%")
    private Double powerRate;

}
