package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;
import java.util.Date;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.PointModelMappingRepository;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import com.example.vvpservice.point.service.PointService;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Component("ac_power")
public class AcPowerStrategy implements AlarmConditionStrategy {
	public static final String AC_POWER = "ac_power";

	@Override
	public List<Double> getConditionValues(String nodeId, Date ts, int count) {
		return getAcPower(nodeId, ts, count);
	}

	private List<Double> getAcPower(String nodeId, Date ts, int count) {
		List<Double> res = new ArrayList<>();
		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);

		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals("power")).collect(Collectors.toList());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ts);
		for (int i = 0; i < count; i++) {
			calendar.add(Calendar.MINUTE, -15); // 往前推15分钟
		}
		Map<Date, ?> map = pointService.getValuesByTime(mappings.get(0), calendar.getTime(), ts);
		List<Date> sortedKeys = new ArrayList<>(map.keySet());
		Collections.sort(sortedKeys);

		// 将排序后的值放入 List
		for (Date key : sortedKeys) {
			Double val = (Double) map.get(key);
			if (val > -1 && val < 1) {
				val = 0.0;
			}
			res.add(val);
		}
		return res;
	}
}
