package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PollResponse implements Serializable {
    private static final long serialVersionUID = -924663667768450738L;

    @ApiModelProperty("DrResponse")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("响应代码")
    private Integer code;

    @ApiModelProperty("错误原因")
    private String reason;

    @ApiModelProperty("下位节点的ID")
    private String dnID;
}
