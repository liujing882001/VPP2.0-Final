package com.example.vvpweb.alarmmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Zhaoph
 */
@Data
public class AlarmModel implements Serializable {

    /**
     * 节点id
     */
    @ApiModelProperty(value = "节点id", name = "nodeId", required = true)
    private String nodeId;
    /**
     * 报警查询开始时间
     */
    @ApiModelProperty(value = "报警查询开始时间", name = "startTs", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date startTs;

    /**
     * 报警查询开始时间
     */
    @ApiModelProperty(value = "报警查询开始时间", name = "endTs", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date endTs;
    /**
     * 严重程度，等级 0 紧急1 重要2 次要3 提示
     *
     *0 1 对应 故障
     * 2 对应 警告
     * 3 4 对 应 提示
     *
     */
    @ApiModelProperty(value = "严重程度，等级 0 紧急1 重要2 次要3 提示", name = "severity", required = true)
    private Integer severity;
    /**
     * 报警状态0 已恢复 1报警中 2 处理中
     */
    @ApiModelProperty(value = "报警状态0 已恢复 1报警中 2 处理中", name = "status", required = true)
    private Integer status;

    /**
     * 每页大小
     */
    @ApiModelProperty(value = "每页大小", name = "pageSize", required = true)
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty(value = "当前页为第几页 默认 1开始", name = "number", required = true)
    private int number;

}
