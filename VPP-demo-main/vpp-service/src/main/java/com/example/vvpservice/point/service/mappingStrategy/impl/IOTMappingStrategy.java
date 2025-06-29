package com.example.vvpservice.point.service.mappingStrategy.impl;

import com.alibaba.fastjson.JSON;
import com.example.vvpcommom.pointmodel.PointConstant;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.point.service.mappingStrategy.MappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class IOTMappingStrategy implements MappingStrategy {

	private final DevicePointRepository devicePointRepository;
	private final PointModelRepository pointModelRepository;
	private final PointModelMappingRepository pointModelMappingRepository;

	private final JexlEngine jexlEngine = new JexlBuilder().create();

	private final IotTsKvRepository iotTsKvRepository;

	private final IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;

	private final BiStorageEnergyLogRepository biStorageEnergyLogRepository;

	@Autowired
	public IOTMappingStrategy(DevicePointRepository devicePointRepository, PointModelRepository pointModelRepository,
	                          PointModelMappingRepository pointModelMappingRepository, IotTsKvRepository iotTsKvRepository,
	                          IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository,
	                          BiStorageEnergyLogRepository biStorageEnergyLogRepository) {
		this.devicePointRepository = devicePointRepository;
		this.pointModelRepository = pointModelRepository;
		this.pointModelMappingRepository = pointModelMappingRepository;
		this.iotTsKvRepository = iotTsKvRepository;
		this.iotTsKvMeteringDevice96Repository = iotTsKvMeteringDevice96Repository;
		this.biStorageEnergyLogRepository = biStorageEnergyLogRepository;
	}

	@Override
	public String getStrategyType() {
		return PointConstant.DataType.MEASURING_POINT;
	}

	@Override
	public void buildMapping(StationNode node) {
		List<PointModelMapping> mappings = new ArrayList<>();
		// step1: 获取属于此系统下的所有点位信息
		Map<String, List<DevicePoint>> map =
				devicePointRepository.findAllByDevice_Node_NodeId(node.getStationId()).stream().filter(o -> o.getPointKey() != null).collect(Collectors.groupingBy(DevicePoint::getPointKey));

		// step2: 获取属于此系统下的所有测点模型
		Map<String, PointModel> pointModelMap =
				pointModelRepository.findAll().stream().filter(pointModel -> pointModel.getPointType().equals(PointConstant.DataType.MEASURING_POINT)).collect(Collectors.toMap(PointModel::getKey, Function.identity()));

		// step3: 建立映射关系
		for (Map.Entry<String, List<DevicePoint>> entry : map.entrySet()) {
			if (pointModelMap.containsKey(entry.getKey())) {
				for (DevicePoint devicePoint : entry.getValue()) {
					PointModelMapping mapping = new PointModelMapping(node, pointModelMap.get(entry.getKey()), devicePoint.getDevice(), false);
					mappings.add(mapping);
				}
			}
		}
		pointModelMappingRepository.saveOrUpdateAll(mappings);
	}
	@Override
	public Map<Date, ?> getDValues(PointModelMapping mapping, Date st, Date et) {
		String formula = mapping.getCalculation_formula();
		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, Map<Date, Double>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		Map<Date, Double> result = new HashMap<>();
		int len = Integer.MAX_VALUE;
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误" + Arrays.toString(fieldList));
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				DevicePoint devicePoint = devicePointRepository.findByDeviceSnAndPointKey(deviceSn, mapping.getPointModel().getKey());
				String pointDesc = mapping.getPointModel().getPointDesc();
				String nodeId = devicePoint.getDevice().getNode().getNodeId();
				Map<Date, Double> v;
				switch (mapping.getPointModel().getKey()) {
					case "soc":
						v =
								biStorageEnergyLogRepository.findSocByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					case "soh":
						v =
								biStorageEnergyLogRepository.findSohByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					default:
						v =
								iotTsKvMeteringDevice96Repository.findAllByNodeIdAndPointSnAndPointDescAndCountDataTimeBetweenOrderByCountDataTimeDesc(nodeId,
										devicePoint.getPointSn(), pointDesc, st, et).stream().collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
				}
			}
		}

		// 交换后的结果 Map
		Map<Date, Map<String, Double>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, Double>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, Double> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}

		// 计算结果
		for (Map.Entry<Date, Map<String, Double>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			context.set("data", entry.getValue());
			result.put(entry.getKey(), (double) expression.evaluate(context));
		}

		return result;

	}
	@Override
	public Map<Long, Double> getLDValues(PointModelMapping mapping, Date st, Date et) {
		String formula = mapping.getCalculation_formula();
		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, Map<Date, Double>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		Map<Long, Double> result = new HashMap<>();
		int len = Integer.MAX_VALUE;
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误" + Arrays.toString(fieldList));
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				DevicePoint devicePoint = devicePointRepository.findByDeviceSnAndPointKey(deviceSn, mapping.getPointModel().getKey());
				String pointDesc = mapping.getPointModel().getPointDesc();
				String nodeId = devicePoint.getDevice().getNode().getNodeId();
				Map<Date, Double> v;
				switch (mapping.getPointModel().getKey()) {
					case "soc":
						v =
								biStorageEnergyLogRepository.findSocByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					case "soh":
						v =
								biStorageEnergyLogRepository.findSohByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					default:
						v =
								iotTsKvMeteringDevice96Repository.findAllByNodeIdAndPointSnAndPointDescAndCountDataTimeBetweenOrderByCountDataTimeDesc(nodeId,
										devicePoint.getPointSn(), pointDesc, st, et).stream().collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
				}
			}
		}

		// 交换后的结果 Map
		Map<Date, Map<String, Double>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, Double>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, Double> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}

		// 计算结果
		for (Map.Entry<Date, Map<String, Double>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			context.set("data", entry.getValue());
			result.put(entry.getKey().getTime(), (double) expression.evaluate(context));
		}

		return result;

	}
	@Override
	public List<?> getValues(PointModelMapping mapping, int count) {
		String formula = mapping.getCalculation_formula();
		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, List<?>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		List<Double> result = new ArrayList<>();
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误");
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				String pointDesc = mapping.getPointModel().getPointDesc();
				List<Double> v = iotTsKvRepository.findAllByNodeIdAndPointDescAndDeviceSnOrderByTsDesc(mapping.getStation().getStationId(),
						pointDesc, deviceSn, PageRequest.of(0, count)).stream().map(iotTsKv -> Double.parseDouble(iotTsKv.getPointValue())).collect(Collectors.toList());
				values.put(field, v);
			}
		}
		for (int i = 0; i < count; i++) {
			JexlContext context = new MapContext();
			Map<String, Object> dataMap = new HashMap<>();

			for (String field : fields) {
				dataMap.put(field, values.get(field).get(i));
				context.set("data", dataMap);
			}
			double v = (double) expression.evaluate(context);
			result.add(v);
		}
		return result;

	}

	@Override
	public Map<Date, Double> getValues(PointModelMapping mapping, Date st, Date et) {
		String formula = mapping.getCalculation_formula();
		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, Map<Date, Double>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		Map<Date, Double> result = new HashMap<>();
		int len = Integer.MAX_VALUE;
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误" + Arrays.toString(fieldList));
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				DevicePoint devicePoint = devicePointRepository.findByDeviceSnAndPointKey(deviceSn, mapping.getPointModel().getKey());
				String pointDesc = mapping.getPointModel().getPointDesc();
				String nodeId = devicePoint.getDevice().getNode().getNodeId();
				Map<Date, Double> v;
				switch (mapping.getPointModel().getKey()) {
					case "soc":
						v =
								biStorageEnergyLogRepository.findSocByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					case "soh":
						v =
								biStorageEnergyLogRepository.findSohByNodeId(nodeId, st, et).stream().collect(Collectors.toMap(o -> (Date) o[1],
										o -> (double) o[0]));
						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
						break;
					default:
						v = iotTsKvMeteringDevice96Repository.findAllValue(nodeId, devicePoint.getPointSn(), pointDesc, st, et)
								.stream()
								.collect(Collectors.toMap(
										obj -> (Date) obj[0],
										obj -> (Double) obj[1]
								));

						if (v.size() < len) {
							len = v.size();
						}
						values.put(field, v);
				}
			}
		}

		// 交换后的结果 Map
		Map<Date, Map<String, Double>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, Double>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, Double> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}

		// 计算结果
		for (Map.Entry<Date, Map<String, Double>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			context.set("data", entry.getValue());
			result.put(entry.getKey(), (double) expression.evaluate(context));
		}

		return result;

	}

	public Object getFirstValue(PointModelMapping mapping, Date st) {
		String formula = mapping.getCalculation_formula();
		Set<String> fields = extractFields(formula);
		if (fields.size() != 1) {
			return null;
		}
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误" + Arrays.toString(fieldList));
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				DevicePoint devicePoint = devicePointRepository.findByDeviceSnAndPointKey(deviceSn, mapping.getPointModel().getKey());
				String pointDesc = mapping.getPointModel().getPointDesc();
				switch (mapping.getPointModel().getKey()) {
					case "soc":
						// 暂无对应场景
						break;
					case "soh":
						// 暂无对应场景
						break;
					default:
						return iotTsKvMeteringDevice96Repository.findFirst(mapping.getStation().getStationId(), devicePoint.getDeviceSn(),
								devicePoint.getPointSn(),
								pointDesc, st);
				}
			}
		}

		return null;
	}

	public Object getLastValue(PointModelMapping mapping, Date et) {
		String formula = mapping.getCalculation_formula();
		Set<String> fields = extractFields(formula);
		if (fields.size() != 1) {
			return null;
		}
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length != 2) {
				throw new RuntimeException("测点公式错误" + Arrays.toString(fieldList));
			} else {
				String deviceSn = fieldList[0].replaceAll("_", "-");
				if (!mapping.getPointModel().getKey().equals(fieldList[1])) {
					throw new RuntimeException("测点公式只能包含对应测点模型");
				}
				DevicePoint devicePoint = devicePointRepository.findByDeviceSnAndPointKey(deviceSn, mapping.getPointModel().getKey());
				String pointDesc = mapping.getPointModel().getPointDesc();
				switch (mapping.getPointModel().getKey()) {
					case "soc":
						// 暂无对应场景
						break;
					case "soh":
						// 暂无对应场景
						break;
					default:
						return iotTsKvMeteringDevice96Repository.findLast(mapping.getStation().getStationId(), devicePoint.getDeviceSn(),
								devicePoint.getPointSn(),
								pointDesc, et);
				}
			}
		}

		return null;
	}

	private Set<String> extractFields(String formula) {
		Set<String> fields = new HashSet<>();
		String regex = "data\\['(.*?)']";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(formula);

		while (matcher.find()) {
			fields.add(matcher.group(1));
		}
		return fields;
	}

}
