package com.example.vvpweb.chinasouthernpower.model;

import io.swagger.annotations.ApiModelProperty;

public class AgentMerchantsRequest {

    @ApiModelProperty(value = "系统时间戳", required = true)
    private String systemTime;

    @ApiModelProperty(value = "负荷聚合商唯一标识", required = true)
    private String creditCode;

    @ApiModelProperty(value = "事件类型", required = true, allowableValues = "agentMerchants")
    private String eventType;

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getCreditCode() {
        return creditCode;
    }

    public void setCreditCode(String creditCode) {
        this.creditCode = creditCode;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
}
