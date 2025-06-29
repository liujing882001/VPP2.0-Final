package com.example.vvpdomain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "iot_ts_alarm_information")
public class IotTsAlarmInformation implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "id")
    private String id;




    /**
     * 电站状态 在线离线
     */
    @Column(name = "online")
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
     * 可充功率=∑单个电站电池处于“充电”状态时的电池功率
     */
    @Column(name = "max_in_load")
    private Double maxInLoad;

    /**
     * 最大可放功率kw
     * 可放功率= ∑单个电站电池处于“放电”状态时的电池功率；
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
    private Date ts;

    /**
     * created_time
     */

    @Column(name = "created_time")
    private Date createdTime;

    /**
     * update_time
     */
    @Column(name = "update_time")
    private Date updateTime;





    /**
     * 计划充电状态
     */
    @Column(name = "plan_charge_status")
    private Boolean planChargeStatus;


    /**
     * 计划放电状态
     */
    @Column(name = "plan_discharge_status")
    private Boolean planDischargeStatus;


    @Column(name = "alarm_type")
    private String alarmType;

    @Column(name = "alarm_duration")
    private Timestamp alarmDuration;

    @Column(name = "alarm_level")
    private String alarmLevel;
    @Column(name = "station_name")
    private String stationName;

    @Column(name = "alarm_information")
    private String alarmInformation;


    @Column(name = "alarm_status")
    private String alarmStatus;


    @Column(name = "alarm_operation")
    private String alarmOperation;


    @Column(name = "alarm_end_time")
    private Timestamp alarmEndTime;

    //设备分类-所属系统名称
    @Column(name = "system_name")
    private String systemName;

    //设备分类-所属系统名称
    @Column(name = "phone")
    private String phone;

    //设备分类-所属系统名称
    @Column(name = "alarm_start_time")
    private Timestamp alarmStartTime;

    //所属系统id
    @Column(name = "system_id")
    private String systemId;
    //设备id，设备序列号
    @Column(name = "device_id")
    private String deviceId;
    //节点类型名称
    @Column(name = "node_type_name")
    private String nodeTypeName;
    //点位id
    @Column(name = "point_id")
    private String pointId;
    //点位名称
    @Column(name = "point_name")
    private String pointName;

    @Column(name = "severity")
    private Integer severity;
    @Column(name = "severity_desc")
    private String severityDesc;
    @Column(name = "status")
    private Integer status;
    @Column(name = "status_desc")
    private String statusDesc;

    /**
     * 报警内容-弃用字段-用alarm-information
     */
    @Column(name = "additional_info")
    private String additionalInfo;


    @Column(name = "device_name")
    private String deviceName;
    @Column(name = "alarm_id")
    private String alarmID;
    /**
     * 检索报警开始时间 yyyy-MM
     */
    @Column(name = "index_start_ts")
    private Date indexStartTs;

    @Column(name = "start_ts")
    private Timestamp startTs;



}
