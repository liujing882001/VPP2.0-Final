package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Poll implements Serializable {
    private static final long serialVersionUID = -1292611434397051003L;

    @ApiModelProperty("Poll")
    private String root;

    @ApiModelProperty("协议版本")
    private Integer version;

    @ApiModelProperty("下位节点的ID")
    private String dnId;
}
