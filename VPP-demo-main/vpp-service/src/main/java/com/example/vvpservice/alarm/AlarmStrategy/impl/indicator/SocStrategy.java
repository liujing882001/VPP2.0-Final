package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpdomain.PointModelMappingRepository;
import com.example.vvpdomain.entity.IotTsKv;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import com.example.vvpservice.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component("soc")
public class SocStrategy implements AlarmConditionStrategy {

	public static final String SOC = "soc";

	@Autowired
	private IotTsKvRepository iotTsKvRepository;

	@Override
	public List<Double> getConditionValues(String nodeId, Date ts, int count) {
		return getSoc(nodeId, ts, count);
	}

	private List<Double> getSoc(String nodeId, Date ts, int count) {
		List<Double> res = new ArrayList<>();
		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ts);
		for (int i = 0; i < count; i++) {
			calendar.add(Calendar.MINUTE, -15); // 往前推15分钟
		}

		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals(SOC)).collect(Collectors.toList());
		Map<Date, ?> map = pointService.getValuesByTime(mappings.get(0), calendar.getTime(), ts);

		List<Date> sortedKeys = new ArrayList<>(map.keySet());
		Collections.sort(sortedKeys);

		// 将排序后的值放入 List
		for (Date key : sortedKeys) {
			res.add((Double) map.get(key) * 100.0);
		}
		return res;
	}
}
