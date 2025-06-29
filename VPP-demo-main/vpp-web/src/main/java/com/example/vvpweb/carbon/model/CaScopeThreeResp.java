package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳模型-范围三
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class CaScopeThreeResp {

    @ApiModelProperty("年份")
    private Integer caYear;

    @ApiModelProperty("月份")
    private Integer caMonth;

    @ApiModelProperty("差旅-飞机(km)")
    private String tPlane;

    @ApiModelProperty("差旅-火车（km) ")
    private String tTrain;

    @ApiModelProperty("差旅-私家车(辆) ")
    private String tCar;

    @ApiModelProperty("自来水(t) ")
    private String tTapWater;

    @ApiModelProperty("纸张消耗(张)")
    private String tPaper;

}