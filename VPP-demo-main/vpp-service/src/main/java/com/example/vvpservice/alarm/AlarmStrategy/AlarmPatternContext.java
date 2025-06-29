package com.example.vvpservice.alarm.AlarmStrategy;
import java.util.regex.Pattern;

import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.rule.AlarmRule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Component
public class AlarmPatternContext {

	private final Map<String, AlarmPatternStrategy> strategyMap;

	@Autowired
	public AlarmPatternContext(Map<String, AlarmPatternStrategy> strategyMap) {
		this.strategyMap = strategyMap;
	}

	public void triggerAlarm(String key, AlarmRule rule, Map<String, Boolean> map, Date ts) {
		AlarmPatternStrategy strategy = strategyMap.get(key);
		if (strategy == null) {
			strategy = strategyMap.get("alarmPatternSeparateStrategy");
		}
		strategy.triggerAlarm(rule, map, ts);
	}

	public void recoverAlarm(String key, AlarmRule rule, Map<String, Boolean> map, Date ts, List<AlarmInfo> oldAlarms) {
		AlarmPatternStrategy strategy = strategyMap.get(key);
		if (strategy == null) {
			strategy = strategyMap.get("alarmPatternSeparateStrategy");
		}
		strategy.recoverAlarm(rule, map, ts, oldAlarms);
	}
}
