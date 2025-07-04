package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@Entity
@Data
@Table(name = "bi_storage_energy_log")
@EntityListeners(AuditingEntityListener.class)
public class BiStorageEnergyLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    /**
     * id
     */
    @Column(name = "id")
    private String id;

    /**
     * 电站状态 在线离线
     */
    @Column(name = "`online`")
    private Boolean online;

    /**
     * 建设中/已完成，默认false 为建设中
     */
    @Column(name = "is_enabled")
    private Boolean isEnabled;

    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;


    /**
     * 电站名称id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 电站容量
     */
    @Column(name = "capacity")
    private Double capacity;
    /**
     * soc
     */
    @Column(name = "soc")
    private Double soc;

    /**
     * soh
     */
    @Column(name = "soh")
    private Double soh;

    /**
     * 当前可充容量kwh
     * 可充容量= ∑ 单个电站的容量*（1-SOC）
     */
    @Column(name = "in_capacity")
    private Double inCapacity;

    /**
     * 当前可放容量kwh
     * 可放容量= ∑ 单个电站的容量*（SOC）；
     */
    @Column(name = "out_capacity")
    private Double outCapacity;

    /**
     * 电站功率
     */
    @Column(name = "`load`")
    private Double load;
    /**
     * 最大可充功率kw
     * 可充功率=∑单个电站电池处于"充电"状态时的电池功率
     */
    @Column(name = "max_in_load")
    private Double maxInLoad;

    /**
     * 最大可放功率kw
     * 可放功率= ∑单个电站电池处于"放电"状态时的电池功率；
     */
    @Column(name = "max_out_load")
    private Double maxOutLoad;


    /**
     * 正在执行的策略
     */
    @Column(name = "strategy")
    private String Strategy;

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

    public BiStorageEnergyLog() {

    }
    public BiStorageEnergyLog(BiStorageEnergyResources bis) {
        Date date = new Date();
        this.online = bis.getOnline();
        this.isEnabled = bis.getIsEnabled();
        this.nodeName = bis.getNodeName();
        this.nodeId = bis.getNodeId();
        this.capacity = bis.getCapacity() != null ? bis.getCapacity() : 0.0;
        this.soc = bis.getSoc() != null ? bis.getSoc() : 0.0;
        this.soh = bis.getSoh() != null ? bis.getSoh() : 0.0;
        this.inCapacity = bis.getInCapacity() != null ? bis.getInCapacity() : 0.0;
        this.outCapacity = bis.getOutCapacity() != null ? bis.getOutCapacity() : 0.0;
        this.load = bis.getLoad() != null ? bis.getLoad() : 0.0;
        this.maxInLoad = bis.getMaxInLoad() != null ? bis.getMaxInLoad() : 0.0;
        this.maxOutLoad = bis.getMaxOutLoad() != null ? bis.getMaxOutLoad() : 0.0;
        this.Strategy = bis.getStrategy();
        this.createdTime = date;
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"));
        calendar.setTime(date);
        int minute = calendar.get(Calendar.MINUTE);
        calendar.set(Calendar.MINUTE, minute + 15 - minute % 15);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        this.ts = calendar.getTime();
        this.id = bis.getId() + calendar.getTimeInMillis();
    }

    public Double getSoc() { return soc; }

    public String getStationName() { return nodeName; }
    public Date getStartTime() { return ts; }
    public String getPk() { return id; }
    public String getProperty() { return null; }
    public Double getPrice() { return null; }
    public String getTimeScope() { return null; }
}