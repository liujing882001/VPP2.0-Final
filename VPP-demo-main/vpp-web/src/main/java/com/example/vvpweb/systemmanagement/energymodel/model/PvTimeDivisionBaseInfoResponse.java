package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@ApiModel(value = "pvBaseInfoResponse", description = "光伏基本信息")
@Data
public class PvTimeDivisionBaseInfoResponse implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 系统id
     */
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;


    /**
     * 分时电价开始时间 yyyy-MM
     */
    @ApiModelProperty(value = "分时电价开始时间 yyyy-MM", name = "timeDivisionStartTime", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date timeDivisionStartTime;
    /**
     * 分时电价年限
     */
    @ApiModelProperty(value = "分时电价年限", name = "timeDivisionExpiryDate", required = true)
    private int timeDivisionExpiryDate;

}
