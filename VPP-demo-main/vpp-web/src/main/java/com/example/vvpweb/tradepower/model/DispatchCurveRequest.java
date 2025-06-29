package com.example.vvpweb.tradepower.model;

import com.example.vvpweb.demand.model.CopilotResponse;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DispatchCurveRequest {
    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("预测策略")
    private List<CopilotResponse> energyFore;
}
