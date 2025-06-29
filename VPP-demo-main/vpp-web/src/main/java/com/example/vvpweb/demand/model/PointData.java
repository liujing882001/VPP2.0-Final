package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 测点数据
 */
@Data
public class PointData implements Serializable {
    private static final long serialVersionUID = -797444039449790011L;

    @ApiModelProperty("数据测点ID")
    private Integer rID;

    @ApiModelProperty("值")
    private Double value;

    @ApiModelProperty("采样时间")
    private String timestamp;

    @ApiModelProperty("质量标志")
    private DataQuality quality;
}
