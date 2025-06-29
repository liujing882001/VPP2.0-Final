package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


@Data
public class DemandForecastResponse implements Serializable {

    @ApiModelProperty("实际负荷值")
    private List<Long> realValue;

    @ApiModelProperty("基线负荷值")
    private List<Long> baselineLoadValue;

    @ApiModelProperty("预测负荷")
    private List<Long> forecastLoad;

    @ApiModelProperty("预测调节负荷")
    private List<Long> forecastRegulationLoad;

    @ApiModelProperty("预测调节后负荷")
    private List<Long> forecastLoadAfterRegulation;

    @ApiModelProperty("时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private Date timeStamp;

    @ApiModelProperty("是否调整")
    private boolean isAdjust;

}
