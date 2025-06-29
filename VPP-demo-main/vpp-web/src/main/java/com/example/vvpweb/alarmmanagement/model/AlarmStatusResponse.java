package com.example.vvpweb.alarmmanagement.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class AlarmStatusResponse implements Serializable {

    /**
     * 报警状态
     */
    @ApiModelProperty(value = "报警状态", name = "status", required = true)
    private Integer status;


    /**
     * 报警状态0 已恢复 1报警中 2 处理中
     */
    @ApiModelProperty(value = "报警状态描述 0 已恢复 1报警中 2 处理中", name = "severityDesc", required = true)
    private String statusDesc;
}
