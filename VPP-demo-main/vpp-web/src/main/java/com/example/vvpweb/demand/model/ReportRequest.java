package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReportRequest implements Serializable {
    private static final long serialVersionUID = 2991986753238509672L;

    @ApiModelProperty("报告请求ID")
    private String reportRequestID;

    @ApiModelProperty("报告样式,当请求元数据时忽略")
    private List<ReportSpecifier> reportSpecifier;
}
