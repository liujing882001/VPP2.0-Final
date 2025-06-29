package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 */
@Data
public class DemandModel implements Serializable {

    /**
     * 每页大小
     */
    @ApiModelProperty("每页大小")
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    @ApiModelProperty("第几页，默认从1开始")
    private int number;

    @ApiModelProperty("任务id,仅查价格详情接口使用该字段")
    private String respId;

    /**
     * 状态（1-未开始 2-执行中）
     */
//    @ApiModelProperty("状态（1-未开始 2-执行中）,仅实时监测接口使用该字段")
//    private Integer status;

    @ApiModelProperty("开始日期，仅历史查询|需求看板接口使用该字段")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date startDate;

    @ApiModelProperty("结束日期，仅历史查询|需求看板接口使用该字段")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date endDate;

    @ApiModelProperty("响应时段排序 1-升序 2-降序")
    private Integer rsTimeSort;

    @ApiModelProperty("反馈截止排序 1-升序 2-降序")
    private Integer feedbackTimeSort;

    @ApiModelProperty("响应类型排序 1-升序 2-降序")
    private Integer respTypeSort;

    @ApiModelProperty("响应级别排序 1-升序 2-降序")
    private Integer respLevelSort;

    @ApiModelProperty("响应补贴排序 1-升序 2-降序")
    private Integer respSubsidySort;

    @ApiModelProperty("收益排序 1-升序 2-降序")
    private Integer profitSort;

    @ApiModelProperty("节点id")
    private String nodeId;
}
