package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
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
 * @description 辅助服务
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ancillary_services")
public class AncillaryServices implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 辅助任务id
     * 表字段： ancillary_services.as_id
     */
    @Id
    @Column(name = "as_id")
    private String asId;

    /**
     * 辅助时段(开始)
     * 表字段： ancillary_services.ass_time
     */
    @Column(name = "ass_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date assTime;

    /**
     * 辅助时段(结束)
     * 表字段： ancillary_services.ase_time
     */
    @Column(name = "ase_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm:ss")
    private Date aseTime;

    /**
     * 辅助日期
     * 表字段： ancillary_services.ass_date
     */
    @Column(name = "ass_date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date assDate;

    /**
     * 任务编码
     * 表字段： ancillary_services.task_code
     */
    @Column(name = "task_code")
    private Long taskCode;

    /**
     * 辅助规模，单位（kW）
     * 表字段： ancillary_services.as_load
     */
    @ApiModelProperty("辅助规模，单位（kW）")
    @Column(name = "as_load")
    private Double asLoad;

    /**
     * 辅助类型( 1-调峰、2-调频、3-备用)
     * 表字段： ancillary_services.as_type
     */
    @ApiModelProperty("辅助类型( 1-调峰、2-调频、3-备用)")
    @Column(name = "as_type")
    private Integer asType;

    /**
     * 辅助补贴（元/kWh）
     * 表字段： ancillary_services.as_subsidy
     */
    @ApiModelProperty("辅助补贴（元/kWh）")
    @Column(name = "as_subsidy")
    private Double asSubsidy;

    /**
     * 状态（0-删除 1-未开始 2-执行中 3-已完成）
     * 表字段： ancillary_services.a_status
     */
    @Column(name = "a_status")
    private Integer aStatus;

    /**
     * 表字段： ancillary_services.create_by
     */
    @Column(name = "create_by")
    private String createBy;

    /**
     * 表字段： ancillary_services.create_time
     */
    @Column(name = "create_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createTime;

    /**
     * 表字段： ancillary_services.update_by
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 表字段： ancillary_services.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 定时任务id
     * 表字段： ancillary_services.job_id
     */
    @Column(name = "job_id")
    private Long jobId;

    /**
     * 预估收益
     * 表字段： ancillary_services.profit
     */
    @ApiModelProperty("预估收益")
    @Column(name = "profit")
    private Double profit;

    /**
     * 辅助服务负荷（kW）
     * 表字段： ancillary_services.actual_load
     *
     * @Transient 代表表中不存在的字段
     */
    @ApiModelProperty("辅助服务负荷（kW）|总调节负荷")
    @Column(name = "actual_load")
    private Double actualLoad;

    /**
     * 总调节电量（kWh）
     * 表字段： ancillary_services.actual_power
     */
    @ApiModelProperty("总调节电量（kWh）")
    @Column(name = "actual_power")
    private Double actualPower;

    /**
     * 响应策略
     */
    @OneToMany(mappedBy = "ancillaryServices",
            targetEntity = AncillarySStrategy.class,
            fetch = FetchType.EAGER,
            cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    @Fetch(FetchMode.SUBSELECT)
    private List<AncillarySStrategy> strategyList;

}