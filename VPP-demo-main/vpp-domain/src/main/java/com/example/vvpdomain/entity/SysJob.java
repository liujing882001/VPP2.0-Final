package com.example.vvpdomain.entity;


import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description 定时任务调度表
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "sys_job")
public class SysJob implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    /**
     * 任务id
     */
    @Column(name = "job_id")
    @ExcelProperty(value = "任务序号")
    private Long jobId;

    /**
     * 任务名称
     */
    @ApiModelProperty(value = "工作名称", name = "jobName", required = true)
    @Column(name = "job_name")
    @ExcelProperty(value = "任务名称")
    private String jobName;

    /**
     * 任务组名
     */
    @ApiModelProperty(value = "任务组名", name = "jobGroup", required = true)
    @Column(name = "job_group")
    @ExcelProperty(value = "任务组名")
    private String jobGroup;

    /**
     * cron执行表达式
     */
    @Column(name = "cron_expression")
    @ExcelProperty(value = "执行表达式 ")
    private String cronExpression;

    /**
     * 类名称
     */
    @ApiModelProperty(value = "类名称", name = "invokeTarget", required = true)
    @Column(name = "invoke_target")
    @ExcelProperty(value = "调用目标字符串")
    private String invokeTarget;

    /**
     * 计划执行错误策略（1立即执行 2执行一次 3放弃执行）
     */
    @Column(name = "misfire_policy")
    @ExcelProperty(value = "计划策略 ")
    private String misfirePolicy;

    /**
     * 是否并发执行（0允许 1禁止
     */
    @Column(name = "concurrent")
    @ExcelProperty(value = "并发执行")
    private String concurrent;

    /**
     * 状态（0正常 1暂停）
     */
    @ApiModelProperty(value = "工作状态（0正常 1暂停）", name = "status", required = true)
    @Column(name = "status")
    @ExcelProperty(value = "任务状态")
    private String status;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "create_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;


    @Column(name = "create_by")
    private String createBy;
    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


    @Column(name = "update_by")
    private String updateBy;


    @Column(name = "remark")
    private String remark;

    public SysJob() {
    }

}