package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 实时数据报告请求
 */
@Data
@EqualsAndHashCode(callSuper = false) // 添加这个注解
public class MomentDataReportRequest extends DrRequest implements Serializable {
    private static final long serialVersionUID = -306227142735592403L;

    @ApiModelProperty("报告请求ID")
    private String reportRequestID;

    @ApiModelProperty("创建报告的时间")
    private String createdDateTime;

    @ApiModelProperty("测点数据")
    private List<PointData> pointData;
}