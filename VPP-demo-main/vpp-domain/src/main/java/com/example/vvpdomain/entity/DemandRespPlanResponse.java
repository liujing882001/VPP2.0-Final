package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 每栋楼平均功率-南网
 * @date 2022-12-14
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_plan_response")
public class DemandRespPlanResponse implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     * 表字段： demand_resp_plan_response.r_id
     */
    @Id
    @Column(name = "r_id")
    @ApiModelProperty("id")
    private String rId;

    /**
     * 负荷所含大楼、充电站等资源唯一标识
     * 表字段： demand_resp_plan_response.resource_id
     */
    @Column(name = "resource_id")
    private String resourceId;

    /**
     * 时间
     * 表字段： demand_resp_plan_response.response_time
     */
    @Column(name = "response_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date responseTime;

    /**
     * 日期
     * 表字段： demand_resp_plan_response.response_date
     */
    @Column(name = "response_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date responseDate;

    /**
     * 前15分钟平均功率值（kW）
     * 表字段： demand_resp_plan_response.avg_power
     */
    @Column(name = "avg_power")
    private Double avgPower;

    /**
     * 邀约计划id
     * 表字段： demand_resp_plan_response.invitation_id
     */
    @Column(name = "invitation_id")
    private String invitationId;

    /**
     * 基线负荷
     * 表字段： demand_resp_plan_response.baseline
     */
    @Column(name = "baseline")
    private Double baseline;

    public DemandRespPlanResponse(){}

}