package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "storageEnergyBaseInfoResponse", description = "储能基本信息")
@Data
public class StorageEnergyStrategyBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;

    /**
     * 充放电策略开始时间 yyyy-MM
     */
    @ApiModelProperty(value = "充放电策略开始时间", name = "strategyStartTime", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date strategyStartTime;
    /**
     * 充放电策略年限
     */
    @ApiModelProperty(value = "充放电策略年限", name = "strategyExpiryDate", required = true)
    private int strategyExpiryDate;

}
