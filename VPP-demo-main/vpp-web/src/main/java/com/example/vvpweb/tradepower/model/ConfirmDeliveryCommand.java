package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class ConfirmDeliveryCommand {
    @ApiModelProperty("参与任务id")
    private String taskCode;
    @ApiModelProperty("数据")
    List<SchedulingStrategyModel> taskData;

}
