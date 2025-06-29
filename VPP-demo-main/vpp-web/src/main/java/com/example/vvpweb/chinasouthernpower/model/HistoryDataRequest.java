package com.example.vvpweb.chinasouthernpower.model;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;


public class HistoryDataRequest implements Serializable {
    @ApiModelProperty(value = "系统时间戳", required = true)
    private String systemTime;

    @ExcelProperty(value = "用电户号resourceId")
    @ApiModelProperty(value = "工厂/园区/大楼唯一标识（用电户号）", required = true)
    private String resourceId;

    @ApiModelProperty(value = "事件类型", required = true, allowableValues = "“historyData”")
    private String eventType;

    @ApiModelProperty(value = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String startTime;

    @ApiModelProperty(value = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
    private String endTime;

    public String getSystemTime() {
        return systemTime;
    }

    public void setSystemTime(String systemTime) {
        this.systemTime = systemTime;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}
