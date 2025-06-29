package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;
import java.util.Date;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpdomain.PointModelMappingRepository;
import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import com.example.vvpservice.point.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Stream;
import java.util.stream.Collectors;

@Component("total_load")
public class TotalLoadStrategy implements AlarmConditionStrategy {

	public static final String TOTAL_LOAD = "total_load";

	@Autowired
	private StationNodeRepository stationNodeRepository;

	@Autowired
	private IotTsKvRepository iotTsKvRepository;

	@Override
	public List<Double> getConditionValues(String nodeId, Date ts, int count) {
		return getTotalLoad(nodeId, ts, count);
	}

	private List<Double> getTotalLoad(String nodeId, Date ts, int count) {
		StationNode node = stationNodeRepository.findByStationId(nodeId);
		List<Double> res = new ArrayList<>();
		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ts);
		for (int i = 0; i < count; i++) {
			calendar.add(Calendar.MINUTE, -15); // 往前推15分钟
		}

		if (node.getStationCategory().equals("系统")) {
			List<PointModelMapping> mappings =
					pointModelMappingRepository.findAllByStation_StationId(node.getParentId()).stream().filter(o -> o.getPointModel().getKey().equals("total_load")).collect(Collectors.toList());
			Map<Date, ?> map = pointService.getValuesByTime(mappings.get(0), calendar.getTime(), ts);
			// 根据 Map 的键排序
			List<Date> sortedKeys = new ArrayList<>(map.keySet());
			Collections.sort(sortedKeys);

			// 将排序后的值放入 List
			for (Date key : sortedKeys) {
				res.add((Double) map.get(key));
			}
			return res;
		} else if (node.getStationCategory().contains("项目")) {
			List<PointModelMapping> mappings =
					pointModelMappingRepository.findAllByStation_StationId(node.getStationId()).stream().filter(o -> o.getPointModel().getKey().equals(
							"total_load")).collect(Collectors.toList());
			Map<Date, ?> map = pointService.getValuesByTime(mappings.get(0), calendar.getTime(), ts);
			// 根据 Map 的键排序
			List<Date> sortedKeys = new ArrayList<>(map.keySet());
			Collections.sort(sortedKeys);

			// 将排序后的值放入 List
			for (Date key : sortedKeys) {
				res.add((Double) map.get(key));
			}
			return res;
		}
		return res;

	}

}
