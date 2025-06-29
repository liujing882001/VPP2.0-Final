package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class DeviceStatusReportRequest implements Serializable {
    private static final long serialVersionUID = -2653172333407398520L;

    @ApiModelProperty("DeviceStatusReportRequest")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("请求ID,由请求方生成,同一个DN应保证其唯一性,以便与响应相对应")
    private String requestID;

    @ApiModelProperty("下位节点的ID")
    private String dnId;

    @ApiModelProperty("报告请求ID")
    private String reportRequestID;

    @ApiModelProperty("创建报告的时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdDateTime;

    @ApiModelProperty("设备状态")
    private List<DeviceStatus> deviceStatus;
}
