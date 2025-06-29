package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 返回碳汇信息
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class SinkResp {

    @ApiModelProperty("总吸收碳排放量t")
    private String total;

    @ApiModelProperty("绿化面积")
    private String greenArea;

    @ApiModelProperty("吸收的碳排放量t")
    private String greenEmissions;

    @ApiModelProperty("种植树木")
    private String treeNum;

    @ApiModelProperty("吸收的碳排放量t")
    private String treeEmissions;

    @ApiModelProperty("种植一棵树每天吸收的二氧化碳kg")
    private String dayTreeCO2;

    @ApiModelProperty("1平米草坪每天吸收的二氧化碳kg")
    private String dayGreenCO2;

}