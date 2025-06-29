package com.example.vvpscheduling;


import com.example.vvpservice.alarm.PerformanceAlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@EnableAsync
@Profile({"resLe","resSliceTest","localTest"})
public class AlarmJob {

	@Autowired
	private PerformanceAlarmService performanceAlarmService;

	@Scheduled(cron = "0 5/15 * * * *")
	public void checkPerformanceAlarm() throws ParseException {
		performanceAlarmService.checkPerformanceAlarms();
	}

	@Scheduled(cron = "0 1/1 * * * *")
	public void checkAlarm(){
		performanceAlarmService.checkDeviceAlarms();
	}


	@Scheduled(cron = "0 5/15 * * * *")
	public void checkRecovery(){
		performanceAlarmService.checkRecovery();
	}
}
