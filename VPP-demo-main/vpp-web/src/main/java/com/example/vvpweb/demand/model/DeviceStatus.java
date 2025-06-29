package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DeviceStatus implements Serializable {
    private static final long serialVersionUID = -3516914427724243866L;

    @ApiModelProperty("数据测点ID")
    private Integer rID;

    @ApiModelProperty("值")
    private Float value;

    @ApiModelProperty("采样时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    @ApiModelProperty("报告请求ID")
    private DataQuality quality;
}
