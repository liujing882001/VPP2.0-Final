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

}
