package com.example.vvpweb.loadmanagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class StorageEnergyResourcesResponse {
    /**
     * 电站容量
     */
    @ApiModelProperty("电站容量")
    private Double capacity;

    /**
     * 电站功率
     */
    @ApiModelProperty("电站功率")
    private Double load;

    /**
     * soc
     */
    @ApiModelProperty("soc")
    private Double soc;

    /**
     * soh
     */
    @ApiModelProperty("soh")
    private Double soh;

    /**
     * 当前可充容量kwh
     */
    @ApiModelProperty("当前可充容量kwh")
    private Double inCapacity;

    /**
     * 当前可放容量kwh
     */
    @ApiModelProperty("当前可放容量kwh")
    private Double outCapacity;

    /**
     * 最大可充功率kw
     */
    @ApiModelProperty("最大可充功率kw")
    private Double maxInLoad;

    /**
     * 最大可放功率kw
     */
    @ApiModelProperty("最大可放功率kw")
    private Double maxOutLoad;
    /**
     * 储能数量
     */
    @ApiModelProperty("储能数量")
    private int storedenergy;
}
