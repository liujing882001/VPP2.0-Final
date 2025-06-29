package com.example.vvpweb.chinasouthernpower.model;

import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.List;

public class ResourcesResponse {

    @ApiModelProperty(value = "页码", required = true)
    private int page;

    @ApiModelProperty(value = "总页数", required = true)
    private int totalPage;

    @ApiModelProperty(value = "用户资源", required = true)
    private List<ResourcesInfo> resources;


    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public List<ResourcesInfo> getResources() {
        return resources;
    }

    public void setResources(List<ResourcesInfo> resources) {
        if(resources==null)
            resources = new ArrayList<>();
        this.resources = resources;
    }

}
