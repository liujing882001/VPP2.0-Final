package com.example.gateway.model;

import java.io.Serializable;

/**
 * 户号下参与需求响应设备
 * 设置温度
 */
@lombok.Data
public class PointInfo implements Serializable {

    /**
     *deviceSn 属性值
     */
    private String pointSn;

    /**
     * 设置的值
     */
    private String value;

    public PointInfo() {
    }
    public PointInfo(String pointSn, String value) {
        this.pointSn = pointSn;
        this.value = value;
    }


}
