package com.example.vvpservice.alarm.AlarmStrategy.impl.pattern;
import java.util.regex.Pattern;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.example.vvpservice.alarm.BiStorageEnergyResourcesHistoryService.calculateDuration;
import static com.example.vvpservice.alarm.impl.AlarmServiceImpl.getAlarmContext;

@Component
@Slf4j
public class AlarmPatternSeparateStrategy implements AlarmPatternStrategy {

	private static final Logger log = LoggerFactory.getLogger(AlarmPatternSeparateStrategy.class);

	private static final String ALARM = "alarm";

	private static final String RECOVERY = "recovery";

	@Autowired
	StationNodeRepository stationNodeRepository;

	@Autowired
	AlarmInfoRepository alarmInfoRepository;

	@Autowired
	AlarmServiceImpl alarmService;

	public void triggerAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts) {
		for (Map.Entry<String, Boolean> entry : map.entrySet()) {
			StationNode stationNode = stationNodeRepository.findByStationId(entry.getKey());
			AlarmInfo alarmInfo = alarmInfoRepository.findByAlarmRuleAndStationIdAndAlarmStatusNot(rule, entry.getKey(),
					AlarmConstant.AlarmStatus.ENDED);
			if (entry.getValue() == null || !entry.getValue()) {
				log.info("no alarm created , rule :{} ,device:{}", rule.getRuleName(), entry.getKey());
				continue;
			}
			log.info("alarm created , rule :{} ,device:{}", rule.getRuleName(), entry.getKey());

			if (alarmInfo == null) {
				alarmInfo = new AlarmInfo();
				alarmInfo.setAlarmId(UUID.randomUUID().toString());
				alarmInfo.setAlarmRule(rule);
				alarmInfo.setRuleName(rule.getRuleName());
				alarmInfo.setAlarmType(rule.getAlarmType());
				alarmInfo.setAlarmLevel(rule.getAlarmLevel());
				alarmInfo.setStationId(entry.getKey());
				alarmInfo.setStationName(stationNode.getStationName());
				alarmInfo.setNodeId(entry.getKey());
				alarmInfo.setNodeName(stationNode.getStationName());
				alarmInfo.setDeviceType(StringUtils.isEmpty(rule.getDeviceType()) ? stationNode.getSystemNames() : rule.getDeviceType());
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
	}

	@Override
	public void recoverAlarm(AlarmRule rule, Map<String, Boolean> map, Date ts, List<AlarmInfo> oldAlarms) {
		for (AlarmInfo oldAlarm : oldAlarms) {
			if (!map.containsKey(oldAlarm.getNodeId())) {
				continue;
			}
			if (map.get(oldAlarm.getNodeId())) {
				// update alarm time
				oldAlarm.setAlarmDurationTime(calculateDuration(oldAlarm.getStartTime(), ts));
				log.info("update alarm duration：：{}", oldAlarm);
				alarmInfoRepository.save(oldAlarm);
			} else {
				// recovery alarm
				oldAlarm.setAlarmDurationTime(calculateDuration(oldAlarm.getStartTime(), ts));
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
