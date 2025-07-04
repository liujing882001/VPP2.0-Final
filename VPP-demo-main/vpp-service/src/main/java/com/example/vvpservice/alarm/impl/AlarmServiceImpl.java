package com.example.vvpservice.alarm.impl;
import java.util.Date;

import com.alibaba.fastjson.JSON;
import com.example.gateway.EnterpriseServiceBusService;
import com.example.kafka.alarm.PerformanceAlarmMsg;
import com.example.vvpcommom.JsonConfigUtil;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.BiStorageEnergyResourcesRepository;
import com.example.vvpdomain.CfgStorageEnergyStrategyPower96Repository;
import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpdomain.alarm.rule.AlarmRule;
import com.example.vvpdomain.alarm.rule.AlarmRuleRepository;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.alarm.AlarmConstant;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionContext;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmPatternContext;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmPatternStrategy;
import com.example.vvpservice.alarm.PerformanceAlarmService;
import com.example.vvpservice.alarm.message.AlarmShortMessage;
import com.example.vvpservice.alarm.message.AliyunSmsThrService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.stream.Stream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.vvpservice.alarm.BiStorageEnergyResourcesHistoryService.calculateDuration;
import static com.example.vvpservice.alarm.message.AliyunSmsThrService.ALARM_SMS_CODE;
import static com.example.vvpservice.alarm.message.AliyunSmsThrService.RECOVERY_SMS_CODE;


/**
 * 性能告警service
 */
@Service
@Slf4j
public class AlarmServiceImpl implements PerformanceAlarmService {

	// 手动添加log变量以确保编译通过
	private static final Logger log = LoggerFactory.getLogger(AlarmServiceImpl.class);

	@Autowired
	private AlarmRuleRepository alarmRuleRepository;

	@Autowired
	private AlarmInfoRepository alarmInfoRepository;

	@Autowired
	private StationNodeRepository stationNodeRepository;

	@Autowired
	private AliyunSmsThrService aliyunSmsThrService;

	@Autowired
	private EnterpriseServiceBusService enterpriseServiceBusService;

	private static final String ALARM = "alarm";

	private static final String RECOVERY = "recovery";

	@Value("${spring.kafka.alarm.performance.topic}")
	private String performance_topic;

	@Value("${spring.kafka.alarm.device.topic}")
	private String device_topic;

	@Value("${spring.kafka.alarm.key}")
	private String key;


	@Override
	public void checkPerformanceAlarms() {
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(2024,Calendar.DECEMBER,9,17,50,0);
//		Date ts = calendar.getTime();
		Date ts = new Date();
		JexlEngine jexl = new JexlBuilder().create();
		List<AlarmRule> rules = alarmRuleRepository.findAllByAlarmTypeAndEnable(AlarmConstant.AlarmType.PERFORMANCE_ALARM, Boolean.TRUE);
		rules.forEach(o -> {
			Map<String, Boolean> alarmStatusMap = checkAlarmByRule(o, jexl, ts, null);
			setAlarmByMap(o, alarmStatusMap, ts);
		});
	}

	public void checkDeviceAlarms() {
		Date ts = new Date();
		JexlEngine jexl = new JexlBuilder().create();
		List<AlarmRule> deviceRules = alarmRuleRepository.findAllByAlarmTypeAndEnable(AlarmConstant.AlarmType.DEVICE_ALARM, Boolean.TRUE);
		deviceRules.forEach(o -> {
			Map<String, Boolean> alarmStatusMap = checkAlarmByRule(o, jexl, ts, null);
			setAlarmByMap(o, alarmStatusMap, ts);
		});
	}

