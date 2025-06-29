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
     * 可充容量= ∑ 单个电站的容量*（1-SOC）
     */
    @Column(name = "in_capacity")
    private Double inCapacity;

    /**
     * 当前可放容量kwh
     * 可放容量= ∑ 单个电站的容量*（SOC）；
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

}