package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 价格、总功率-南网
 * @date 2022-12-14
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_plan_price")
public class DemandRespPlanPrice implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     * 表字段： demand_resp_plan_price.p_id
     */
    @Id
    @Column(name = "p_id")
    @ApiModelProperty("id")
    private String pId;

    /**
     * 时间
     * 表字段： demand_resp_plan_price.response_time
     */
    @Column(name = "response_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date responseTime;

    /**
     * 日期
     * 表字段： demand_resp_plan_price.response_date
     */
    @Column(name = "response_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date responseDate;

    /**
     * 前15分钟总功率值（kW）
     * 表字段： demand_resp_plan_price.total_power
     */
    @Column(name = "total_power")
    private Double totalPower;

    /**
     * 邀约计划id
     * 表字段： demand_resp_plan_price.invitation_id
     */
    @Column(name = "invitation_id")
    private String invitationId;

    /**
     * 运行价格曲线(元/MWh)
     * 表字段： demand_resp_plan_price.price
     */
    @Column(name = "price")
    private Double price;

    public DemandRespPlanPrice(){}

}