package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DispatchCurveCommand {
    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskCode;
    @ApiModelProperty("策略时间")
    private String queryDate;
    @ApiModelProperty("预测策略")
    private List<SchedulingStrategyModel> strategyData;
}
