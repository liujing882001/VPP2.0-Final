package com.example.vvpservice.alarm;

import java.text.ParseException;

public interface PerformanceAlarmService {
	void checkPerformanceAlarms() throws ParseException;

	void checkDeviceAlarms();

	void checkRecovery();

}
