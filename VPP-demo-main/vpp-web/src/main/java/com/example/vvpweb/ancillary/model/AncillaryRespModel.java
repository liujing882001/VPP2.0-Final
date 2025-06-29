package com.example.vvpweb.ancillary.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AncillaryRespModel {

    @ApiModelProperty("节点名称")
    private String nodeName;

    @ApiModelProperty("调节负荷(kW)")
    private Double deviceRatedLoad;

    @ApiModelProperty("调节电量(kW)")
    private Double regulatePower;

    @ApiModelProperty("占比%")
    private Double powerRate;

}
