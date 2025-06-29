package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PointCurveData implements Serializable {
    private static final long serialVersionUID = 970442933624842460L;

    @ApiModelProperty("不规则曲线,数据间隔时间不规则")
    private IrregularCurve irregular;

    @ApiModelProperty("不规则曲线,数据间隔时间不规则")
    private Integer rID;
}
