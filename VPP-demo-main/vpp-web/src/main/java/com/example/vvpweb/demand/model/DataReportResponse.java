package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DataReportResponse implements Serializable {
    private static final long serialVersionUID = 8652734764464142827L;

    @ApiModelProperty("DataReportResponse")
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

    @ApiModelProperty("要取消的报告清单")
    private List<CancelReportRequest> cancelReport;
}
