package com.example.vvpservice.alarm.AlarmStrategy;
import java.util.Collections;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class AlarmConditionContext {
	private final Map<String, AlarmConditionStrategy> strategyMap;

	@Autowired
	public AlarmConditionContext(Map<String, AlarmConditionStrategy> strategyMap) {
		this.strategyMap = strategyMap;
	}

	public List<?> getValues(String key, String nodeId, Date ts, int count) {
		AlarmConditionStrategy strategy = strategyMap.get(key);
		return strategy != null ? strategy.getConditionValues(nodeId, ts, count) : Collections.emptyList();
	}
}
