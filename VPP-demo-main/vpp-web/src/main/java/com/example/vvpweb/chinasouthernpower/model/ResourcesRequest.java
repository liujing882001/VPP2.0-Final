package com.example.vvpweb.chinasouthernpower.model;

import io.swagger.annotations.ApiModelProperty;

public class ResourcesRequest {

    @ApiModelProperty(value = "系统时间戳", required = true)
    private String systemTime;

    @ApiModelProperty(value = "负荷聚合商唯一标识", required = true)
    private String creditCode;

    @ApiModelProperty(value = "事件类型", required = true, allowableValues = "resourceInfo")
    private String eventType;

    @ApiModelProperty(value = "父资源 id，为 null 时表示第二级", required = false)
    private String parentResourceId;

    @ApiModelProperty(value = "页码，若不上传该字段，默认为第 1页", required = false)
    private int page = 1;

    @ApiModelProperty(value = "每页显示最大条数，若不上传该字段，则默认为 100", required = false)
    private int pageSize = 100;

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

    public String getParentResourceId() {
        return parentResourceId;
    }

    public void setParentResourceId(String parentResourceId) {
        this.parentResourceId = parentResourceId;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
