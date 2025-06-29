package com.example.vvpweb.chinasouthernpower.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class ResourcesInfo implements Serializable {


    @ExcelProperty(value = "负荷聚合商唯一标识")
    @JSONField(serialize = false, deserialize = false)
    @JsonIgnore
    private String loadAggregatorCreditCode;

    @ExcelProperty(value = "代理用户唯一标识")
    @ApiModelProperty(value = "负荷聚合商和其代理用户 唯一标识", required = true)
    @JsonIgnore
    private String creditCode;

    @ExcelProperty(value = "用电户号resourceId")
    @ApiModelProperty(value = "工厂/园区/大楼唯一标识（用电户号）", required = true)
    private String resourceId;

    @ExcelProperty(value = "资源名称")
    @ApiModelProperty(value = "工厂/园区/大楼中文名称", required = true)
    private String resourceName;

    @ExcelProperty(value = "响应方式")
    @ApiModelProperty(value = "响应方式，传编码值（1：自动响应，0：人工响应）", required = true)
    private String responseType;


    @ExcelProperty(value = "资源类型")
    @ApiModelProperty(value = "资源类型，传编码值（1 表示发电资源，2 表示储电资源，3 表示用电资源）", required = true)
    private String resourceType;

    @ExcelProperty(value = "资源用电类别")
    @ApiModelProperty(value = "资源用电类别，传编码值（\n" +
            "用电资源类别中：大工业 301； 商业负荷 302；充电站/桩 304； 智能建筑 312；其他 315；\n" +
            "发电资源类别中：风力发电站101；集中式光伏 102；分布式光伏 103；冷热电发电站 104；柴油发电站 105；\n" +
            "储能资源类别中：储能 204）\n", required = true)
    private String subType;

    @ExcelProperty(value = "资源状态")
    @ApiModelProperty(value = "资源状态，传编码值（2 表示测试，3 表示投运，4 表示退役，5 表示停机）", required = true)
    private String resourceState;


    @ExcelProperty(value = "额定功率")
    @JSONField(serialize = false, deserialize = false)
    private double ratedPower;


    @ExcelProperty(value = "最大可调节功率")
    @ApiModelProperty(value = "最大可调节功率（kW），需用户自行评估可减少的功率范围", required = true)
    private double maxAdjustablePower;

    @ExcelProperty(value = "最大上升速率")
    @ApiModelProperty(value = "最大上升速率（kW/min）", required = true)
    private double increaseRate;

    @ExcelProperty(value = "最大下降速率")
    @ApiModelProperty(value = "最大下降速率（kW/min）", required = true)
    private double decreaseRate;

    @ExcelProperty(value = "响应时间级别")
    @ApiModelProperty(value = "响应时间级别，\n" +
            "响应一级（响应时间≤1 秒以内达到响应目标）：1\n" +
            "响应二级（1 秒＜响应时间≤1 分钟以内达到响应目标）：2\n" +
            "响应三级（ 1 分钟＜响应时间≤\n" +
            "15 分钟以内达到响应目标）：3 \n" +
            "响应四级（15 分钟＜响应时间≤ 30 分钟以内达到响应目标） ：4 \n" +
            "响应五级（30 分钟＜响应时间≤ 2 小时以内达到响应目标）：5 \n" +
            "响应六级（2 小时＜响应时间≤ 24 小时以内达到响应目标）：6 \n" +
            "响应七级（≥24 小时以内达到响应目标）：7", required = true)
    private String responseLevel;

    @ExcelProperty(value = "挂设备数量")
    @ApiModelProperty(value = "该 resourceID 下挂设备数量(充电站填充电桩数量，换电站填电池数量，建筑楼宇填空调主机数量， 铁塔基站/分布式光伏及其他均填 1)", required = true)
    private int deviceNum;

    @ExcelProperty(value = "经度")
    @ApiModelProperty(value = "经度采用百度地图坐标系保留小数点后 6 位", required = true)
    private double longitude;

    @ExcelProperty(value = "纬度")
    @ApiModelProperty(value = "纬度采用百度地图坐标系保留小数点后 6 位", required = true)
    private double latitude;

    @ExcelProperty(value = "用电地址")
    @ApiModelProperty(value = "用电地址", required = true)
    private String address;


    @ExcelProperty(value = "父用电户号parentResourceId")
    @ApiModelProperty(value = "父资源 id，为 null 时表示为无上级 用电户号resourceId", required = true)
    private String parentResourceId;
    @ExcelIgnore
    private String listingNum = "";
    @ExcelIgnore
    private double ratedcapacity = 0;
    @ExcelIgnore
    private double ratedVoltage= 0;


}

