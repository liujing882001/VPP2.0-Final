package com.example.vvpdomain.alarm.rule;


import lombok.Data;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Data
@Table(name = "alarm_rule")
@EntityListeners(AuditingEntityListener.class)
public class AlarmRule implements Serializable {

	@Id
	@Column(name = "rule_id")
	private String ruleId;


	@Column(name = "rule_name")
	private String ruleName;

	@Column(name = "rule_expression")
	private String ruleExpression;

	@Column(name = "alarm_type")
	private int alarmType;

	@Column(name = "alarm_level")
	private int alarmLevel;

	@Column(name = "device_type")
	private String deviceType;

	@Column(name = "enable")
	private Boolean enable;

	@Column(name = "station_id_list")
	private String station_id_list;

	@Column(name = "phone_number")
	private String phoneNumber;

	@Column(name = "trigger_count")
	private int count;

	@Column(name = "context_template")
	private String contextTemplate;

	@Column(name = "send_msg_alarm")
	private Boolean sendMsgAlarm;

	@Column(name = "send_msg_recovery")
	private Boolean sendMsgRecovery;

	@Column(name = "notify_maintenance_system")
	private Boolean notifyMaintenanceSys;

	@Column(name = "pattern")
	private Integer pattern;

	public String getRuleName() { return ruleName; }
	public String getRuleExpression() { return ruleExpression; }
	public int getCount() { return count; }
	public String getContextTemplate() { return contextTemplate; }
	public Boolean getSendMsgAlarm() { return sendMsgAlarm; }
	public String getPhoneNumber() { return phoneNumber; }
	public Boolean getNotifyMaintenanceSys() { return notifyMaintenanceSys; }
	public Integer getPattern() { return pattern; }

	// 手动添加所有缺失的getter和setter方法以确保编译通过
	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public void setRuleExpression(String ruleExpression) {
		this.ruleExpression = ruleExpression;
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

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public Boolean getEnable() {
		return enable;
	}

	public void setEnable(Boolean enable) {
		this.enable = enable;
	}

	public String getStation_id_list() {
		return station_id_list;
	}

	public void setStation_id_list(String station_id_list) {
		this.station_id_list = station_id_list;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public void setContextTemplate(String contextTemplate) {
		this.contextTemplate = contextTemplate;
	}

	public void setSendMsgAlarm(Boolean sendMsgAlarm) {
		this.sendMsgAlarm = sendMsgAlarm;
	}

	public Boolean getSendMsgRecovery() {
		return sendMsgRecovery;
	}

	public void setSendMsgRecovery(Boolean sendMsgRecovery) {
		this.sendMsgRecovery = sendMsgRecovery;
	}

	public void setNotifyMaintenanceSys(Boolean notifyMaintenanceSys) {
		this.notifyMaintenanceSys = notifyMaintenanceSys;
	}

	public void setPattern(Integer pattern) {
		this.pattern = pattern;
	}
}
