package com.example.gateway.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class RPCModel implements Serializable {

    /**
     * deviceSn 设备编号
     */
    private String deviceSn;
    /**
     * 属性集合 属性sn  对应设置的值
     */
    private List<PointInfo> pointInfoList = new ArrayList<>();


    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timestamp;

    public RPCModel(String deviceSn, List<PointInfo> pointInfoList, Date timestamp) {
        this.deviceSn = deviceSn;
        this.pointInfoList = pointInfoList;
        this.timestamp = timestamp;
    }

    public RPCModel() {
    }

    public String getDeviceSn() { return deviceSn; }
    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public List<PointInfo> getPointInfoList() { return pointInfoList; }
    public void setPointInfoList(List<PointInfo> pointInfoList) { this.pointInfoList = pointInfoList; }
    public Date getTimestamp() { return timestamp; }
    public void setTimestamp(Date timestamp) { this.timestamp = timestamp; }

}
