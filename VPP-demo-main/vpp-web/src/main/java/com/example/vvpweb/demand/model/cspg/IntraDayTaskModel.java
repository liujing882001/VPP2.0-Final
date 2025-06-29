package com.example.vvpweb.demand.model.cspg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class IntraDayTaskModel {
    @ApiModelProperty("系统时间戳，格式(yyyy-MM-dd HH:mm:ss)")
    private String systemTime;
    @ApiModelProperty("负荷聚合商统一社会信用代码")
    private String creditCode;
    @ApiModelProperty("计划编号")
    private String taskCode;
    @ApiModelProperty("计划名称")
    private String taskName;
    @ApiModelProperty("事件类型,填intraDayTask")
    private String eventType;
    @ApiModelProperty("页码，若不上传该字段，默认为第 1 页")
    private int page;
    @ApiModelProperty("总页数，默认为 1 页")
    private int totalPage;
    @ApiModelProperty("不同园区/充电站/楼宇/储能站具体计划")
    private List<IntraDayTaskResourceModel> responsePlanList;
}
