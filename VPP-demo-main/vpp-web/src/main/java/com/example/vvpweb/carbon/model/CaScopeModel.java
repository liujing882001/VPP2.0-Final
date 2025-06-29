package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳模型-范围一
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class CaScopeModel {

    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

    @ApiModelProperty("年份")
    private Integer caYear;
}