package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SchedulingStrategyEditorReq {

    /**
     * 任务编号
     */
    @ApiModelProperty("任务编号")
    private String taskCode;

    @ApiModelProperty("编辑节点")
    private List<String> nodeIds;
    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String editDate;


    /**
     * 开始时间
     */
    @ApiModelProperty("开始日期,格式HH:mm")
    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    /**
     * 结束时间
     */
    @ApiModelProperty("结束日期,格式HH:mm")
    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    private String type;

    private Double power;
}
