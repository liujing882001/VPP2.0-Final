package com.example.vvpweb.loadmanagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class SubModel implements Serializable {

    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    @ApiModelProperty(value = "系统id", name = "systemId", required = true)
    private String systemId;
    @ApiModelProperty(value = "节点类型 NODE,SYSTEM", name = "type", required = true)
    private String type;
}
