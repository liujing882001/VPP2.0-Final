package com.example.vvpservice.alarm;

import com.example.vvpdomain.StationNodeRepository;

public interface AlarmConstant {
	/**
	 * 告警类型
	 */
	interface AlarmType {

		/**
		 * 设备告警
		 */
		int DEVICE_ALARM = 0;

		/**
		 * 性能告警
		 */
		int PERFORMANCE_ALARM = 1;

		static String getAlarmTypeName(int i){
			switch (i) {
				case DEVICE_ALARM:
					return "设备告警";
				case PERFORMANCE_ALARM:
					return "性能告警";
				default:
					return "";
			}
		}
	}

	/**
	 * 告警等级
	 */
	interface AlarmLevel {

		/**
		 * 故障
		 */
		int FAULT = 0;

		/**
		 * 警告
		 */
		int WARN = 1;


		/**
		 * 提示
		 */
		int HINT = 2;

		static String getAlarmLevelName(int i) {
			switch (i) {
				case FAULT:
					return "故障";
				case WARN:
					return "警告";
				case HINT:
					return "提示";
				default:
					return "";
			}
		}
	}

	/**
	 * 告警状态
	 */
	interface AlarmStatus {

		/**
		 * 告警中
		 */
		int ALARMING = 0;

		/**
		 * 处理中
		 */
		int RECOVERING = 1;

		/**
		 * 已恢复
		 */
		int ENDED = 2;

		static String getAlarmStatusName(int i) {
			switch (i) {
				case ALARMING:
					return "告警中";
				case RECOVERING:
					return "处理中";
				case ENDED:
					return "已恢复";
				default:
					return "";
			}
		}
	}

	interface AlarmPattern {
		int MODE_SEPARATE = 0 ;

		int MODE_AND = 1;

		static String getAlarmPattern(int i){
			switch (i){
				case MODE_SEPARATE:
					return "告警模式：单独判断";
				case MODE_AND:
					return "告警模式：与";
				default:
					return "";
			}
		}
	}
}
