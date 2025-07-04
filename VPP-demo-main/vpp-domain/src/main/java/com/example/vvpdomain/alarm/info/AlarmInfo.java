package com.example.vvpdomain.alarm.info;


import com.alibaba.fastjson.annotation.JSONField;
import com.example.vvpdomain.alarm.rule.AlarmRule;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "alarm_info")
public class AlarmInfo {
	@Id
	@Column(name = "alarm_id")
	private String alarmId;

	@ManyToOne
	@JoinColumn(name = "rule_id")
	@JSONField(serialize = false)
	private AlarmRule alarmRule;

	@Column(name = "node_id")
	private String nodeId;

	@Column(name = "rule_name")
	private String ruleName;

	@Column(name = "node_name")
	private String nodeName;

	@Column(name = "alarm_type")
	private int alarmType;

	@Column(name = "alarm_level")
	private int alarmLevel;

	@Column(name = "alarm_context")
	private String alarmContext;

	@Column(name = "station_id")
	private String stationId;

	@Column(name = "station_name")
	private String stationName;

	@Column(name = "start_time")
	private Date startTime;

	@Column(name = "end_time")
	private Date endTime;

	@Column(name = "checked")
	private Boolean checked;

	@Column(name = "alarm_status")
	private int alarmStatus;

	@Column(name = "alarm_duration_time")
	private String alarmDurationTime;
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

	@Column(name = "device_type")
	private String deviceType;

	public String getAlarmContext() { return alarmContext; }
	public String getStationName() { return stationName; }
	public java.util.Date getStartTime() { return startTime; }

	// 手动添加所有缺失的getter和setter方法以确保编译通过
	public String getAlarmId() {
		return alarmId;
	}

	public void setAlarmId(String alarmId) {
		this.alarmId = alarmId;
	}

	public AlarmRule getAlarmRule() {
		return alarmRule;
	}

	public void setAlarmRule(AlarmRule alarmRule) {
		this.alarmRule = alarmRule;
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public void setNodeName(String nodeName) {
		this.nodeName = nodeName;
	}

	public int getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(int alarmType) {
		this.alarmType = alarmType;
	}

	public int getAlarmLevel() {
		return alarmLevel;
	}

	public void setAlarmLevel(int alarmLevel) {
		this.alarmLevel = alarmLevel;
	}

	public void setAlarmContext(String alarmContext) {
		this.alarmContext = alarmContext;
	}

	public String getStationId() {
		return stationId;
	}

	public void setStationId(String stationId) {
		this.stationId = stationId;
	}

	public void setStationName(String stationName) {
		this.stationName = stationName;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public Boolean getChecked() {
		return checked;
	}

	public void setChecked(Boolean checked) {
		this.checked = checked;
	}

	public int getAlarmStatus() {
		return alarmStatus;
	}

	public void setAlarmStatus(int alarmStatus) {
		this.alarmStatus = alarmStatus;
	}

	public String getAlarmDurationTime() {
		return alarmDurationTime;
	}

	public void setAlarmDurationTime(String alarmDurationTime) {
		this.alarmDurationTime = alarmDurationTime;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public void setCreatedTime(Date createdTime) {
		this.createdTime = createdTime;
	}

	public Date getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(Date updateTime) {
		this.updateTime = updateTime;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
}
