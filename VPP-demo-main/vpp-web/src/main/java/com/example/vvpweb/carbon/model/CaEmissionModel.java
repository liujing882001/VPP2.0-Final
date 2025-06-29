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
public class CaEmissionModel {

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty("第几页，默认从1开始")
    private int number;

    @ApiModelProperty("省份")
    private String province;

    @ApiModelProperty("范围")
    private String scopeType;
}