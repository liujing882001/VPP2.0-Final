package com.example.kafka.alarm;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@NoArgsConstructor
public class PerformanceAlarmMsg {

	private String alarmId;

	private String nodeId;

	private String ruleName;

	private String nodeName;

	private int alarmType;

	private int alarmLevel;

	private String alarmContext;

	private String stationId;

	private String stationName;

	private Date startTime;

	private Date endTime;

	private Boolean checked;

	private int alarmStatus;

	private String alarmDurationTime;

	private Date createdTime;

	private Date updateTime;

	private String deviceType;

	private Boolean recovery;
}
