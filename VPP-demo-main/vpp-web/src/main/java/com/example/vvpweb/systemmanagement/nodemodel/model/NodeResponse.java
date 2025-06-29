package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhaoph
 */
@Data
public class NodeResponse implements Serializable {

    /**
     * 节点id
     */
    private String id;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 节点类型名称
     */
    private String nodeTypeName;
    /**
     * 节点下系统集合 ,分割
     */
    private String systemNames;

    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    /**
     * nodePostType  归属类型 load 负荷，pv 光伏，storageEnergy 储能
     */
    private String nodePostType;

    /**
     * 节点类型ID
     */
    private String nodeTypeId;

    /**
     * 可见系统地址白名单
     */
    private List<String> systemIds;

    /**
     * 是否在线
     */
    private Boolean online;

    /**
     * 启用 、 禁用
     */
    private Boolean isEnabled;

    /**
     * 地址
     */
    private String address;


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
