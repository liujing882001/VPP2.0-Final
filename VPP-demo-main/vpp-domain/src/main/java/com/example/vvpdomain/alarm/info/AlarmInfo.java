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
}
