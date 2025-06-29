package com.example.vvpservice.point.service;

import com.example.vvpcommom.StringUtils;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.point.service.mappingStrategy.MappingStrategy;
import com.example.vvpservice.point.service.model.MappingView;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PointService {

	@Autowired
	DeviceRepository deviceRepository;

	@Autowired
	DevicePointRepository devicePointRepository;

	@Autowired
	PointModelRepository pointModelRepository;

	@Autowired
	StationNodeRepository stationNodeRepository;

	@Autowired
	PointModelMappingRepository pointModelMappingRepository;

	@Autowired
	SysDictTypeRepository sysDictTypeRepository;

	@Autowired
	private List<MappingStrategy> strategies;


	public List<MappingView> queryMappingsByStationId(String id) {
		List<PointModelMapping> list = pointModelMappingRepository.findAllByStation_StationId(id);
		List<MappingView> res = new ArrayList<>();
		list.forEach(o -> res.add(new MappingView(o)));
		return res;
	}

	public void buildMappingAll(String stationId) {
		if (StringUtils.isEmpty(stationId)) {
			throw new RuntimeException("stationId is empty");
		}

		List<PointModelMapping> mappings = new ArrayList<>();

		List<StationNode> list = findAllNodesByStationNode(stationId);
		list.forEach(o -> buildMappingAuto(o.getStationId()));

		pointModelMappingRepository.saveOrUpdateAll(mappings);
	}


	public void buildMappingAuto(String stationId) {
		if (StringUtils.isEmpty(stationId)) {
			throw new RuntimeException("stationId is empty");
		}

		List<StationNode> nodes = findAllNodesByStationNode(stationId);

		nodes.forEach(node -> {
			for (MappingStrategy strategy : strategies) {
				strategy.buildMapping(node);
			}
		});
	}


	public List<StationNode> findAllNodesByStationNode(String nodeId) {
		List<StationNode> res = new ArrayList<>();
		Queue<StationNode> queue = new ArrayDeque<>();
		StationNode root = stationNodeRepository.findByStationId(nodeId);
		queue.offer(root);
		while (!queue.isEmpty()) {
			StationNode newNode = queue.poll();
			res.add(newNode);
			List<StationNode> tmp = stationNodeRepository.findAllByParentId(newNode.getStationId());
			for (StationNode s : tmp) {
				queue.offer(s);
			}
		}
		return res;

	}

	public List<?> getValues(String mappingId,int count) {
		PointModelMapping mapping = pointModelMappingRepository.findByMappingId(mappingId);
		Map<String, MappingStrategy> mappingStrategyMap = this.strategies.stream().collect(Collectors.toMap(MappingStrategy::getStrategyType,
				Function.identity()));
		return mappingStrategyMap.get(mapping.getPointModel().getPointType()).getValues(mapping,count);
	}

	public Map<Date, ?> getValuesByTime(String mappingId,Date st,Date et){
		PointModelMapping mapping = pointModelMappingRepository.findByMappingId(mappingId);
		try {
			return getValuesByTime(mapping,st,et);
		}catch (Exception e){
			return new HashMap<>();
		}

	}

	public Map<Date, ?> getValuesByTime(PointModelMapping mapping,Date st,Date et){
		Map<String, MappingStrategy> mappingStrategyMap = this.strategies.stream().collect(Collectors.toMap(MappingStrategy::getStrategyType,
				Function.identity()));
		try {
			return mappingStrategyMap.get(mapping.getPointModel().getPointType()).getValues(mapping,st,et);
		}catch (Exception e){
			return new HashMap<>();
		}
	}
	public Map<Date, ?> getDValuesByMTime(PointModelMapping mapping,Date st,Date et){
		Map<String, MappingStrategy> mappingStrategyMap = this.strategies.stream().collect(Collectors.toMap(MappingStrategy::getStrategyType,
				Function.identity()));
		try {
			return mappingStrategyMap.get(mapping.getPointModel().getPointType()).getDValues(mapping,st,et);
		}catch (Exception e){
			return new HashMap<>();
		}
	}
	public Map<Date, ?> getDValuesByTime(String mappingId,Date st,Date et){
		PointModelMapping mapping = pointModelMappingRepository.findByMappingId(mappingId);
		Map<String, MappingStrategy> mappingStrategyMap = this.strategies.stream().collect(Collectors.toMap(MappingStrategy::getStrategyType,
				Function.identity()));
		try {
			Map<Date, ?> tempRes = mappingStrategyMap.get(mapping.getPointModel().getPointType()).getDValues(mapping,st,et);
			return tempRes;
		}catch (Exception e){
			return new HashMap<>();
		}
	}
	public Map<Long, Double> getLDValuesByTime(PointModelMapping mapping,Date st,Date et){
		Map<String, MappingStrategy> mappingStrategyMap = this.strategies.stream().collect(Collectors.toMap(MappingStrategy::getStrategyType,
				Function.identity()));
		try {
			return mappingStrategyMap.get(mapping.getPointModel().getPointType()).getLDValues(mapping,st,et);
		}catch (Exception e){
			return new HashMap<>();
		}
	}

	private List<SysDictType> getAllSystemId() {
		List<SysDictType> res = sysDictTypeRepository.findAll();
		return res;
	}
}
