package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳溯源
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class TraceabilityResp {

    @ApiModelProperty("类型")
    private String dischargeEntity;

    @ApiModelProperty("数量")
    private String num;

    @ApiModelProperty("单位")
    private String unit;

    @ApiModelProperty("碳排放因子")
    private String factor;

    @ApiModelProperty("二氧化碳排放量")
    private String emission;

    @ApiModelProperty("燃烧源")
    private String dischargeType;

}