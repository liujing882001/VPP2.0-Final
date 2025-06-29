package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RegisterReportRequest implements Serializable {
    private static final long serialVersionUID = 1700465120197739253L;

    @ApiModelProperty("RegisterReportRequest")
    private String root;

    @ApiModelProperty("协议版本(继承自DrRequest)")
    private Integer version;

    @ApiModelProperty("请求ID(继承自DrRequest)")
    private String requestID;

    @ApiModelProperty("下位节点的ID(继承自DrRequest)")
    private String dnID;

    @ApiModelProperty("报告请求ID")
    private String reportRequestID;

    @ApiModelProperty("元数据报告")
    private List<MetaDataReport> report;
}
