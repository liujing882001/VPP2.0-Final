package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class QueryEventResponse implements Serializable {
    private static final long serialVersionUID = 6760399840671575599L;

    @ApiModelProperty("QueryEventResponse")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("响应代码")
    private Integer code;

    @ApiModelProperty("错误原因")
    private String description;

    @ApiModelProperty("请求ID")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnID;

    @ApiModelProperty("事件信息")
    private List<Event> events;
}
