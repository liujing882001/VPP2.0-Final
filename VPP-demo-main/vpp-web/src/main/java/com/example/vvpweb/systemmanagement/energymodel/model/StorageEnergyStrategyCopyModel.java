package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
public class StorageEnergyStrategyCopyModel implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * from开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String fromStartDate;

    /**
     * from结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String fromEndDate;

    /**
     * to开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String toStartDate;

    /**
     * to结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String toEndDate;

    /**
     * 时间
     */
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
//    private Date fromTs;


//    private String toNodeId;
    /**
     * 系统id
     */
//    private String toSystemId;

    /**
     * 时间
     */
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
//    private Date toTs;

}
