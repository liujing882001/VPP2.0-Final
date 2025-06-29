package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "storageEnergyBaseInfoResponse", description = "储能基本信息")
@Data
public class StorageEnergyShareProportionBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;


    /**
     * 分层比例开始时间 yyyy-MM
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    @ApiModelProperty(value = "分层比例开始时间", name = "shareProportionStartTime", required = true)
    private Date shareProportionStartTime;
    /**
     * 分层比例年限
     */
    @ApiModelProperty(value = "分层比例年限", name = "shareProportionExpiryDate", required = true)
    private int shareProportionExpiryDate;
}
