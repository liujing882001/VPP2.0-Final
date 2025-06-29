package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@lombok.Data
public class Data implements Serializable {
    private static final long serialVersionUID = -8864312600899121722L;

    @ApiModelProperty("值")
    private Double value;

    @ApiModelProperty("值的采样时间")
    private String timestamp;

    @ApiModelProperty("质量标志,默认为空")
    private DataQuality quality;
}