	@Override
	public void checkRecovery() {
		List<AlarmInfo> alarms =
				alarmInfoRepository.findAllByAlarmStatusNot(AlarmConstant.AlarmStatus.ENDED).stream().filter(o -> o.getAlarmRule() != null).collect(Collectors.toList());
		Map<AlarmRule, List<AlarmInfo>> alarmGroupByRule = alarms.stream().collect(Collectors.groupingBy(AlarmInfo::getAlarmRule));
		JexlEngine jexl = new JexlBuilder().create();
		for (Map.Entry<AlarmRule, List<AlarmInfo>> entry : alarmGroupByRule.entrySet()) {
			Map<String, Boolean> alarmStatusMap = checkAlarmByRule(entry.getKey(), jexl, new Date(), entry.getValue());
			recoverAlarmByMap(entry.getKey(), alarmStatusMap, new Date(), alarms);
		}
	}

	private Map<String, Boolean> checkAlarmByRule(AlarmRule o, JexlEngine jexl, Date ts, List<AlarmInfo> oldAlarm) {
		Map<String, Boolean> alarmStatusMap = new HashMap<>();
		try {
			String[] nodes;
			if (oldAlarm == null) {
				nodes = o.getStation_id_list().split(",");
			} else {
				nodes = oldAlarm.stream().map(AlarmInfo::getNodeId).toArray(String[]::new);
			}

			Map<String, Object> expMap = parseExpression(o.getRuleExpression());
			JexlExpression jexlExpression = jexl.createExpression(o.getRuleExpression());
			AlarmConditionContext context = SpringBeanHelper.getBeanOrThrow(AlarmConditionContext.class);

			for (String node : nodes) {
				boolean triggered = true;

				Map<String, Object> conditionValues = new HashMap<>();
				for (String key : expMap.keySet()) {
					conditionValues.put(key, context.getValues(key, node, ts, o.getCount()));
				}

				for (int i = 0; i < o.getCount(); i++) {
					JexlContext jexlContext = new MapContext();
					for (Map.Entry<String, Object> entry : conditionValues.entrySet()) {
						if (entry.getValue() instanceof List) {
							List<?> values = (List<?>) entry.getValue();
							if (i < values.size()) {
								jexlContext.set(entry.getKey(), values.get(i));
							}
						}
					}

					try {
						triggered = triggered & (boolean) jexlExpression.evaluate(jexlContext);
						alarmStatusMap.put(node, triggered);
					} catch (Exception e) {
						alarmStatusMap.put(node, false);
						log.error("calculate alarm expression failed , alarm rule: {} , device id: {}, variable:{}", o.getRuleName(), node,
								conditionValues, e);
						break;
					}
				}
			}
			return alarmStatusMap;
		} catch (Exception e) {
			log.error("rule :{} check failed.", o.getRuleName(), e);
		}
		return alarmStatusMap;
	}

	private void setAlarmByMap(AlarmRule rule, Map<String, Boolean> map, Date ts) {
		AlarmPatternContext alarmPatternContext = SpringBeanHelper.getBeanOrThrow(AlarmPatternContext.class);
		switch (rule.getPattern()) {
			case AlarmConstant.AlarmPattern.MODE_AND:
				alarmPatternContext.triggerAlarm("alarmPatternAndStrategy", rule, map, ts);
				break;
			case AlarmConstant.AlarmPattern.MODE_SEPARATE:
			default:
				alarmPatternContext.triggerAlarm("alarmPatternSeparateStrategy", rule, map, ts);
				break;
		}
	}

	private void recoverAlarmByMap(AlarmRule rule, Map<String, Boolean> map, Date ts, List<AlarmInfo> oldAlarms) {
		AlarmPatternContext alarmPatternContext = SpringBeanHelper.getBeanOrThrow(AlarmPatternContext.class);
		switch (rule.getPattern()) {
			case AlarmConstant.AlarmPattern.MODE_AND:
				alarmPatternContext.recoverAlarm("alarmPatternAndStrategy", rule, map, ts, oldAlarms);
				break;
			case AlarmConstant.AlarmPattern.MODE_SEPARATE:
			default:
				alarmPatternContext.recoverAlarm("alarmPatternSeparateStrategy", rule, map, ts, oldAlarms);
				break;
		}
	}

