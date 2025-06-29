package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author zph
 * @description 光伏资源-电站列表信息
 * @date 2022-08-10
 */
@Entity
@Getter
@Setter
@Table(name = "bi_photovoltaic_resources")
@EntityListeners(AuditingEntityListener.class)
public class BiPvResources implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

    /**
     * 电站状态
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    /**
     * 电站名称id
     */
    @Column(name = "node_id")
    private String nodeId;
    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * 当日发电量
     */
    @Column(name = "now_energy")
    private Double nowEnergy;

    /**
     * 累计发电量
     */
    @Column(name = "`energy`")
    private Double energy;

    /**
     * 实际发电功率
     */
    @Column(name = "`load`")
    private Double load;

    /**
     * 装机容量
     */
    @Column(name = "`capacity`")
    private Double capacity;

    /**
     * 消息时间戳 毫秒数转为 年月日 时分秒
     */
    @Column(name = "ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;

    /**
     * created_time
     */
    @CreatedDate
    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public BiPvResources() {
    }

}