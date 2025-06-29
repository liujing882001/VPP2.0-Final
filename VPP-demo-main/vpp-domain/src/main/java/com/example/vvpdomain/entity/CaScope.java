package com.example.vvpdomain.entity;

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
 * 碳模型-范围
 * add by maoyating
 */
@Entity
@Getter
@Setter
@Table(name = "ca_scope")
@EntityListeners(AuditingEntityListener.class)
public class CaScope implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表字段： ca_scope.scope_id
     */
    @Id
    @Column(name = "scope_id")
    @ApiModelProperty("范围id")
    private String scopeId;

    /**
     * 范围(1-范围一 2-范围二 3-范围三)
     * 表字段： ca_scope.scope_type
     */
    @Column(name = "scope_type")
    @ApiModelProperty("范围(1-范围一 2-范围二 3-范围三)")
    private Integer scopeType;

    /**
     * 燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
     * 表字段： ca_scope.discharge_type
     */
    @Column(name = "discharge_type")
    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

    /**
     * 燃烧排放实体（11-天然气 12-煤气 13-柴油 21-公务车 31-冷机制冷剂 32-空调氟利昂 33-灭火器）
     * 表字段： ca_scope.discharge_entity
     */
    @Column(name = "discharge_entity")
    @ApiModelProperty("燃烧排放实体（11-天然气 12-煤气 13-柴油 21-公务车 31-冷机制冷剂 32-空调氟利昂 33-灭火器）")
    private Integer dischargeEntity;

    /**
     * 冷机参数（1-冷机制冷剂类型 2-每台冷机制冷剂数量 3-冷机数量）
     * 表字段： ca_scope.refrigerator
     */
    @Column(name = "refrigerator")
    @ApiModelProperty("冷机参数（1-冷机制冷剂类型 2-每台冷机制冷剂数量 3-冷机数量）")
    private Integer refrigerator;

    /**
     * 范围二购买内容（1-外购电力（kWh）2-外购热力（KJ））
     * 表字段： ca_scope.scope_two
     */
    @Column(name = "scope_two")
    @ApiModelProperty("范围二购买内容（1-外购电力（kWh）2-外购热力（KJ））")
    private Integer scopeTwo;

    /**
     * 范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）
     * 表字段： ca_scope.scope_three
     */
    @Column(name = "scope_three")
    @ApiModelProperty("范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）")
    private Integer scopeThree;

    /**
     * 值
     * 表字段： ca_scope.discharge_value
     */
    @Column(name = "discharge_value")
    @ApiModelProperty("值")
    private Double dischargeValue;

    /**
     * 年份
     * 表字段： ca_scope.ca_year
     */
    @Column(name = "ca_year")
    @ApiModelProperty("年份")
    private Integer caYear;

    /**
     * 月份
     * 表字段： ca_scope.ca_month
     */
    @Column(name = "ca_month")
    @ApiModelProperty("月份")
    private Integer caMonth;

    /**
     * 年月
     * 表字段： ca_scope.ca_year_month
     */
    @Column(name = "ca_year_month")
    @ApiModelProperty("年月")
    private Integer caYearMonth;

    /**
     * 表字段： ca_scope.created_time
     */
    @ApiModelProperty("创建时间")
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * 表字段： ca_scope.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 状态（0-删除 1-正常）
     * 表字段： ca_scope.s_status
     */
    @Column(name = "s_status")
    @ApiModelProperty("状态（0-删除 1-正常）")
    private Integer sStatus;

    @Column(name = "node_id")
    @ApiModelProperty("楼宇节点id")
    private String nodeId;

    public CaScope() {

    }
}