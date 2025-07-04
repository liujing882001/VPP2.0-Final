package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @author zph
 * @description 储能资源-电站列表信息
 * @date 2022-08-12
 */
@Entity
@Data
@Table(name = "bi_storage_energy_resources")
@EntityListeners(AuditingEntityListener.class)
public class BiStorageEnergyResources implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

    /**
     * 电站状态 在线离线
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;


    /**
     * 电站名称id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 电站容量
     */
    @Column(name = "capacity")
    private Double capacity;
    /**
     * soc
     */
    @Column(name = "soc")
    private Double soc;

    /**
     * soh
     */
    @Column(name = "soh")
    private Double soh;

    /**
     * 当前可充容量kwh
     * 可充容量= ∑ 单个电站的容量*（1-SOC）
     */
    @Column(name = "in_capacity")
    private Double inCapacity;

    /**
     * 当前可放容量kwh
     * 可放容量= ∑ 单个电站的容量*（SOC）；
     */
    @Column(name = "out_capacity")
    private Double outCapacity;

    /**
     * 电站功率
     */
    @Column(name = "`load`")
    private Double load;
    /**
     * 最大可充功率kw
     * 可充功率=∑单个电站电池处于"充电"状态时的电池功率
     */
    @Column(name = "max_in_load")
    private Double maxInLoad;

    /**
     * 最大可放功率kw
     * 可放功率= ∑单个电站电池处于"放电"状态时的电池功率；
     */
    @Column(name = "max_out_load")
    private Double maxOutLoad;

    /**
     * 实际功率kw
     */
    @Column(name = "actual_load")
    private Double actualLoad;

    /**
     * 实际充放电状态
     */
    @Column(name = "actual_strategy")
    private String actualStrategy;
    /**
     * 正在执行的策略
     */
    @Column(name = "strategy")
    private String Strategy;

    /**
     * 消息时间戳 毫秒数转为 年月日 时分秒
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;



    /**
     * 计划充电状态
     */
    @Column(name = "plan_charge_status")
    private Boolean planChargeStatus;


    /**
     * 计划放电状态
     */
    @Column(name = "plan_discharge_status")
    private Boolean planDischargeStatus;


    public BiStorageEnergyResources() {
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Boolean getOnline() { return online; }
    public void setOnline(Boolean online) { this.online = online; }
    public Boolean getIsEnabled() { return isEnabled; }
    public void setIsEnabled(Boolean isEnabled) { this.isEnabled = isEnabled; }
    public String getNodeName() { return nodeName; }
    public void setNodeName(String nodeName) { this.nodeName = nodeName; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public Double getCapacity() { return capacity; }
    public void setCapacity(Double capacity) { this.capacity = capacity; }
    public Double getSoc() { return soc; }
    public void setSoc(Double soc) { this.soc = soc; }
    public Double getSoh() { return soh; }
    public void setSoh(Double soh) { this.soh = soh; }
    public Double getInCapacity() { return inCapacity; }
    public void setInCapacity(Double inCapacity) { this.inCapacity = inCapacity; }
    public Double getOutCapacity() { return outCapacity; }
    public void setOutCapacity(Double outCapacity) { this.outCapacity = outCapacity; }
    public Double getLoad() { return load; }
    public void setLoad(Double load) { this.load = load; }
    public Double getMaxInLoad() { return maxInLoad; }
    public void setMaxInLoad(Double maxInLoad) { this.maxInLoad = maxInLoad; }
    public Double getMaxOutLoad() { return maxOutLoad; }
    public void setMaxOutLoad(Double maxOutLoad) { this.maxOutLoad = maxOutLoad; }
    public Double getActualLoad() { return actualLoad; }
    public void setActualLoad(Double actualLoad) { this.actualLoad = actualLoad; }
    public String getActualStrategy() { return actualStrategy; }
    public void setActualStrategy(String actualStrategy) { this.actualStrategy = actualStrategy; }
    public String getStrategy() { return Strategy; }
    public void setStrategy(String Strategy) { this.Strategy = Strategy; }
    public Date getTs() { return ts; }
    public void setTs(Date ts) { this.ts = ts; }
    public Date getCreatedTime() { return createdTime; }
    public void setCreatedTime(Date createdTime) { this.createdTime = createdTime; }
    public Date getUpdateTime() { return updateTime; }
    public void setUpdateTime(Date updateTime) { this.updateTime = updateTime; }
    public Boolean getPlanChargeStatus() { return planChargeStatus; }
    public void setPlanChargeStatus(Boolean planChargeStatus) { this.planChargeStatus = planChargeStatus; }
    public Boolean getPlanDischargeStatus() { return planDischargeStatus; }
    public void setPlanDischargeStatus(Boolean planDischargeStatus) { this.planDischargeStatus = planDischargeStatus; }
}