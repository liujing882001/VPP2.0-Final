package com.example.vvpservice.alarm.AlarmStrategy.impl.pattern;
import java.util.Date;

import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpdomain.alarm.rule.AlarmRule;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.alarm.AlarmConstant;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmPatternStrategy;
import com.example.vvpservice.alarm.impl.AlarmServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.example.vvpservice.alarm.BiStorageEnergyResourcesHistoryService.calculateDuration;
import static com.example.vvpservice.alarm.impl.AlarmServiceImpl.getAlarmContext;

@Component
@Slf4j
public class AlarmPatternAndStrategy implements AlarmPatternStrategy {

	private static final String ALARM = "alarm";

	private static final String RECOVERY = "recovery";

	@Autowired
	StationNodeRepository stationNodeRepository;

	@Autowired
	AlarmInfoRepository alarmInfoRepository;

	@Autowired
	AlarmServiceImpl alarmService;

	@Override
	public void triggerAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts) {
		boolean triggerred = true;
		List<String> nodeIds = new ArrayList<>();
		List<String> nodeNames = new ArrayList<>();
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			StationNode stationNode = stationNodeRepository.findByStationId(entry.getKey());
			nodeIds.add(entry.getKey());
			nodeNames.add(stationNode.getStationName());
			triggerred = triggerred && entry.getValue();
		}
		nodeIds.sort(String::compareTo);

		String nodes = String.join(",", nodeIds);

		if (!triggerred) {
			log.info("no alarm created , rule :{} ,device:{}", rule.getRuleName(), nodes);
		} else {
			log.info("alarm created ,rule:{} ,device:{}", rule.getRuleName(), nodes);
		}

		AlarmInfo alarmInfo = alarmInfoRepository.findByAlarmRuleAndStationIdAndAlarmStatusNot(rule, String.join(",", nodes),
				AlarmConstant.AlarmStatus.ENDED);

		if (alarmInfo == null) {
			alarmInfo = new AlarmInfo();
			alarmInfo.setAlarmId(UUID.randomUUID().toString());
			alarmInfo.setAlarmRule(rule);
			alarmInfo.setRuleName(rule.getRuleName());
			alarmInfo.setAlarmType(rule.getAlarmType());
			alarmInfo.setAlarmLevel(rule.getAlarmLevel());
			alarmInfo.setStationId(nodes);
			alarmInfo.setStationName(String.join(",", nodeNames));
			alarmInfo.setNodeId(nodes);
			alarmInfo.setNodeName(String.join(",", nodeNames));
			alarmInfo.setDeviceType(StringUtils.isEmpty(rule.getDeviceType()) ? "" : rule.getDeviceType());
			alarmInfo.setStartTime(ts);
			alarmInfo.setEndTime(null);
			alarmInfo.setChecked(false);
			alarmInfo.setCreatedTime(new Date());
			alarmInfo.setUpdateTime(new Date());
			alarmInfo.setAlarmStatus(AlarmConstant.AlarmStatus.ALARMING);
			alarmInfo.setAlarmContext(getAlarmContext(rule.getContextTemplate(), alarmInfo));
			try {
				if (null != rule.getSendMsgAlarm() && rule.getSendMsgAlarm()) {
					alarmService.sendMsg(rule.getPhoneNumber(), alarmInfo, ALARM);
				}
			} catch (Exception e) {
				log.error("send msg failed.", e);
			}

			try {
				alarmService.pushMsg(rule, alarmInfo, false);
			} catch (Exception e) {
				log.error("push msg to kafka failed.", e);
			}

			log.info("create alarm：{}", alarmInfo);
			alarmInfoRepository.save(alarmInfo);
		} else {
			alarmInfo.setAlarmDurationTime(calculateDuration(alarmInfo.getStartTime(), ts));
			log.info("update alarm duration：：{}", alarmInfo);
			alarmInfoRepository.save(alarmInfo);
		}
	}

	@Override
	public void recoverAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts, List<AlarmInfo> oldAlarms) {
		for (AlarmInfo oldAlarm : oldAlarms) {
			String[] nodes = oldAlarm.getNodeId().split(",");
			boolean status = true;
			for (String node : nodes) {
				status = status && (map.get(node) != null);
			}
			oldAlarm.setAlarmDurationTime(calculateDuration(oldAlarm.getStartTime(), ts));
			if (status) {
				log.info("update alarm duration：：{}", oldAlarm);
				alarmInfoRepository.save(oldAlarm);
			} else {
				// recovery alarm
				oldAlarm.setEndTime(ts);
				oldAlarm.setAlarmStatus(2);
				alarmInfoRepository.save(oldAlarm);
				log.info("update alarm recovery, alarm :{}", oldAlarm.getAlarmId());
				if (null != rule.getSendMsgRecovery() && rule.getSendMsgRecovery()) {
					try {
						alarmService.sendMsg(rule.getPhoneNumber(), oldAlarm, RECOVERY);
					} catch (Exception e) {
						log.error("send msg failed.", e);
					}
				}
				try {
					alarmService.pushMsg(rule, oldAlarm, true);
				} catch (Exception e) {
					log.error("push msg to kafka failed.", e);
				}
			}

		}
	}
}
