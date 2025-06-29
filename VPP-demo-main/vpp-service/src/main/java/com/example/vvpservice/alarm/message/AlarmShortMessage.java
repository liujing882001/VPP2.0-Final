package com.example.vvpservice.alarm.message;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
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
}