	public void pushMsg(AlarmRule alarmRule, AlarmInfo alarmInfo, Boolean recovery) {
		if (null != alarmRule.getNotifyMaintenanceSys() && alarmRule.getNotifyMaintenanceSys() && alarmInfo.getAlarmLevel() == AlarmConstant.AlarmLevel.FAULT) {
			if (alarmInfo.getAlarmType() == AlarmConstant.AlarmType.PERFORMANCE_ALARM) {
				// 告警触发，推送消息
				String kafkaMsg = buildKafkaMsg(alarmInfo, recovery);
				enterpriseServiceBusService.eventBusToESB(key, performance_topic, kafkaMsg);
			} else if (alarmInfo.getAlarmType() == AlarmConstant.AlarmType.DEVICE_ALARM) {
				// 告警触发，推送消息
				String kafkaMsg = buildKafkaMsg(alarmInfo, recovery);
				enterpriseServiceBusService.eventBusToESB(key, device_topic, kafkaMsg);
			}
		}
	}

	public void sendMsg(String phoneNumbers, AlarmInfo alarmInfo, String type) {
		List<String> phoneNumberList = Arrays.asList(Strings.split(phoneNumbers, ','));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String timestamp = sdf.format(new Date());
		String stationName = alarmInfo.getStationName();
		String nodeName = alarmInfo.getNodeName();
		String alarmInformation = alarmInfo.getAlarmContext();
		String alarmLevel = AlarmConstant.AlarmLevel.getAlarmLevelName(alarmInfo.getAlarmLevel());

		AlarmShortMessage message = new AlarmShortMessage(timestamp, stationName, nodeName, alarmInformation, alarmLevel);
		String templateParam = AliyunSmsThrService.generateTemplateParam(message);
		switch (type) {
			case ALARM:
				phoneNumberList.forEach(o -> aliyunSmsThrService.sendSmsByCode(o, templateParam, ALARM_SMS_CODE));
				break;
			case RECOVERY:
				phoneNumberList.forEach(o -> aliyunSmsThrService.sendSmsByCode(o, templateParam, RECOVERY_SMS_CODE));
				break;
		}
	}


	public static String getAlarmContext(String template, AlarmInfo alarmInfo) {
		String context = template;
		// 正则表达式模式
		String regex = "\\{([^}]*)\\}";

		// 创建Pattern对象
		Pattern pattern = Pattern.compile(regex);

		// 创建Matcher对象
		Matcher matcher = pattern.matcher(template);

		Field[] fields = AlarmInfo.class.getDeclaredFields();

		while (matcher.find()) {
			String fieldName = matcher.group(1);
			for (Field field : fields) {
				if (field.getName().equals(fieldName)) {
					try {
						field.setAccessible(true);
						context = context.replace('{' + fieldName + '}', field.get(alarmInfo).toString());
					} catch (Exception e) {
						log.error("set alarm context: get param failed", e);
					}
				}
			}
		}
		return context;
	}


	/*
	 * soc<100 && ac_power<0 && ES_POWER<0
	 * */
	private Map<String, Object> parseExpression(String expression) {
		List<String> strings = Arrays.asList(expression.split("&&|\\|\\|"));
		Map<String, Object> expMap = new HashMap<>();
		strings.forEach(o -> {
			String[] str = o.split("[<>]=?|==|!=");
			if (str.length != 2) {
				return;
			}
			expMap.put(str[0].trim(), str[1].trim());
		});
		return expMap;
	}

	private String buildKafkaMsg(AlarmInfo alarmInfo, Boolean recovery) {
		PerformanceAlarmMsg msg = new PerformanceAlarmMsg();
		BeanUtils.copyProperties(alarmInfo, msg);
		msg.setRecovery(recovery);
		return JSON.toJSONString(msg, JsonConfigUtil.getSnakeCase());
	}
}
