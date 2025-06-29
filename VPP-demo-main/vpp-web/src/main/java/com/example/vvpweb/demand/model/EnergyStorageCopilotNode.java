package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class EnergyStorageCopilotNode {
    @ApiModelProperty("节点分类")
    private String nodeType;

    @ApiModelProperty("节点数量")
    private Integer cnt;
}
