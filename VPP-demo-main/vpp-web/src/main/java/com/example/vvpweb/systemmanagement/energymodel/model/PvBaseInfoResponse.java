package com.example.vvpweb.systemmanagement.energymodel.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "pvBaseInfoResponse", description = "光伏基本信息")
@Data
public class PvBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;

    /**
     * 光伏装机容量 kwp
     */
    @ApiModelProperty(value = "光伏装机容量 kwp", name = "photovoltaicInstalledCapacity", required = true)
    private double photovoltaicInstalledCapacity;

}
