package com.example.vvpweb.loadmanagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class PVResourcesResponse {
    /**
     * 实际发电功率
     */
    @ApiModelProperty("实际发电功率")
    private Double load;
    /**
     * 装机容量
     */
    @ApiModelProperty("装机容量")
    private Double capacity;
    /**
     * 当日发电量
     */
    @ApiModelProperty("当日发电量")
    private Double nowEnergy;
    /**
     * 累计发电量
     */
    @ApiModelProperty("累计发电量")
    private Double energy;
    /**
     * 光伏数量
     */
    @ApiModelProperty("光伏数量")
    private int photovoltaic;
}
