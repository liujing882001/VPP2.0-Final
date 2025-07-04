package com.example.vvpservice.alarm.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

@Data
public class AlarmShortMessage {

	@JSONField(name = "timestamp")
	private String timestamp;

	@JSONField(name = "station_name")
	private String stationName;

	@JSONField(name = "node_name")
	private String nodeName;

	@JSONField(name = "alarm_information")
	private String alarmInformation;

	@JSONField(name = "alarm_level")
	private String alarmLevel;

	// 手动添加构造函数以确保编译通过
	public AlarmShortMessage() {
	}

	public AlarmShortMessage(String timestamp, String stationName, String nodeName, String alarmInformation, String alarmLevel) {
		this.timestamp = timestamp;
		this.stationName = stationName;
		this.nodeName = nodeName;
		this.alarmInformation = alarmInformation;
		this.alarmLevel = alarmLevel;
	}
}
