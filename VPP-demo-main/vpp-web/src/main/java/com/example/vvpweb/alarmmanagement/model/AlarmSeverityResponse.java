package com.example.vvpweb.alarmmanagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class AlarmSeverityResponse implements Serializable {

    /**
     * 严重程度
     */
    @ApiModelProperty(value = "严重程度", name = "severity", required = true)
    private Integer severity;
    /**
     * 报警状态0 已恢复 1报警中 2 处理中
     */
    @ApiModelProperty(value = "报警状态0 已恢复 1报警中 2 处理中", name = "severityDesc", required = true)
    private String severityDesc;
}
