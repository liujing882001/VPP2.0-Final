package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.time.Duration;
import java.util.List;

@Data
public class CreateRegistrationResponse implements Serializable {
    private static final long serialVersionUID = 7748295379332568472L;

    @ApiModelProperty("CreateRegistrationResponse")
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

    @ApiModelProperty("上位节点的ID")
    private String unID;

    @ApiModelProperty("注册ID")
    private String registrationID;

    @ApiModelProperty("询问频率")
    private Duration pollFreq;

    @ApiModelProperty("支持的传输协议类型")
    private List<TransportType> transport;

    @ApiModelProperty("支持的服务规范")
    private List<ServiceSpecific> serviceSpecific;
}
