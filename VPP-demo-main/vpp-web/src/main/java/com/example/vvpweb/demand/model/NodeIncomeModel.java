package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class NodeIncomeModel {
    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("节点名")
    private String nodeName;

    @ApiModelProperty("节点id")
    private List<Object> list;

    @ApiModelProperty("节点名")
    private Double income;
}
