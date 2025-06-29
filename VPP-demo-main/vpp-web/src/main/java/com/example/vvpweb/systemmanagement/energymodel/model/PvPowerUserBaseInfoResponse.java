package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "pvBaseInfoResponse", description = "光伏基本信息")
@Data
public class PvPowerUserBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;


    /**
     * 电力用户购电折扣比例 开始时间 yyyy-MM
     */
    @ApiModelProperty(value = "电力用户购电折扣比例 开始时间", name = "powerUserStartTime", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date powerUserStartTime;
    /**
     * 电力用户购电折扣比例 年限
     */
    @ApiModelProperty(value = "电力用户购电折扣比例 年限", name = "powerUserExpiryDate", required = true)
    private int powerUserExpiryDate;
}
