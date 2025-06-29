package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

@Data
public class SamplingRate implements Serializable {
    private static final long serialVersionUID = 6527382615247042170L;

    @ApiModelProperty("只有在数据发生变化时报送,通常用于开关量变化")
    private Boolean onChange;

    @ApiModelProperty("数据报送的最小采样周期")
    private String minPeriod;

    @ApiModelProperty("数据报送的最大采样周期")
    private String maxPeriod;
}
