package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 日前预调度计划-南网
 * @date 2022-12-14
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_plan")
public class DemandRespPlan implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 计划id
     * 表字段： demand_resp_plan.plan_id
     */
    @Id
    @Column(name = "plan_id")
    @ApiModelProperty("计划id")
    private String planId;

    /**
     * 计划编号
     * 表字段： demand_resp_plan.plan_code
     */
    @Column(name = "plan_code")
    private String planCode;

    /**
     * 计划名称
     * 表字段： demand_resp_plan.plan_name
     */
    @Column(name = "plan_name")
    private String planName;

    /**
     * 响应日期
     * 表字段： demand_resp_plan.plan_time
     */
    @Column(name = "plan_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date planTime;

    /**
     * 邀约计划id
     * 表字段： demand_resp_plan.invitation_id
     */
    @Column(name = "invitation_id")
    private String invitationId;

    /**
     * 负荷聚合商统一社会信用代码
     * 表字段： demand_resp_plan.credit_code
     */
    @Column(name = "credit_code")
    private String creditCode;


    /**
     * 操作者
     */
    @Column(name = "create_by")
    @ApiModelProperty("操作者")
    private String createBy;

    /**
     * 表字段： demand_resp_plan.create_time
     */
    @Column(name = "create_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;

    public DemandRespPlan(){}

}