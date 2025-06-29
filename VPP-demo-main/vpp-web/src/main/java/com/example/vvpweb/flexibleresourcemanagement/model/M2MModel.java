package com.example.vvpweb.flexibleresourcemanagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "M2MModel", description = "用电同比分析")
public class M2MModel implements Serializable {

    @ApiModelProperty(value = "当月用电", name = "theSameMonthEnergy", required = true)
    private double theSameMonthEnergy;


    @ApiModelProperty(value = "上月同期", name = "theLastMonthEnergy", required = true)
    private double theLastMonthEnergy;


    @ApiModelProperty(value = "同期对比", name = "m2mEnergy", required = true)
    private String m2mEnergy;
}
