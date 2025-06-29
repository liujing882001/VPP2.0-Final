package com.example.vvpservice.alarm.AlarmStrategy.impl.indicator;

import com.example.vvpdomain.CfgStorageEnergyStrategyPower96Repository;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategyPower96;
import com.example.vvpservice.alarm.AlarmStrategy.AlarmConditionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@Component("es_power")
public class EsPowerStrategy implements AlarmConditionStrategy {
	public static final String ES_POWER = "es_power";

	@Autowired
	private CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;

	@Override
	public List<Double> getConditionValues(String nodeId, Date ts, int count) {
		return getEsPower(nodeId, ts, count);
	}

	private List<Double> getEsPower(String nodeId, Date ts, int count) {
		Date effectiveDate;
		String endDate;
		try {
			SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
			fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			effectiveDate = fmt_ymd.parse(fmt_ymd.format(ts));
			SimpleDateFormat fmt_hms = new SimpleDateFormat("HH:mm:ss");
			fmt_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			endDate = fmt_hms.format(ts);
		} catch (Exception e) {
			return new ArrayList<>();
		}

		Specification<CfgStorageEnergyStrategyPower96> spec = (root, query, criteriaBuilder) -> {
			// 构建查询条件
			return criteriaBuilder.and(
					criteriaBuilder.equal(root.get("systemId"), "nengyuanzongbiao"),
					criteriaBuilder.equal(root.get("nodeId"), nodeId),
					criteriaBuilder.equal(root.get("effectiveDate"), effectiveDate),
					criteriaBuilder.lessThanOrEqualTo(root.get("eTime"), endDate)
			);
		};
		PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "eTime"));

		List<CfgStorageEnergyStrategyPower96> res = cfgStorageEnergyStrategyPower96Repository.findAll(spec, pageable).getContent();
		List<Double> result = new ArrayList<>();
		for (CfgStorageEnergyStrategyPower96 p : res) {
			if (p.getStrategy().equals("放电")) {
				result.add(p.getPower());
			} else if (p.getStrategy().equals("充电")) {
				result.add(0 - p.getPower());
			} else {
				result.add(p.getPower());
			}
		}
		return result;
	}
}
