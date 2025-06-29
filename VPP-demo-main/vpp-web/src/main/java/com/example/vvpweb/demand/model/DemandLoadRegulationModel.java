package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Optional;


@Data
public class DemandLoadRegulationModel {

    @ApiModelProperty(value = "任务id", required = false)
    private String respId;

    @ApiModelProperty(value = "节点id", required = false)
    private String nodeId;

    @ApiModelProperty(value = "系统id", required = false)
    private String systemId;

    @ApiModelProperty("响应时段(开始)")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Optional<Date> sTime;

    @ApiModelProperty("响应时段(结束)")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Optional<Date> eTime;

    @ApiModelProperty("开始时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Optional<Date> rsTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Optional<Date> reTime;

    @ApiModelProperty("时间点")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Optional<Date> regulationTime;

    @ApiModelProperty(value = "负荷调节值", required = false)
    private String loadRegulationValue;

}
