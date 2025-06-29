package com.example.vvpdomain.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Table(name = "aliyun_message_log")
@Data
public class AliyunMessageLog {
    private static final long serialVersionUID = 1L;

    /**
     * 短信唯一标识
     */
    @Id
    @Column(name = "message_id")
    private String messageId;

    /**
     * 设备名称
     */
    @Column(name = "device_name")
    private String deviceName;

    /**
     * 报警内容
     */
    @Column(name = "alarm_content")
    private String alarmContent;

    /**
     * 电话号码
     */
    @Column(name = "phone")
    private String phone;

    /**
     * 是否在线
     */
    @Column(name = "online")
    private Boolean online;

    /**
     * 创建时间
     */
    @Column(name = "created_time")
    private Timestamp createdTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Timestamp updateTime;

    /**
     * 更新时间
     */
    @Column(name = "ts")
    private Timestamp ts;

    /**
     * 电站名称
     */
    @Column(name = "node_name")
    private String nodeName;

    /**
     * bi_storage_energy_resources 中的 id
     */
    @Column(name = "node_id")
    private String nodeId;

    /**
     * sys_user 表中的 id
     */
    @Column(name = "user_id")
    private String userId;
}
