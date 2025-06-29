package com.example.vvpservice.point.service.mappingStrategy;

import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpdomain.entity.StationNode;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface MappingStrategy {
	String getStrategyType();

	void buildMapping(StationNode node);

	Map<Date, ?> getValues(PointModelMapping mapping, Date st, Date et);
	Map<Date, ?> getDValues(PointModelMapping mapping, Date st, Date et);
	Map<Long, Double> getLDValues(PointModelMapping mapping, Date st, Date et);


	List<?> getValues(PointModelMapping mapping, int count);
}