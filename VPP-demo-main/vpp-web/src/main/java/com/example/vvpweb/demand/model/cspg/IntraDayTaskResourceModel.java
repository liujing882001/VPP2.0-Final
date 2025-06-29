package com.example.vvpweb.demand.model.cspg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class IntraDayTaskResourceModel {

    @ApiModelProperty("唯一标识")
    private String resourceId;
    @ApiModelProperty("开始时间 yyyy-MM-dd HH:mm:ss")
    private String startTime;
    @ApiModelProperty("结束时间 yyyy-MM-dd HH:mm:ss")
    private String endTime;
    @ApiModelProperty("功率目标值（kW）")
    private Double targetPower;

}
