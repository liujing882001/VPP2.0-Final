package com.example.vvpservice.alarm.AlarmStrategy;
import java.util.regex.Pattern;

import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.rule.AlarmRule;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface AlarmPatternStrategy {

	public void triggerAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts);

	public void recoverAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts, List<AlarmInfo> oldAlarms);
}
