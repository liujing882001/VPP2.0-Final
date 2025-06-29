package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;

import com.example.vvpdomain.IotTsKvRepository;
import com.example.vvpdomain.entity.IotTsKv;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component("fire_extinguisher_fault")
public class FireExtinguisherFaultStrategy implements AlarmConditionStrategy {

	public static final String FIRE_EXTINGUISHER = "fire_extinguisher_fault";

	@Autowired
	private IotTsKvRepository iotTsKvRepository;

	@Override
	public List<Double> getConditionValues(String nodeId, Date ts, int count) {
		return getFireExtinguisherFault(nodeId, ts, count);
	}

	private List<Double> getFireExtinguisherFault(String nodeId, Date ts, int count) {
		Specification<IotTsKv> spec = (root, query, criteriaBuilder) -> {
			// 构建查询条件
			return criteriaBuilder.and(criteriaBuilder.equal(root.get("pointDesc"), "tmr_yx_301"),
					criteriaBuilder.in(root.get("nodeId")).value(nodeId), criteriaBuilder.lessThanOrEqualTo(root.get("ts"), ts));
		};
		// 使用排序和分页来模仿 `ORDER BY ts DESC LIMIT 4`
		PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "ts"));

		// 执行查询
		return iotTsKvRepository.findAll(spec, pageable).getContent().stream().map(o -> Double.parseDouble(o.getPointValue())).collect(Collectors.toList());
	}
}
