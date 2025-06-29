package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Optional;


@Data
public class strategyModel {

    @ApiModelProperty(value = "ids", required = false)
    private List<String> ids;
    @ApiModelProperty(value = "id", required = false)
    private String id;

    @ApiModelProperty(value = "任务id", required = false)
    private String respId;

    @ApiModelProperty(value = "节点id", required = false)
    private String nodeId;

    @ApiModelProperty(value = "节点名称", required = false)
    private String nodeName;

    @ApiModelProperty(value = "系统id", required = false)
    private String systemId;

//    @ApiModelProperty("开始时间")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    private Optional<Date> startTs;
//
//    @ApiModelProperty("结束时间")
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
//    private Optional<Date> endTs;

    @ApiModelProperty("时间点")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Optional<Date> timePoint;

    @ApiModelProperty(value = "策略内容", required = false)
    private String strategyContent;

    @ApiModelProperty(value = "修改前的温度", required = false)
    private String strategyContentBefore;

//    @ApiModelProperty(value = "预测后调节", required = false)
//    private String opt_fx;

}
