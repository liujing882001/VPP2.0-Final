package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cfg_storage_energy_strategy_power_96")
public class CfgStorageEnergyStrategyPower96 implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    /**
     * 参数主键=键名类型父菜单id
     */
    @Column(name = "id")
    private String id;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 系统id
     */
    @Column(name = "system_id")
    private String systemId;

    /**
     * 生效时间 yyyy-MM-dd
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-mm-dd")
    @Column(name = "effective_date")
    private Date effectiveDate;

    /**
     * 时间范围 00:00-00:15
     */
    @Column(name = "time_scope")
    private String timeScope;

    /**
     * 开始时间
     */
    @Column(name = "s_time")
    private String sTime;

    /**
     * 结束时间
     */
    @Column(name = "e_time")
    private String eTime;

    /**
     * 功率
     */
    @Column(name = "power")
    private Double power;

    /**
     * 充放电策略
     */
    @Column(name = "strategy")
    private String strategy;

    /**
     * 策略下发状态(0-未下发,1-已下发)
     */
    @Column(name = "distribute_status")
    private Integer distributeStatus;

    @Column(name = "policy_model")
    private Integer policyModel;

    public String getStrategy() { return strategy; }
    public Double getPower() { return power; }

    // 手动添加缺失的getter方法以确保编译通过
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public String getETime() {
        return eTime;
    }
}
