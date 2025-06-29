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
 * @description 报警记录
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "alarm_log")
public class AlarmLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id

    /**
     * alarm_id
     */
    @Column(name = "alarm_id")
    private String alarmId;

    /**
     * 节点id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * 节点名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * 所属系统Id
     */
    @Column(name = "system_id")
    private String systemId;
    /**
     * 所属系统名称
     */
    @Column(name = "system_name")
    private String systemName;
    /**
     * 设备Id
     */
    @Column(name = "device_id")
    private String deviceId;
    /**
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 点位名称
     */
    @Column(name = "point_id")
    private String pointId;

    /**
     * 点位名称
     */
    @Column(name = "point_name")
    private String pointName;

    /**
     * 严重程度，等级 0 1 2 3  4
     */
    @Column(name = "severity")
    private Integer severity;

    /**
     * 严重程度，等级 报警等级描述 0 紧急1 重要2 次要3 提示 4 其它
     */
    @Column(name = "severity_desc")
    private String severityDesc;

    /**
     * 报警状态0  1 2
     */
    @Column(name = "status")
    private Integer status;

    /**
     * 报警状态描述 0 已恢复 1报警中 2 处理中
     */
    @Column(name = "status_desc")
    private String statusDesc;

    /**
     * 检索报警开始时间 yyyy-MM
     */
    @Column(name = "index_start_ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date indexStartTs;
    /**
     * 报警时间
     */
    @Column(name = "start_ts")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss.SS")
    private Date startTs;

    /**
     * 报警内容
     */
    @Column(name = "additional_info")
    private String additionalInfo;




    /**
     * 告警类型 性能告警 平台告警 设备告警
     */
    @Column(name = "alarm_type")
    private String alarmType;

    /**
     * 告警持续时长
     */
    @Column(name = "alarm_duration")
    private String alarmDuration;


    /**
     * 现在更改为 故障 警告 提示
     *   - 映射关系：
     *
     *     - 0紧急、1重要--故障
     *
     *    - 2次要--警告
     *
     *    - 3提示、4其他--提示
     */
    @Column(name = "alarm_level")
    private String alarmLevel;

    /**
     * stationName是项目名称，对应stationNode表中的stationName。告警展示要用
     */
    @Column(name = "station_name")
    private String stationName;


    /**
     * 报警结束时间
     */
    @Column(name = "alarm_end_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss.SS")
    private Date alarmEndTime;

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

    public AlarmLog() {
    }

}