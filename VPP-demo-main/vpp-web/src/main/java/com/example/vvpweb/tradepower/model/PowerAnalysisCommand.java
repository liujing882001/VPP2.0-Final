package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class PowerAnalysisCommand {
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
    private String startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String endDate;
}
