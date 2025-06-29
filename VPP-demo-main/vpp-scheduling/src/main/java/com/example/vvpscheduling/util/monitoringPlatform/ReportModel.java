package com.example.vvpscheduling.util.monitoringPlatform;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class ReportModel {
    @ApiModelProperty(value = "root", required = false)
    private String root;
    @ApiModelProperty(value = "version", required = false)
    private Integer version;
    @ApiModelProperty(value = "code", required = false)
    private Integer code;
    @ApiModelProperty(value = "description", required = false)
    private String description;
    @ApiModelProperty(value = "requestID", required = false)
    private String requestID;
    @ApiModelProperty(value = "dnID", required = false)
    private String dnID;
    @ApiModelProperty(value = "unID", required = false)
    private String unID;
    @ApiModelProperty(value = "registrationID", required = false)
    private String registrationID;
    @ApiModelProperty(value = "pollFreq", required = false)
    private String pollFreq;
    @ApiModelProperty(value = "transport", required = false)
    private List<String> transport;
    @ApiModelProperty(value = "serviceSpecific", required = false)
    private List<String> serviceSpecific;
    @ApiModelProperty(value = "pendingReports", required = false)
    private Map<String,Object> pendingReports;
    @ApiModelProperty(value = "reportRequest", required = false)
    private List<Map<String,Object>> reportRequest;
    @ApiModelProperty(value = "events", required = false)
    private List<Map<String,Object>> events;

}
