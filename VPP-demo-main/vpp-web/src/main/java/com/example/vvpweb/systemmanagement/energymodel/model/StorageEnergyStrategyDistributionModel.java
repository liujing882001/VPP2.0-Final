package com.example.vvpweb.systemmanagement.energymodel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

@Data
public class StorageEnergyStrategyDistributionModel implements Serializable {

    private String nodeId;

    /**
     * 系统id
     */
    private String systemId;

    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String endDate;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始日期,格式hh:mm")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束日期,格式hh:mm")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    //调度策略
    private String strategy;

    //充放电功率
    private Double power;

    /**
     * 时间
     */
//    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
//    private Date ts;

    /**
     * 是否立即生效 还是第二天生效
     */
//    private boolean  takeEffect;

    public StorageEnergyStrategyDistributionModel() {}

    public StorageEnergyStrategyDistributionModel(String nodeId, String systemId, String startDate, String endDate, String startTime, String endTime, String strategy, Double power) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.strategy = strategy;
        this.power = power;

    }
}
