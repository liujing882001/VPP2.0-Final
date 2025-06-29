package com.example.vvpservice.alarm.AlarmStrategy;

import java.util.Date;
import java.util.List;

public interface AlarmConditionStrategy {
	List<?> getConditionValues(String nodeId, Date ts, int count);
}
