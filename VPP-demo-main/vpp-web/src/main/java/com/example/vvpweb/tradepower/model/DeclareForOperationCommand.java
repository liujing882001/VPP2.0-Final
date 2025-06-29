package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class DeclareForOperationCommand {
    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskCode;

    @ApiModelProperty("申报日期,格式yyyy-MM-dd")
    private String queryDate;
}
