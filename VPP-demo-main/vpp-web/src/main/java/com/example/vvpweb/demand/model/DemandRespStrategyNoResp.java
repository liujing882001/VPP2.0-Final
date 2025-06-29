package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;

/**
 * @author maoyating
 * @description 需求响应策略户号应对关系
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
public class DemandRespStrategyNoResp {
    /**
     * 策略id
     */
    @ApiModelProperty("策略id")
    private String drsId;


    /**
     * 所属策略id
     */
    @ApiModelProperty("所属策略id")
    private String strategyId;

    /**
     * 户号
     */
    @ApiModelProperty("户号")
    private String noHouseholds;

    /**
     * 状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）
     */
    @ApiModelProperty("状态（11-未申报 12-执行中未申报 15-已结束未申报 21-待出清已申报  22-出清成功已申报 23-出清失败已申报 24-执行中已申报 25-已结束已申报）")
    private Integer drsStatus;

    /**
     * 申报负荷
     */
    @ApiModelProperty("申报负荷")
    private Double declareLoad;

    /**
     * 节点名称
     */
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 基线负荷
     */
    @ApiModelProperty("基线负荷")
    private String baseLoad;

    /**
     * 当前负荷-实时负荷
     */
    @ApiModelProperty("当前负荷")
    private String nowLoad;

    /**
     * 实时响应负荷电量
     */
    @ApiModelProperty("实时响应负荷电量")
    private Double realTimeLoad;

    /**
     * 实际响应率
     */
    @ApiModelProperty("实际响应率")
    private Double actualRatio;

    /**
     * 收益
     */
    @ApiModelProperty("收益")
    private Double profit;

    /**
     * 价格
     */
    @ApiModelProperty("价格")
    private Double declarePrice;

    /**
     * 调节负荷
     */
    @ApiModelProperty("调节负荷")
    private String adjustLoad;

    /**
     * 预测负荷
     */
    @ApiModelProperty("预测负荷")
    private String forecastLoad;

    /**
     * 节点在线状态 true-在线 false-不在线
     */
    @ApiModelProperty("节点在线状态")
    private boolean online;

    /**
     * 响应负荷
     */
    @ApiModelProperty("响应负荷")
    private Double responseLoad;

}