package com.example.vvpweb.systemmanagement.nodemodel.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author Zhaoph
 */
@Data
public class NodeModelData implements Serializable {
    /**
     * 节点ID（为更新节点是使用）
     */
    private String nodeId;
    /**
     * 节点类型id
     */
    private String nodeTypeId;

    /**
     * 节点类名称
     */
    private String nodeName;

    /**
     * 地址
     */
    private String address;
    /**
     * 可见系统地址白名单
     */
    private List<String> sysIds;

    /**
     * 收益模型地址
     */
    private String earningsPatternPath;


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
