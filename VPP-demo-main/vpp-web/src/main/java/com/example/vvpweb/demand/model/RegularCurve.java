package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Data
public class RegularCurve implements Serializable {
    private static final long serialVersionUID = -695199026903288092L;

    @ApiModelProperty("开始时间")
    private String dtstart;

    @ApiModelProperty("采样间隔")
    private String period;

    @ApiModelProperty("存储值的数组")
    private List<Double> array;
}
