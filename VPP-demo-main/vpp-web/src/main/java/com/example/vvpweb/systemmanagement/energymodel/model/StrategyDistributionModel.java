package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Data
public class StrategyDistributionModel implements Serializable {
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
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date endDate;

    /**
     * 开始时间
     */
    @ApiModelProperty("开始日期,格式hh:mm")
    @NotBlank(message = "开始时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    private Date startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束日期,格式hh:mm")
    @NotBlank(message = "结束时间不能为空")
    @JsonFormat(pattern = "HH:mm")
    private Date endTime;

    //调度策略
    private String strategy;

    //充放电功率
    private Double power;

    private int index;

    public StrategyDistributionModel() {}

    public StrategyDistributionModel(String nodeId, String systemId, String startDate, String endDate, String startTime, String endTime, String strategy, Double power,int index) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdfhm = new SimpleDateFormat("HH:mm");
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.startDate = formatter.parse(startDate);
        this.endDate = formatter.parse(endDate);
        this.startTime = sdfhm.parse(startTime);
        this.endTime = sdfhm.parse(endTime);
        this.strategy = strategy;
        this.power = power;
        this.index = index;
    }

}
