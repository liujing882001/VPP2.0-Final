package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

@Data
public class CopilotRequest {
    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("系统id")
    private String systemId;

    @ApiModelProperty("开始日期,格式yyyy-MM-dd")
    @NotBlank(message = "开始日期不能为空")
    private String startDate;

    @ApiModelProperty("结束日期,格式yyyy-MM-dd")
    @NotBlank(message = "结束日期不能为空")
    private String endDate;

    @ApiModelProperty("预测策略")
    private List<CopilotResponse> energyFore;
}
