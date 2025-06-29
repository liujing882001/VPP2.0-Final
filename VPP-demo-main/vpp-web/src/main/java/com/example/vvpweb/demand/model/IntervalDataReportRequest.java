package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class IntervalDataReportRequest implements Serializable {
    private static final long serialVersionUID = 5073061288506818055L;

    @ApiModelProperty("DrRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnID;

    @ApiModelProperty("报告请求ID")
    private String reportRequestID;

    @ApiModelProperty("创建报告的时间")
    private String createdDateTime;

    @ApiModelProperty("曲线数据")
    private List<PointCurveData> pointCurveData;
}
