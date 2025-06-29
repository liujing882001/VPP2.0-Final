package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
public class StorageEnergyBaseInfoReq {
    /**
     * 节点id
     */
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 系统
     */
    @ApiModelProperty("系统Id")
    private String systemId;

    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd HH:mm:ss")
    @NotBlank(message = "开始日期不能为空")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String endDate;
}
