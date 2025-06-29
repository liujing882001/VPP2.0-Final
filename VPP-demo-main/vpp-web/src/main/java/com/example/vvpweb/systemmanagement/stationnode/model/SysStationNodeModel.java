package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import java.util.List;

@Data
public class SysStationNodeModel {

    /**
     * 节点类型id
     */
    private String nodeTypeId;

    /**
     * 系统节点类型
     */
    private String nodeType;
    /**
     * 节点类名称
     */
    private String nodeName;
    /**
     * 可见系统地址白名单
     */
    private List<String> sysIds;

    /**
     * 地址
     */
    private String address;

    /**
     * 经度
     */
    private double longitude;


    /**
     * 纬度
     */
    private double latitude;


    /**
     * 楼宇建筑面积（平方米）
     */
    private double nodeArea;


    /**
     * 省所在区域id
     */
    private String provinceRegionId;

    /**
     * 省所在区域名称
     */
    private String provinceRegionName;


    /**
     * 市所在区域id
     */
    private String cityRegionId;

    /**
     * 市所在区域名称
     */
    private String cityRegionName;

    /**
     * 县/区所在区域id
     */
    private String countyRegionId;

    /**
     * 县/区所在区域名称
     */
    private String countyRegionName;

    /**
     * 户号
     */
    private String noHouseholds;
}
