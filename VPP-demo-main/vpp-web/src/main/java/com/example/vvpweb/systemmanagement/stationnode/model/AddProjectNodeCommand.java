package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Data
public class AddProjectNodeCommand {


    /**
     * 节点类型id
     */
    @NotEmpty(message = "项目节点类型id不能为空")
    private String nodeTypeId;
    /**
     * 项目节点类型
     */
    @NotBlank(message = "项目节点类型不能为空")
    private String nodeType;

    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称不能为空")
    private String nodeName;

    /**
     * 地址
     */
    @NotBlank(message = "地址不能为空")
    private String address;

    /**
     * 设备分类（系统类型）
     */
    @NotEmpty(message = "设备分类不能为空")
    private List<String> sysIds;

    /**
     * 经度
     */
    @NotNull(message = "经度不能为空")
    private Double longitude;

    /**
     * 纬度
     */
    @NotNull(message = "纬度不能为空")
    private Double latitude;

    /**
     * 楼宇建筑面积（平方米）
     */
    @Positive(message = "楼宇建筑面积（平方米）必须大于 0")
    private double nodeArea;

    /**
     * 省所在区域id
     */
    @NotBlank(message = "省所在区域id不能为空")
    private String provinceRegionId;

    /**
     * 省所在区域名称
     */
    @NotBlank(message = "省所在区域名称不能为空")

    private String provinceRegionName;

    /**
     * 市所在区域id
     */
    @NotBlank(message = "市所在区域id不能为空")

    private String cityRegionId;

    /**
     * 市所在区域名称
     */
    @NotBlank(message = "市所在区域名称不能为空")

    private String cityRegionName;

    /**
     * 县/区所在区域id
     */
    @NotBlank(message = "县/区所在区域id不能为空")

    private String countyRegionId;

    /**
     * 县/区所在区域名称
     */
    @NotBlank(message = "县/区所在区域名称不能为空")

    private String countyRegionName;

    /**
     * 户号
     */
    @NotBlank(message = "户号不能为空")
    private String noHouseholds;
    /**
     * 节点阶段 建设中、运营中
     */
    @NotBlank(message = "节点阶段")
    private String stationState;

    private String eType;

    private String vol;

    private String basicBill;

    private String electricityCompany;
}
