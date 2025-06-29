package com.example.vvpweb.chinasouthernpower.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RealTimeDataResponse implements Serializable {

    @ApiModelProperty(value = "页码", required = true)
    private int page;

    @ApiModelProperty(value = "总页数", required = true)
    private int totalPage;

    @ApiModelProperty(value = "用户资源数据", required = true)
    private List<RealData> realData;

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

    public List<RealData> getRealData() {
        return realData;
    }

    public void setRealData(List<RealData> realData) {

        if(realData==null)
            realData = new ArrayList<>();
        this.realData = realData;
    }

}
