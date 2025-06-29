package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class DemandTaskMqttModel implements Serializable {

    @ApiModelProperty("是否在物管已超时 0-未超时 1-已超时")
    private Integer flag;

    @ApiModelProperty("指令编码")
    private String cmdCode;

    @ApiModelProperty("下发时间")
    private String issuedTime;

    /**
     * 1-日前 2-小时 3-分钟 4-秒级
     * 1.日前 2.日内 3.实时
     */
    @ApiModelProperty("调节编码 1.日前2.日内 3.实时")
    private String adjustCode;

    @ApiModelProperty("调控类型 1.削峰 2.填谷")
    private String adjustType;

    @ApiModelProperty("执行开始时间")
    private String startTime;

    @ApiModelProperty("执行结束时间")
    private String endTime;

    @ApiModelProperty("调控命令集")
    private Object controls;
}
