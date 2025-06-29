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
 * @description 需求响应收益
 * @date 2024-03-19
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_profit")
public class DemandProfit implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 节点收益ID
     * 表字段： demand_profit.profit_id
     */
    @Id
    @Column(name = "profit_id")
    @ApiModelProperty("节点收益ID")
    private String profitId;


    /**
     * 响应任务id
     * 表字段： demand_profit.node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty("节点ID")
    private String nodeId;

    /**
     * 节点收益时间，到天
     * 表字段： demand_profit.profit_date
     */
    @Column(name = "profit_date", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date profitDate;

    /**
     * 创建时间
     * 表字段： demand_profit.created_time
     */
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 更新时间
     * 表字段： demand_profit.update_time
     */
    @Column(name = "update_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 削峰收益（元）
     * 表字段： demand_profit.peak_profit
     */
    @Column(name = "peak_profit")
    private Double peakProfit;

    /**
     * 填谷收益（元）
     * 表字段： demand_profit.fill_profit
     */
    @Column(name = "fill_profit")
    private Double fillProfit;

    /**
     * 容量收益（元）
     * 表字段： demand_profit.volume_profit
     */
    @Column(name = "volume_profit")
    private Double volumeProfit;

    /**
     * 总收益（元）
     * 表字段： demand_profit.total_profit
     */
    @Column(name = "total_profit")
    private Double totalProfit;

    /**
     * 削峰电量
     * 表字段： demand_profit.peak_power
     */
    @Column(name = "peak_power")
    private Double peakPower;

    /**
     * 填谷电量
     * 表字段： demand_profit.fill_power
     */
    @Column(name = "fill_power")
    private Double fillPower;

    /**
     * 申报负荷
     * 表字段： demand_profit.declare_load
     */
    @Column(name = "declare_load")
    private Double declareLoad;

    /**
     * 节点收益时间，到年
     * 表字段： demand_profit.profit_year
     */
    @Column(name = "profit_year")
    private Integer profitYear;

    /**
     * 节点收益时间，到月
     * 表字段： demand_profit.profit_month
     */
    @Column(name = "profit_month")
    private Integer profitMonth;

    /**
     * 节点收益时间，年月
     * 表字段： demand_profit.profit_year_month
     */
    @Column(name = "profit_year_month")
    private Integer profitYearMonth;

    /**
     * 节点收益时间字符串格式，年-月
     * 表字段： demand_profit.profit_year_month_str
     */
    @Column(name = "profit_year_month_str")
    private String profitYearMonthStr;

}