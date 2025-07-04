package com.example.vvpservice.nodeep.model;

import lombok.Data;

import java.time.YearMonth;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UpdateNodeETimeRequest {

	private String nodeId;

	private YearMonth date;

	private List<TimeInfo> timeInfos;

	@Data
	public static class TimeInfo {
		private String timeSpan;

		private String property;

		public String getTimeSpan() { return timeSpan; }
		public String getProperty() { return property; }
	}

	public Map<String, String> getTimeMap() {
		Map<String, String> map = new HashMap<>();
		for (TimeInfo timeInfo : this.getTimeInfos()) {
			map.put(timeInfo.getTimeSpan(), timeInfo.getProperty());
		}
		return map;
	}

	public List<TimeInfo> getTimeInfos() { return timeInfos; }

}
