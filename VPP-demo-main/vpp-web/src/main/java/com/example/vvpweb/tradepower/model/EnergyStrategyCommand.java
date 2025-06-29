package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;


@Data
public class EnergyStrategyCommand {
    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String task_code;
    /**
     * 开始日期
     */
    @ApiModelProperty("策略内容")
    private List<SchedulingStrategyFirstModel> strategies;
}
