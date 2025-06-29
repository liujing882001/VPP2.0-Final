package com.example.gateway.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 户号下参与需求响应设备
 * 设置温度
 */
@lombok.Data
public class DeviceOneAttrModel implements Serializable {
    /**
     * deviceSn 设备编号
     */
    private String deviceSn;
    /**
     *deviceSn 属性值
     */
    private String pointSn;
    /**
     * 设置的值
     */
    private String pointValue;
}
