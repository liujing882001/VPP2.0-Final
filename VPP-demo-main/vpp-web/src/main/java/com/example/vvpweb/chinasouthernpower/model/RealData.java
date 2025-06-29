package com.example.vvpweb.chinasouthernpower.model;

import com.example.vvpservice.chinasouthernpower.model.RData;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.Date;

public class RealData implements Serializable {

    @ApiModelProperty(value = "采集数据时间(yyyy-MM-dd HH:mm:00)", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date dateTime;

    @ApiModelProperty(value = "工厂/园区/大楼唯一标识", required = true)
    private String resourceId;

    @ApiModelProperty(value = "工厂/园区/大楼中文名称", required = false)
    private String resourceName;

    @ApiModelProperty(value = "当前时刻，资源最大上调调节能力(单位 kW)", required = true)
    private String maxupCapacity;

    @ApiModelProperty(value = "当前时刻，资源最大上调调节速率(单位 kW/min)", required = true)
    private String maxupRate;

    @ApiModelProperty(value = "当前时刻，资源最大上调持续时长(分钟)", required = true)
    private String maxupLength;

    @ApiModelProperty(value = "当前时刻，资源最大下调调节能力(单位 kW)", required = true)
    private String maxdownCapacity;

    @ApiModelProperty(value = "当前时刻，资源最大下调调节速率(单位 kW/min)", required = true)
    private String maxdownRate;

    @ApiModelProperty(value = "当前时刻，资源最大下调持续时长(分钟)", required = true)
    private String maxdownLength;

    @ApiModelProperty(value = "若下列 4 个状态均无变化，则不传， 资源状态变更时间，格式化时间数据：yyyy-MM-dd HH:mm:ss", required = false)
    private Date statusChangeTime;

    @ApiModelProperty(value = "若状态无变化，则不传，资源受控状态，0：受控中，1：待控中；2：旁路中，若下列 4 个状态无变化，则为null", required = false)
    private String controlState;

    @ApiModelProperty(value = "若状态无变化，则不传，resourceId 对应资源的采集终端是否在线，取值范围：ONLINE、OFFLINE。", required = false)
    private String status;

    @ApiModelProperty(value = "若状态无变化，则不传，resourceId 对应资源的响应模式，传编码值（1表示自动响应，0 表示人工响应）", required = false)
    private String responseType;

    @ApiModelProperty(value = "若状态无变化，则不传，resourceId 对应资源的状态，传编码值（2 表示测试，3 表示投运，4 表示退役，5表示停机）", required = true)
    private String resourceState;

    private RData data;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date dateTime) {
        this.dateTime = dateTime;
    }

    public Date getStatusChangeTime() {
        return statusChangeTime;
    }

    public void setStatusChangeTime(Date statusChangeTime) {
        this.statusChangeTime = statusChangeTime;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getMaxupCapacity() {
        return maxupCapacity;
    }

    public void setMaxupCapacity(String maxupCapacity) {
        this.maxupCapacity = maxupCapacity;
    }

    public String getMaxupRate() {
        return maxupRate;
    }

    public void setMaxupRate(String maxupRate) {
        this.maxupRate = maxupRate;
    }

    public String getMaxupLength() {
        return maxupLength;
    }

    public void setMaxupLength(String maxupLength) {
        this.maxupLength = maxupLength;
    }

    public String getMaxdownCapacity() {
        return maxdownCapacity;
    }

    public void setMaxdownCapacity(String maxdownCapacity) {
        this.maxdownCapacity = maxdownCapacity;
    }

    public String getMaxdownRate() {
        return maxdownRate;
    }

    public void setMaxdownRate(String maxdownRate) {
        this.maxdownRate = maxdownRate;
    }

    public String getMaxdownLength() {
        return maxdownLength;
    }

    public void setMaxdownLength(String maxdownLength) {
        this.maxdownLength = maxdownLength;
    }


    public String getControlState() {
        return controlState;
    }

    public void setControlState(String controlState) {
        this.controlState = controlState;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResponseType() {
        return responseType;
    }

    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    public String getResourceState() {
        return resourceState;
    }

    public void setResourceState(String resourceState) {
        this.resourceState = resourceState;
    }

    public RData getData() {
        return data;
    }

    public void setData(RData data) {
        this.data = data;
    }
}
