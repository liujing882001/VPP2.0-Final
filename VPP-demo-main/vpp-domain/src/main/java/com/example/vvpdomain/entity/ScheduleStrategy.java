package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zph
 * @description 运行策略
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "schedule_strategy")
public class ScheduleStrategy implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * id
     */
    @Column(name = "strategy_id")
    private String strategyId;

    /**
     * 策略名称
     */
    @Column(name = "strategy_name")
    private String strategyName;


    /**
     * 是否参与自动需求响应
     */
    @Column(name = "is_demand_response")
    private boolean isDemandResponse;

    /**
     * 策略启停状态
     */
    @Column(name = "is_strategy_status")
    private boolean isStrategyStatus;

    /**
     * 下发命令的json表达式
     */
    @Column(name = "cron_expression")
    private String cronExpression;


    /**
     * 下发命令的json表达式
     */
    @Column(name = "cmd_expression")
    private String cmdExpression;


    /**
     * 策略类型
     * 0 空调策略
     * 1 其他策略(针对照明、基站充电桩等，可对设备的启动/停止进行控制)
     */
    @Column(name = "strategy_type")
    private int strategyType;


    /**
     * 运行策略类型 0 一次性 或者 1 周期性
     */
    @Column(name = "run_strategy")
    private int runStrategy;

    /**
     * 改策略控制的设备
     */
    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    @JoinTable(name = "schedule_strategy_device", joinColumns = @JoinColumn(name = "strategy_id"),
            inverseJoinColumns = @JoinColumn(name = "device_id"))
    private List<Device> deviceList;

    /**
     * 用户id
     */
    @Column(name = "user_id")
    private String userId;


    // 关联关系多对一，级联关系，可更新，持久化， 获取方式懒加载；
    @ManyToOne(targetEntity = User.class,
            cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id"
            , insertable = false, updatable = false)
    private User user;

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
     * 电网类型 1 国网 或者 2 电网
     */
    @Column(name = "power_grid")
    private Integer powerGrid;


    public ScheduleStrategy() {
    }

    // Manual getters to ensure compilation
    public List<Device> getDeviceList() { return deviceList; }
    public String getCmdExpression() { return cmdExpression; }
    public int getStrategyType() { return strategyType; }
    public boolean isDemandResponse() { return isDemandResponse; }
    public boolean isStrategyStatus() { return isStrategyStatus; }
}