package com.example.vvpdomain.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author konghao
 * @description 策略
 * @date 2023-03-07
 */
@Entity
@Data
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_strategy")
public class DemandStrategy implements Serializable {

    @Id
    @Column(name = "id")
    private String id;
    /**
     * 任务id
     */
    @Column(name = "resp_id")
    private String respId;
    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;
    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;
    /**
     * 策略执行时间点
     */
    @Column(name = "time_point")
    private Date timePoint;
    /**
     * 策略内容
     */
    @Column(name = "strategy_content")
    private String strategyContent;
    /**
     * 是否大模型生成
     */
    @Column(name = "is_llm")
    private boolean isLlm;
    /**
     * 策略使用状态：1目前在使用的策略，2弃用的策略，3是等待策略生成
     */
    @Column(name = "state")
    private Integer state;
    /**
     * 预测调节后负荷
     */
    @Column(name = "forecast_adjusted_load")
    private String forecastAdjustedLoad;
    /**
     * 预测调节负荷生成时间
     */
    @Column(name = "forecast_time")
    private Date forecastTime;
    /**
     * 节点名称
     */
    @Column(name = "node_name")
    private String nodeName;
    /**
     * 需求响应下发值也是申报负荷
     */
    @Column(name = "command_value")
    private String commandValue;
    /**
     * 户号
     */
    @Column(name = "no_households")
    private String noHouseholds;
    /**
     * 预测负荷
     */
    @Column(name = "forecast_load")
    private String forecastLoad;
    /**
     * 预测调节负荷
     */
    @Column(name = "forecast_adjust_load")
    private String forecastAdjustLoad;
    /**
     * 确认状态,0未确定，1确定
     */
    @Column(name = "ensure")
    private Integer ensure;
}
