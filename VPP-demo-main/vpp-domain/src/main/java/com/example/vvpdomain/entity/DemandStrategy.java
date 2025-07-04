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

    // Manual getters and setters to ensure compilation
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRespId() { return respId; }
    public void setRespId(String respId) { this.respId = respId; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }
    public Date getTimePoint() { return timePoint; }
    public void setTimePoint(Date timePoint) { this.timePoint = timePoint; }
    public String getStrategyContent() { return strategyContent; }
    public void setStrategyContent(String strategyContent) { this.strategyContent = strategyContent; }
    public boolean isLlm() { return isLlm; }
    public void setLlm(boolean llm) { isLlm = llm; }
    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }
    public String getForecastAdjustedLoad() { return forecastAdjustedLoad; }
    public void setForecastAdjustedLoad(String forecastAdjustedLoad) { this.forecastAdjustedLoad = forecastAdjustedLoad; }
    public Date getForecastTime() { return forecastTime; }
    public void setForecastTime(Date forecastTime) { this.forecastTime = forecastTime; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getCommandValue() { return commandValue; }
    public void setCommandValue(String commandValue) { this.commandValue = commandValue; }
    public String getNoHouseholds() { return noHouseholds; }
    public void setNoHouseholds(String noHouseholds) { this.noHouseholds = noHouseholds; }
    public String getForecastLoad() { return forecastLoad; }
    public void setForecastLoad(String forecastLoad) { this.forecastLoad = forecastLoad; }
    public String getForecastAdjustLoad() { return forecastAdjustLoad; }
    public void setForecastAdjustLoad(String forecastAdjustLoad) { this.forecastAdjustLoad = forecastAdjustLoad; }
    public Integer getEnsure() { return ensure; }
    public void setEnsure(Integer ensure) { this.ensure = ensure; }
}
