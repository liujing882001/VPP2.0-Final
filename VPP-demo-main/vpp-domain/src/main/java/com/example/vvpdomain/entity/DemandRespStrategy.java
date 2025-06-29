package com.example.vvpdomain.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author maoyating
 * @description 需求响应策略
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_resp_strategy")
public class DemandRespStrategy implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 策略id
     * 表字段： demand_resp_strategy.s_id
     */
    @Id
    @Column(name = "s_id")
    @ApiModelProperty("策略id")
    private String sId;


    /**
     * 响应任务id
     * 表字段： demand_resp_strategy.resp_id
     */
    @ApiModelProperty("响应任务id")
    @ManyToOne(targetEntity = DemandRespTask.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "resp_id", referencedColumnName = "resp_id")
    private DemandRespTask respTask;

    /**
     * 所属策略id
     */
    @ManyToOne(targetEntity = ScheduleStrategy.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "strategy_id", referencedColumnName = "strategy_id")
    private ScheduleStrategy scheduleStrategy;

    /**
     * 操作者
     */
    @Column(name = "create_by")
    @ApiModelProperty("操作者")
    private String createBy;

}