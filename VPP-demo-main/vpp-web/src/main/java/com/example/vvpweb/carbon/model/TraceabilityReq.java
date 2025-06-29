package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * 碳溯源
 * add by maoyating
 */
@Entity
@Getter
@Setter
public class TraceabilityReq {

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("范围(1-范围一 2-范围二 3-范围三)")
    private Integer scopeType;

    @ApiModelProperty("起始日期 yyyy-MM")
    private String startTime;

    @ApiModelProperty("截止日期 yyyy-MM")
    private String endTime;

}