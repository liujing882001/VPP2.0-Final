package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳模型-范围二
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class CaScopeTwoResp {

    @ApiModelProperty("年份")
    private Integer caYear;

    @ApiModelProperty("月份")
    private Integer caMonth;

    @ApiModelProperty("外购电力（kWh）")
    private String power;

    @ApiModelProperty("外购热力（KJ） ")
    private String heatingPower;

}