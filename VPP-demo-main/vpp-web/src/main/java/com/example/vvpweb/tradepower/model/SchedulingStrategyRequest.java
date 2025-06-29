package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SchedulingStrategyRequest {

    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskCode;
    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String queryDate;

}
