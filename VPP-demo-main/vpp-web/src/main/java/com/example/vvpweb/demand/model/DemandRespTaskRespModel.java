package com.example.vvpweb.demand.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author maoyating
 * @description 需求响应任务
 * @date 2022-08-09
 */
@Data
public class DemandRespTaskRespModel {

    /**
     * 响应任务id
     */
    private String respId;

    /**
     * 响应时段(开始)
     * 表字段： demand_resp_task.rs_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date rsTime;

    /**
     * 响应时段(结束)
     * 表字段： demand_resp_task.re_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date reTime;

    /**
     * 响应日期
     * 表字段： demand_resp_task.rs_date
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date rsDate;

    /**
     * 任务编码
     * 表字段： demand_resp_task.task_code
     */
    private Long taskCode;

    /**
     * 响应负荷，单位（kW）
     * 表字段： demand_resp_task.resp_load
     */
    private Double respLoad;

    /**
     * 响应类型(1-削峰响应 2-填谷响应)
     * 表字段： demand_resp_task.resp_type
     */
    private Integer respType;

    /**
     * 响应方式(1-约定响应 2-实时响应)
     * 表字段： demand_resp_task.resp_mode
     */
    private Integer respMode;

    /**
     * 响应补贴（元/kWh）
     * 表字段： demand_resp_task.resp_subsidy
     */
    private Double respSubsidy;

    /**
     * 表字段： demand_resp_task.create_by
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 表字段： demand_resp_task.create_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 表字段： demand_resp_task.update_by
     */
    private String updateBy;

    /**
     * 表字段： demand_resp_task.update_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 状态（0-删除 1-未开始 2-执行中 3-已完成 4-不参加申报）
     * 表字段： demand_resp_task.d_status
     */
    private Integer dStatus;

    /**
     * 定时任务id
     * 表字段： demand_resp_task.job_id
     */
    private Long jobId;

    /**
     * 预估收益
     * 表字段： demand_resp_task.profit
     */
    private Double profit;

    /**
     * 实际响应负荷（kW）
     *
     * @Transient 代表表中不存在的字段
     */
    private Double actualLoad;

    /**
     * 实际响应电量（kWh）
     */
    private Double actualPower;

    /**
     * 调控达标率
     */
    private Double complianceRate;

    /**
     * 响应级别(1-日前 2-小时 3-分钟 4-秒级)
     */
    private Integer respLevel;


    /**
     * 反馈截止时间
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date feedbackTime;

    /**
     * 总申报负荷
     * 表字段： demand_resp_task
     */
    private Double declareLoad;


    /**
     * 申报状态（1-未申报 2-已申报 3-已申报出清成功）--南网增加状态
     */
    private Integer declareStatus;

    /**
     * 调节负荷
     */
    private Double adjustLoad;
    /**
     * 预测负荷
     */
    @ApiModelProperty("预测负荷")
    private String forecastLoad;

    /**
     * 基线负荷
     */
    @ApiModelProperty("基线负荷")
    private Double baseLoad;
    /**
     * 实际负荷
     */
    @ApiModelProperty("实际负荷")
    private Double nowLoad;
}