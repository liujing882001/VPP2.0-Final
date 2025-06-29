package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.Date;

@Data
public class QueryEventRequest implements Serializable {
    private static final long serialVersionUID = -4840826665380047455L;

    @ApiModelProperty("QueryEventRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnID;

    @ApiModelProperty("返回的事件数量限制")
    private Integer replyLimit;

    @ApiModelProperty("要查询的事件开始事件")
    private String dtstart;

    @ApiModelProperty("要查询的事件时间范围")
    private String duration;
}
