package com.example.vvpweb.chinasouthernpower.model;

import com.example.vvpservice.chinasouthernpower.model.RData;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class HistoryData implements Serializable {

    @ApiModelProperty(value = "工厂/园区/大楼唯一标识", required = true)
    private String resourceId;

    @ApiModelProperty(value = "采 集 数 据 时 间 ：yyyy-MM-dd HH:mm:ss", required = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    private RData data;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public RData getData() {
        return data;
    }

    public void setData(RData data) {
        this.data = data;
    }
}
