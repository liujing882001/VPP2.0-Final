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
 * 碳模型-排放因子
 */
@Entity
@Getter
@Setter
@Table(name = "ca_emission_factor")
@EntityListeners(AuditingEntityListener.class)
public class CaEmissionFactor implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表字段： ca_emission_factor.emission_factor_id
     */
    @Id
    @Column(name = "emission_factor_id")
    @ApiModelProperty("排放因子id")
    private String emissionFactorId;

    /**
     * 表字段： ca_emission_factor.province
     */
    @Column(name = "province")
    @ApiModelProperty("省份")
    private String province;

    /**
     * 表字段： ca_emission_factor.emission_factor_num
     */
    @Column(name = "emission_factor_num")
    @ApiModelProperty("排放因子编号")
    private Integer emissionFactorNum;

    /**
     * 表字段： ca_emission_factor.emission_factor_num
     */
    @Column(name = "emission_factor_name")
    @ApiModelProperty("排放因子名称")
    private String emissionFactorName;

    /**
     * co2排放量
     * 表字段： ca_emission_factor.co2
     */
    @Column(name = "co2")
    @ApiModelProperty("co2排放量")
    private Double co2;

    /**
     * 单位
     * 表字段： ca_emission_factor.unit
     */
    @Column(name = "unit")
    @ApiModelProperty("单位")
    private String unit;

    /**
     * 范围(1-范围一 2-范围二 3-范围三)
     * 表字段： ca_emission_factor.scope_type
     */
    @Column(name = "scope_type")
    @ApiModelProperty("范围(1-范围一 2-范围二 3-范围三)")
    private Integer scopeType;

    /**
     * 燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）
     * 表字段： ca_emission_factor.discharge_type
     */
    @Column(name = "discharge_type")
    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

    /**
     * 初始值 表字段： ca_emission_factor.description
     */
    @Column(name = "description")
    @ApiModelProperty("备注")
    private String description;

    /**
     * 初始值 表字段： ca_emission_factor.co2
     */
    @Column(name = "initial_value")
    @ApiModelProperty("初始值")
    private Double initialValue;

    /**
     * 创建时间 表字段： ca_scope.created_time
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
     * 状态（0-删除 1-正常） 表字段： ca_scope.s_status
     */
    @Column(name = "s_status")
    @ApiModelProperty("状态（0-删除 1-正常）")
    private Integer sStatus;

    public CaEmissionFactor() {

    }
}