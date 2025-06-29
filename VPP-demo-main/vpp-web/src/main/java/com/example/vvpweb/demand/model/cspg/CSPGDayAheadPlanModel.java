package com.example.vvpweb.demand.model.cspg;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class CSPGDayAheadPlanModel {

    @ApiModelProperty("系统时间戳，格式(yyyy-MM-dd HH:mm:ss)")
    private String systemTime;
    @ApiModelProperty("计划编号")
    private String planCode;
    @ApiModelProperty("计划名称")
    private String planName;
    @ApiModelProperty("响应日期 yyyy-MM-dd")
    private String planTime;
    @ApiModelProperty("邀约计划 ID")
    //日前计划对应的邀约计划，对应多个时，用逗号分开。
    private String invitationId;
    @ApiModelProperty("负荷聚合商统一社会信用代码")
    private String creditCode;
    @ApiModelProperty("事件类型,填dayaheadPlan")
    private String eventType;
    @ApiModelProperty("页码，若不上传该字段，默认为第 1 页")
    private int page;
    @ApiModelProperty("总页数，默认为 1 页")
    private int totalPage;
    //仅含邀约时段内的 96 点价格曲线(元/MWh)
    @ApiModelProperty("价格曲线")
    private Map<String, Double> vppPrices;
    @ApiModelProperty("负荷聚合商或大用户总的日前调度计划,MW")
    private Map<String, Double> vppPlans;
    @ApiModelProperty("不同园区/充电站/楼宇/储能站具体计划")
    private List<CSPGResponsePlanModel> responsePlan;
}
