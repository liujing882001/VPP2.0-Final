package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class CancelReportRequest implements Serializable {
    private static final long serialVersionUID = 5581824288904097873L;

    @ApiModelProperty("DrRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnId;

    @ApiModelProperty("报告取消后当前报告是否返回")
    private Boolean reportToFollow;

    @ApiModelProperty("要取消的报告请求ID")
    private List<String> reportRequestID;
}
