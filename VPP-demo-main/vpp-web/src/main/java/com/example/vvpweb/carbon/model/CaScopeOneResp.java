package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳模型-范围一返回
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class CaScopeOneResp {

    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

    @ApiModelProperty("年份")
    private Integer caYear;

    @ApiModelProperty("月份")
    private Integer caMonth;

    @ApiModelProperty("天然气m²")
    private String naturalGas;

    @ApiModelProperty("煤气m²")
    private String coalGas;

    @ApiModelProperty("柴油m²")
    private String dieselOil;

    @ApiModelProperty("公务车(辆)")
    private String officialVehicle;

    @ApiModelProperty("冷机制冷剂类型")
    private String refrigerant;

    @ApiModelProperty("每台冷机制冷剂数量")
    private String refrigerantNum;

    @ApiModelProperty("冷机数量")
    private String refrigeratorNum;

    @ApiModelProperty("分体空调数量")
    private String airCNum;

    @ApiModelProperty("灭火器数量")
    private String fNum;

}