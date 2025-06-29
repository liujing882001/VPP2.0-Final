package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "schedule_strategy_view")
public class ScheduleStrategyView {

    @Id

    /**
     * id
     */
    @Column(name = "id")
    private String id;

    @Column(name = "strategy_id")
    private String strategyId;

    /**
     * 是否参与自动需求响应
     */
    @Column(name = "is_demand_response")
    private boolean isDemandResponse;

    @Column(name = "node_id")
    private String nodeId;

    @Column(name = "node_name")
    private String nodeName;

    @Column(name = "device_sn")
    private String deviceSn;

    @Column(name = "device_name")
    private String deviceName;


    @Column(name = "device_model")
    private String deviceModel;

    /**
     * 额定功率
     */
    @Column(name = "device_rated_power")
    private double deviceRatedPower;

    /**
     * 实时功率
     */
    @Column(name = "device_real_power")
    private double deviceRealPower;

    /**
     * 设备品牌
     */
    @Column(name = "device_brand")
    private String deviceBrand;

    /**
     * 是否在线
     */
    @Column(name = "online")
    private Boolean online;


    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "created_time", updatable = false)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;


}
