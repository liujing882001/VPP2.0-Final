package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 辅助服务策略
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "ancillary_s_strategy")
public class AncillarySStrategy implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 策略id
     * 表字段： ancillary_s_strategy.s_id
     */
    @Id
    @Column(name = "s_id")
    @ApiModelProperty("策略id")
    private String sId;

    /**
     * 节点id
     * 表字段： ancillary_s_strategy.node_id
     */
    @Column(name = "node_id")
    @ApiModelProperty("节点id")
    private String nodeId;

    /**
     * 节点名称
     * 表字段： ancillary_s_strategy.node_name
     */
    @Column(name = "node_name")
    @ApiModelProperty("节点名称")
    private String nodeName;

    /**
     * 系统id
     * 表字段： ancillary_s_strategy.system_id
     */
    @Column(name = "system_id")
    @ApiModelProperty("系统id")
    private String systemId;

    /**
     * 所属系统名称
     * 表字段： ancillary_s_strategy.system_name
     */
    @Column(name = "system_name")
    @ApiModelProperty("系统名称")
    private String systemName;

    /**
     * 设备ID，设备序列号，唯一码
     * 表字段： ancillary_s_strategy.device_id
     */
    @Column(name = "device_id")
    @ApiModelProperty("设备ID")
    private String deviceId;

    /**
     * 设备名称
     * 表字段： ancillary_s_strategy.device_name
     */
    @Column(name = "device_name")
    @ApiModelProperty("设备名称")
    private String deviceName;

    /**
     * 额定负荷(kW)
     * 表字段： ancillary_s_strategy.device_rated_power
     */
    @Column(name = "device_rated_power")
    @ApiModelProperty("额定负荷(kW)")
    private Double deviceRatedPower;

    /**
     * 实时负荷(kW)
     * 表字段： ancillary_s_strategy.actual_load
     */
    @Column(name = "actual_load")
    @ApiModelProperty("实时负荷(kW)")
    private Double actualLoad;

    /**
     * 策略状态1-开启 2-关闭
     * 表字段： ancillary_s_strategy.s_status
     */
    @Column(name = "s_status")
    @ApiModelProperty("策略状态1-开启 2-关闭")
    private Integer sStatus;

    /**
     * 表字段： ancillary_s_strategy.created_time
     */
    @Column(name = "created_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @CreatedDate
    private Date createdTime;

    /**
     * 表字段： ancillary_s_strategy.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 辅助服务id
     * 表字段： ancillary_s_strategy.as_id
     */
    @ApiModelProperty("辅助服务id")
    @ManyToOne(targetEntity = AncillaryServices.class,
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "as_id", referencedColumnName = "as_id")
    private AncillaryServices ancillaryServices;

}