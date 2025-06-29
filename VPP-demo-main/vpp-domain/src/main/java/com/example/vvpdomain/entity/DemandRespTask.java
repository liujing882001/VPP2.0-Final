package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author maoyating
 * @description 需求响应任务
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_task")
public class DemandRespTask implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 响应任务id
     */
    @Id
    @Column(name = "resp_id")
    private String respId;

    /**
     * 响应时段(开始)  update by myt 20231218  南网字段变更
     * 表字段： demand_resp_task.rs_time
     */
    @Column(name = "rs_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date rsTime;

    /**
     * 响应时段(结束) update by myt 20231218  南网字段变更
     * 表字段： demand_resp_task.re_time
     */
    @Column(name = "re_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date reTime;

    /**
     * 响应日期
     * 表字段： demand_resp_task.rs_date
     */
    @Column(name = "rs_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date rsDate;

    /**
     * 任务编码
     * 表字段： demand_resp_task.task_code
     */
    @Column(name = "task_code")
    private Long taskCode;

    /**
     * 响应负荷，单位（kW）
     * 表字段： demand_resp_task.resp_load
     */
    @Column(name = "resp_load")
    private Double respLoad;

    /**
     * 响应类型(1-削峰响应 2-填谷响应 3-南网辅助服务)
     * 表字段： demand_resp_task.resp_type
     */
    @Column(name = "resp_type")
    private Integer respType;

    /**
     * 响应补贴（元/kWh）
     * 表字段： demand_resp_task.resp_subsidy
     */
    @Column(name = "resp_subsidy")
    private Double respSubsidy;

    /**
     * 表字段： demand_resp_task.create_by
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 表字段： demand_resp_task.create_time
     */
    @Column(name = "create_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;

    /**
     * 表字段： demand_resp_task.update_by
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 表字段： demand_resp_task.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 状态（0-删除 1-未开始 2-执行中 3-已完成 4-不参加申报）
     * 表字段： demand_resp_task.d_status
     */
    @Column(name = "d_status")
    private Integer dStatus;

    /**
     * 定时任务id
     * 表字段： demand_resp_task.job_id
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * 预估收益
     * 表字段： demand_resp_task.profit
     */
    @Column(name = "profit")
    private Double profit;

    /**
     * 实际响应负荷（kW）
     * 表字段： demand_resp_task.actual_load
     *
     * @Transient 代表表中不存在的字段
     */
    @Column(name = "actual_load")
    private Double actualLoad;

    /**
     * 实际响应电量（kWh）
     * 表字段： demand_resp_task.actual_power
     */
    @Column(name = "actual_power")
    private Double actualPower;

    /**
     * 响应策略
     */
    @OneToMany(mappedBy = "respTask",
            targetEntity = DemandRespStrategy.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @Fetch(FetchMode.SUBSELECT)
    private List<DemandRespStrategy> demandRespList;

    /**
     * 调控达标率
     * 表字段： demand_resp_task.compliance_rate
     */
    @Column(name = "compliance_rate")
    private Double complianceRate;

    /**
     * 响应级别(1-日前 2-小时 3-分钟 4-秒级)
     * 表字段： demand_resp_task.resp_level
     */
    @Column(name = "resp_level")
    private Integer respLevel;

    /**
     * 反馈截止时间
     * 表字段： demand_resp_task.feedback_time
     */
    @Column(name = "feedback_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date feedbackTime;

    /**
     * 容量收益（元）
     * 表字段： demand_resp_task.volume_profit
     */
    @Column(name = "volume_profit")
    private Double volumeProfit;

    /**
     * 总申报负荷
     * 表字段： demand_resp_task.declare_load
     */
    @Column(name = "declare_load")
    private Double declareLoad;

    /**
     * 总申报电量
     * 表字段： demand_resp_task.declare_power
     */
    @Column(name = "declare_power")
    private Double declarePower;

    /**
     * 申报状态（1-未申报 2-已申报 3-已申报出清成功）
     * 表字段： demand_resp_task.declare_status
     */
    @Column(name = "declare_status")
    private Integer declareStatus;

    @Column(name = "power_grid")
    private Integer powerGrid;

    public DemandRespTask() {

    }

}