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
@Table(name = "ca_collection_model")
@EntityListeners(AuditingEntityListener.class)
public class CaCollectionModel implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 表字段： ca_collection_model.collection_model_id
     */
    @Id
    @Column(name = "collection_model_id")
    @ApiModelProperty("采集模型ID")
    private String collectionModelId;

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
     * 范围(1-范围一 2-范围二 3-范围三)
     * 表字段： ca_emission_factor.scope_type
     */
    @Column(name = "scope_type")
    @ApiModelProperty("范围(1-范围一 2-范围二 3-范围三)")
    private Integer scopeType;

    /**
     * 采集方式 （0-自动 1-手动） 表字段： ca_emission_factor.co2
     */
    @Column(name = "collect_mode")
    @ApiModelProperty("采集方式")
    private Integer collectMode;

    /**
     * 系统ID 表字段： ca_emission_factor.co2
     */
    @Column(name = "system_id")
    @ApiModelProperty("系统ID")
    private String systemId;

    /**
     * 系统名称 表字段： ca_emission_factor.co2
     */
    @Column(name = "system_name")
    @ApiModelProperty("系统名")
    private String systemName;

    /**
     * 设备 表字段： ca_emission_factor.co2
     */
    @Column(name = "device_id")
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 系统名称 表字段： ca_emission_factor.co2
     */
    @Column(name = "device_name")
    @ApiModelProperty("设备名")
    private String deviceName;

    /**
     * 设备 表字段： ca_emission_factor.co2
     */
    @Column(name = "data_point_id")
    @ApiModelProperty("数据点ID")
    private String dataPointId;

    /**
     * 系统名称 表字段： ca_emission_factor.co2
     */
    @Column(name = "data_point_name")
    @ApiModelProperty("数据点名称")
    private String dataPointName;

    /**
     * 节点： ca_emission_factor.node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty("楼宇节点id")
    private String nodeId;

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

    public CaCollectionModel() {

    }
}