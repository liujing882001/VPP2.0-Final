package com.example.gateway.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 户号下参与需求响应设备
 * 设置温度
 */
@lombok.Data
public class DeviceMultipleAttrModel implements Serializable {
    /**
     * deviceSn 设备编号
     */
    private String deviceSn;
    /**
     * 属性集合 属性sn  对应设置的值
     */
    private List<PointInfo> pointInfoList = new ArrayList<>();
}
