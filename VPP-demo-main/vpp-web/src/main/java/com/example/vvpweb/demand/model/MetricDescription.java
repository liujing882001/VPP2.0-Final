package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;

@Data
public class MetricDescription implements Serializable {
    private static final long serialVersionUID = -5776176131071491824L;

    @ApiModelProperty("公制名称")
    private MetricName metricName;

    @ApiModelProperty("乘数的倍数")
    private UnitMultiplier multiplier;

    @ApiModelProperty("单位的符号")
    private UnitSymbol symbol;
}
