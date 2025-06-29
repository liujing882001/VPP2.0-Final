package com.example.vvpservice.point.service.mappingStrategy.impl;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.pointmodel.PointConstant;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.AiLoadForecasting;
import com.example.vvpdomain.entity.CfgStorageEnergyStrategyPower96;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.point.service.mappingStrategy.MappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.jexl3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class CalculatePointMappingStrategy implements MappingStrategy {

	public static final int LENGTH_OF_DB = 3;

	private final PointModelMappingRepository pointModelMappingRepository;

	private final IOTMappingStrategy iotMappingStrategy;

	private final JexlEngine jexlEngine = new JexlBuilder().create();

	@Autowired
	public CalculatePointMappingStrategy(PointModelRepository pointModelRepository, PointModelMappingRepository pointModelMappingRepository,
	                                     IOTMappingStrategy iotMappingStrategy) {
		this.pointModelMappingRepository = pointModelMappingRepository;
		this.iotMappingStrategy = iotMappingStrategy;
	}

	@Override
	public String getStrategyType() {
		return PointConstant.DataType.CALCULATION_POINT;
	}

	@Override
	public void buildMapping(StationNode node) {
//		List<PointModelMapping> mappings = new ArrayList<>();
//		// step1: 获取属于此系统下的所有点位模型
//		Map<String, PointModel> pointModelMap = pointModelRepository.findAllBySystem(systemId)
//				.stream()
//				.filter(pointModel -> pointModel.getPointType().equals(PointConstant.DataType.Calculation_Point))
//				.collect(Collectors.toMap(PointModel::getKey, Function.identity()));
//
//		// step2: 建立映射关系
//		for (Map.Entry<String, PointModel> entry : pointModelMap.entrySet()) {
//			PointModelMapping mapping = new PointModelMapping(node, entry.getValue(), node.getStationId(), false);
//			mappings.add(mapping);
//		}
//
//		pointModelMappingRepository.saveOrUpdateAll(mappings);
	}

	@Override
	public Map<Date, ?> getValues(PointModelMapping mapping, Date st, Date et) {
		Map<Date, Object> result = new HashMap<>();
		String formula = mapping.getCalculation_formula();
		Map<String, List<PointModelMapping>> map =
				pointModelMappingRepository.findAll()
						.stream().filter(pointModelMapping -> pointModelMapping.getPointModel().getPointType().equals(PointConstant.DataType.MEASURING_POINT))
						.collect(Collectors.groupingBy(o -> o.getPointModel().getKey()));
		JexlExpression expression = jexlEngine.createExpression(formula);

		Map<String, Map<Date, ?>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length == LENGTH_OF_DB) {
				Map<Date, ?> v = getValuesFromDb(fieldList, st, et);
//				boolean allValuesAreDouble = v.values().stream().allMatch(value -> value instanceof Double);
//				if (!allValuesAreDouble){
//					throw new IllegalArgumentException("公式错误");
//				}
				values.put(field, v);
			} else {
				String pointKey = fieldList[fieldList.length - 1];
				if (map.containsKey(pointKey)) {
					for (PointModelMapping pointModelMapping : map.get(pointKey)) {
						if (pointModelMapping.getCalculation_formula().contains("data['" + field + "']")) {
							Map<Date, Double> v = iotMappingStrategy.getValues(pointModelMapping, st, et);
							values.put(field, v);
							break;
						}
					}
				}
			}
		}
		// 交换后的结果 Map
		Map<Date, Map<String, Object>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, ?>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, ?> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}
		getResult(result,swappedValues,fields,expression);
		return result;
	}
	@Override
	public Map<Date, ?> getDValues(PointModelMapping mapping, Date st, Date et) {
		Map<Date, Object> result = new HashMap<>();
		String formula = mapping.getCalculation_formula();
		Map<String, List<PointModelMapping>> map =
				pointModelMappingRepository.findAll()
						.stream().filter(pointModelMapping -> pointModelMapping.getPointModel().getPointType().equals(PointConstant.DataType.MEASURING_POINT))
						.collect(Collectors.groupingBy(o -> o.getPointModel().getKey()));

		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, Map<Date, ?>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length == LENGTH_OF_DB) {
				Map<Date, ?> v = getValuesFromDb(fieldList, st, et);
				values.put(field, v);
			} else {
				String pointKey = fieldList[fieldList.length - 1];
				if (map.containsKey(pointKey)) {
					for (PointModelMapping pointModelMapping : map.get(pointKey)) {
						if (pointModelMapping.getCalculation_formula().contains("data['" + field + "']")) {
							Map<Date, Double> v = iotMappingStrategy.getValues(pointModelMapping, st, et);
							values.put(field, v);
							break;
						}
					}
				}
			}
		}
		// 交换后的结果 Map
		Map<Date, Map<String, Object>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, ?>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, ?> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}
		getDResult(result,swappedValues,fields,expression);
		return result;
	}
	@Override
	public Map<Long, Double> getLDValues(PointModelMapping mapping, Date st, Date et) {
		Map<Date, Object> result = new HashMap<>();
		String formula = mapping.getCalculation_formula();
		Map<String, List<PointModelMapping>> map =
				pointModelMappingRepository.findAll()
						.stream().filter(pointModelMapping -> pointModelMapping.getPointModel().getPointType().equals(PointConstant.DataType.MEASURING_POINT))
						.collect(Collectors.groupingBy(o -> o.getPointModel().getKey()));
		JexlExpression expression = jexlEngine.createExpression(formula);
		Map<String, Map<Date, ?>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length == LENGTH_OF_DB) {
				Map<Date, ?> v = getValuesFromDb(fieldList, st, et);
				values.put(field, v);
			} else {
				String pointKey = fieldList[fieldList.length - 1];
				if (map.containsKey(pointKey)) {
					for (PointModelMapping pointModelMapping : map.get(pointKey)) {
						if (pointModelMapping.getCalculation_formula().contains("data['" + field + "']")) {
							Map<Date, Double> v = iotMappingStrategy.getValues(pointModelMapping, st, et);
							values.put(field, v);
							break;
						}
					}
				}
			}
		}
		// 交换后的结果 Map
		Map<Date, Map<String, Object>> swappedValues = new HashMap<>();

		// 遍历原始 Map，将内外层键交换并构建 JexlContext
		for (Map.Entry<String, Map<Date, ?>> entryOuter : values.entrySet()) {
			String outerKey = entryOuter.getKey();

			for (Map.Entry<Date, ?> entryInner : entryOuter.getValue().entrySet()) {
				swappedValues.computeIfAbsent(entryInner.getKey(), k -> new HashMap<>()).put(outerKey, entryInner.getValue());
			}
		}
		return getSDResult(result,swappedValues,fields,expression);
	}
	private Map<Long, Double> getSDResult(Map<Date, Object> result,Map<Date, Map<String, Object>> swappedValues,Set<String> fields,JexlExpression expression) {
		for (Map.Entry<Date, Map<String, Object>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			if (entry.getValue() == null) {
				result.put(entry.getKey(), null);
				continue;
			}
			Map<String, Object> data = entry.getValue();
			boolean allFieldsNull = true;
			for (String field : fields) {
				if (data.containsKey(field) && data.get(field) != null) {
					allFieldsNull = false;
				} else {
					data.put(field, 0);
				}
			}
			if (allFieldsNull) {
				result.put(entry.getKey(), null);
				continue;
			}
			context.set("data", data);
			result.put(entry.getKey(), expression.evaluate(context));
		}
		Map<Long, Double> dResult = new TreeMap<>();
		result.entrySet()
				.stream()
				.filter(entry -> entry.getValue() instanceof Number || entry.getValue() == null)
				.forEach(entry -> dResult.put(entry.getKey().getTime(), (Double) entry.getValue()));

		for (Map.Entry<Long, Double> dR : dResult.entrySet()) {
			Long k = dR.getKey();
			Double v = dR.getValue();
			if (v == null) {
				continue;
			}
			Double nextValue = dResult.get(k + 900000);
			if (nextValue == null) {
				continue;
			}
			if (nextValue < 0) {
				continue;
			}
			Double preValue = dResult.get(k - 900000);
			if (preValue == null) {
				continue;
			}
			if (preValue < 0) {
				continue;
			}
			if (v < 0) {
//				Date madeDate;
//				if (preValue > nextValue) {
//					madeDate = new Date(k - 900000);
//				} else {
//					madeDate = new Date(k + 900000);
//				}
//				Map<String, Object> preData = swappedValues.get(madeDate);
//				List<Map<String, Object>> combinations = new ArrayList<>();
//				for (Map.Entry<String, Object> nde : preData.entrySet()) {
//					Map<String, Object> singlePair = new HashMap<>();
//					singlePair.put(nde.getKey(), nde.getValue());
//					combinations.add(singlePair);
//				}
//				for (Map<String, Object> dataCombination : combinations) {
//					Map<String, Object> nowData = new HashMap<>(swappedValues.get(new Date(k)));
//					nowData.putAll(dataCombination);

				Map<String, Object> nextData = swappedValues.get(new Date(k + 900000));
				for (Map.Entry<String, Object> nde : nextData.entrySet()) {
					String nk = nde.getKey();
					Object nv = nde.getValue();
					Map<String, Object> nowData = swappedValues.get(new Date(k));
					nowData.put(nk, nv);

					JexlContext context = new MapContext();
					context.set("data", nowData);
					Object nowDO = expression.evaluate(context);
					if (nowDO == null) {
						continue;
					}
					Double nowD = (Double) nowDO;
//					if (nowD.equals(preValue)) {
//						continue;
//					}
					if (nowD > 0) {
						v = nowD;
						dR.setValue(v);
					}
				}
			}

			if (Math.abs(v - preValue) > 20 && Math.abs(v - nextValue) > 20) {
//				log.info("k 的值:{}",new Date(k));
//				log.info("v 的值:{}",v);
				Map<String, Object> nextData = swappedValues.get(new Date(k + 900000));
				List<Map<String, Object>> combinations = new ArrayList<>();
				for (Map.Entry<String, Object> nde : nextData.entrySet()) {
					Map<String, Object> singlePair = new HashMap<>();
					singlePair.put(nde.getKey(), nde.getValue());
					combinations.add(singlePair);
				}
				List<Map.Entry<String, Object>> entries = new ArrayList<>(nextData.entrySet());
				for (int i = 0; i < entries.size(); i++) {
					for (int j = i + 1; j < entries.size(); j++) {
						String key1 = entries.get(i).getKey();
						Object value1 = entries.get(i).getValue();
						String key2 = entries.get(j).getKey();
						Object value2 = entries.get(j).getValue();
						Map<String, Object> combinedData = new HashMap<>();
						combinedData.put(key1, value1);
						combinedData.put(key2, value2);
						combinations.add(combinedData);
					}
				}
				for (Map<String, Object> dataCombination : combinations) {
					Map<String, Object> nowData = new HashMap<>(swappedValues.get(new Date(k)));
					nowData.putAll(dataCombination);

					JexlContext context = new MapContext();
					context.set("data", nowData);
					Object nowDO = expression.evaluate(context);
					if (nowDO == null) {
						continue;
					}
					Double nowD = (Double) nowDO;
//					log.info("nowD 的值:{}",nowD);
//					log.info("preValue 的值:{}",preValue);
//					log.info("nextValue 的值:{}",nextValue);
//
//					if (preValue > nextValue) {
//						if ((int) Math.abs(nowD - preValue) <= 30 ) {
//							v = nowD;
//							dR.setValue(v);
//						}
//					} else {
//						if ((int) Math.abs(nowD - nextValue) <= 20) {
//							v = nowD;
//							dR.setValue(v);
//						}
//					}
					if ((int) Math.abs(nowD - preValue) <= 30 && (int) Math.abs(nowD - nextValue) <= 20) {
						v = nowD;
						dR.setValue(v);
					}
				}
			}

		}
		return dResult;
	}
	private Map<Date, Object> getDResult(Map<Date, Object> result,Map<Date, Map<String, Object>> swappedValues,Set<String> fields,JexlExpression expression) {
		for (Map.Entry<Date, Map<String, Object>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			if (entry.getValue() == null) {
				result.put(entry.getKey(), null);
				continue;
			}
			Map<String, Object> data = entry.getValue();
			boolean allFieldsNull = true;
			for (String field : fields) {
				if (data.containsKey(field) && data.get(field) != null) {
					allFieldsNull = false;
				} else {
					data.put(field, 0);
				}
			}
			if (allFieldsNull) {
				result.put(entry.getKey(), null);
				continue;
			}
			context.set("data", data);
			result.put(entry.getKey(), expression.evaluate(context));
		}
		Map<Long, Double> dResult = new TreeMap<>();
		result.entrySet()
				.stream()
				.filter(entry -> entry.getValue() instanceof Number || entry.getValue() == null)
				.forEach(entry -> dResult.put(entry.getKey().getTime(), (Double) entry.getValue()));

		for (Map.Entry<Long, Double> dR : dResult.entrySet()) {
			Long k = dR.getKey();
			Double v = dR.getValue();
			if (v == null) {
				continue;
			}
			Double nextValue = dResult.get(k + 900000);
			if (nextValue == null) {
				continue;
			}
			if (nextValue < 0) {
				continue;
			}
			Double preValue = dResult.get(k - 900000);
			if (preValue == null) {
				continue;
			}
			if (preValue < 0) {
				continue;
			}
			if (v < 0) {
//				Date madeDate;
//				if (preValue > nextValue) {
//					madeDate = new Date(k - 900000);
//				} else {
//					madeDate = new Date(k + 900000);
//				}
//				Map<String, Object> preData = swappedValues.get(madeDate);
//				List<Map<String, Object>> combinations = new ArrayList<>();
//				for (Map.Entry<String, Object> nde : preData.entrySet()) {
//					Map<String, Object> singlePair = new HashMap<>();
//					singlePair.put(nde.getKey(), nde.getValue());
//					combinations.add(singlePair);
//				}
//				for (Map<String, Object> dataCombination : combinations) {
//					Map<String, Object> nowData = new HashMap<>(swappedValues.get(new Date(k)));
//					nowData.putAll(dataCombination);

					Map<String, Object> nextData = swappedValues.get(new Date(k + 900000));
				for (Map.Entry<String, Object> nde : nextData.entrySet()) {
					String nk = nde.getKey();
					Object nv = nde.getValue();
					Map<String, Object> nowData = swappedValues.get(new Date(k));
					nowData.put(nk, nv);

					JexlContext context = new MapContext();
					context.set("data", nowData);
					Object nowDO = expression.evaluate(context);
					if (nowDO == null) {
						continue;
					}
					Double nowD = (Double) nowDO;
//					if (nowD.equals(preValue)) {
//						continue;
//					}
					if (nowD > 0) {
						v = nowD;
						dR.setValue(v);
					}
				}
			}

			if (Math.abs(v - preValue) > 20 && Math.abs(v - nextValue) > 20) {
//				log.info("k 的值:{}",new Date(k));
//				log.info("v 的值:{}",v);
				Map<String, Object> nextData = swappedValues.get(new Date(k + 900000));
				List<Map<String, Object>> combinations = new ArrayList<>();
				for (Map.Entry<String, Object> nde : nextData.entrySet()) {
					Map<String, Object> singlePair = new HashMap<>();
					singlePair.put(nde.getKey(), nde.getValue());
					combinations.add(singlePair);
				}
				List<Map.Entry<String, Object>> entries = new ArrayList<>(nextData.entrySet());
				for (int i = 0; i < entries.size(); i++) {
					for (int j = i + 1; j < entries.size(); j++) {
						String key1 = entries.get(i).getKey();
						Object value1 = entries.get(i).getValue();
						String key2 = entries.get(j).getKey();
						Object value2 = entries.get(j).getValue();
						Map<String, Object> combinedData = new HashMap<>();
						combinedData.put(key1, value1);
						combinedData.put(key2, value2);
						combinations.add(combinedData);
					}
				}
				for (Map<String, Object> dataCombination : combinations) {
					Map<String, Object> nowData = new HashMap<>(swappedValues.get(new Date(k)));
					nowData.putAll(dataCombination);

					JexlContext context = new MapContext();
					context.set("data", nowData);
					Object nowDO = expression.evaluate(context);
					if (nowDO == null) {
						continue;
					}
					Double nowD = (Double) nowDO;
//					log.info("nowD 的值:{}",nowD);
//					log.info("preValue 的值:{}",preValue);
//					log.info("nextValue 的值:{}",nextValue);
//
//					if (preValue > nextValue) {
//						if ((int) Math.abs(nowD - preValue) <= 30 ) {
//							v = nowD;
//							dR.setValue(v);
//						}
//					} else {
//						if ((int) Math.abs(nowD - nextValue) <= 20) {
//							v = nowD;
//							dR.setValue(v);
//						}
//					}
					if ((int) Math.abs(nowD - preValue) <= 30 && (int) Math.abs(nowD - nextValue) <= 20) {
						v = nowD;
						dR.setValue(v);
					}
				}
			}

		}
		dResult.forEach((key, value) -> result.put(new Date(key), value));
		return result;
	}
	@Override
	public List<?> getValues(PointModelMapping mapping, int count) {
		return getValuesFromFormula(mapping, count);
	}

	private Map<Date, Object> getResult(Map<Date, Object> result,Map<Date, Map<String, Object>> swappedValues,Set<String> fields,JexlExpression expression) {
		for (Map.Entry<Date, Map<String, Object>> entry : swappedValues.entrySet()) {
			JexlContext context = new MapContext();
			if (entry.getValue() == null) {
				result.put(entry.getKey(), null);
				continue;
			}
			Map<String, Object> data = entry.getValue();
			boolean allFieldsNull = true;
			for (String field : fields) {
				if (data.containsKey(field) && data.get(field) != null) {
					allFieldsNull = false;
				} else {
					data.put(field, 0);
				}
			}
			if (allFieldsNull) {
				result.put(entry.getKey(), null);
				continue;
			}
			context.set("data", data);
			result.put(entry.getKey(), expression.evaluate(context));
		}
		return result;
	}
	private List<?> getValuesFromFormula(PointModelMapping mapping, int count) {
		String formula = mapping.getCalculation_formula();
		Map<String, List<PointModelMapping>> map =
				pointModelMappingRepository.findAll()
						.stream().filter(pointModelMapping -> pointModelMapping.getPointModel().getPointType().equals(PointConstant.DataType.MEASURING_POINT))
						.collect(Collectors.groupingBy(o -> o.getPointModel().getKey()));
		JexlExpression expression = jexlEngine.createExpression(formula);

		Map<String, List<?>> values = new HashMap<>();
		Set<String> fields = extractFields(formula);

		List<Object> result = new ArrayList<>();

		int size = Integer.MAX_VALUE;
		for (String field : fields) {
			String[] fieldList = field.split("\\.");
			if (fieldList.length == LENGTH_OF_DB) {
				List<?> v = getValuesFromDb(fieldList, count);
				if (v.size() < size) {
					size = v.size();
				}
				values.put(field, v);
			} else {
				String pointKey = fieldList[fieldList.length - 1];
				if (map.containsKey(pointKey)) {
					for (PointModelMapping pointModelMapping : map.get(pointKey)) {
						if (pointModelMapping.getCalculation_formula().contains("data['" + field + "']")) {
							List<?> v = iotMappingStrategy.getValues(pointModelMapping, count);
							if (v.size() < size) {
								size = v.size();
							}
							values.put(field, v);
							break;
						}
					}
				}
			}
		}
		for (int i = 0; i < size; i++) {
			JexlContext context = new MapContext();
			Map<String, Object> dataMap = new HashMap<>();

			for (String field : fields) {
				dataMap.put(field, values.get(field).get(i));
				context.set("data", dataMap);
			}
			result.add(expression.evaluate(context));
		}
		return result;
	}

	// 从公式中提取所有字段名
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

	private List<?> getValuesFromDb(String[] formula, int count) {
		String tableName = formula[0];
		String nodeId = formula[1].replaceAll("_", "");
		String colName = formula[2];

		switch (tableName) {
			case "node": {
				NodeRepository nodeRepository = SpringBeanHelper.getBeanOrThrow(NodeRepository.class);
				if (colName.equals("online")) {
					return Collections.singletonList(nodeRepository.findByNodeId(nodeId).getOnline());
				}

			}
			case "cfg_storage_energy_strategy_power_96": {
				Specification<CfgStorageEnergyStrategyPower96> spec = (root, query, criteriaBuilder) -> {
					// 构建查询条件
					return criteriaBuilder.and(criteriaBuilder.equal(root.get("nodeId"), nodeId));
				};
				PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "effectiveDate"));
				CfgStorageEnergyStrategyPower96Repository repository =
						SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyPower96Repository.class);
				if (colName.equals("strategy")) {
					return repository.findAll(spec, pageable).getContent();
				}
				if (colName.equals("power")) {
					List<CfgStorageEnergyStrategyPower96> power96s = repository.findAll(spec, pageable).getContent();
					List<Double> res = new ArrayList<>();
					power96s.forEach(o -> {
						switch (o.getStrategy()) {
							case "充电":
								res.add(-o.getPower());
								break;
							case "放电":
								res.add(o.getPower());
								break;
							default:
								res.add(0.0);
						}
					});
					return res;
				}
			}
			case "ai_load_forecasting": {
				Specification<AiLoadForecasting> spec = (root, query, criteriaBuilder) -> {
					// 构建查询条件
					return criteriaBuilder.and(criteriaBuilder.equal(root.get("nodeId"), nodeId),
							criteriaBuilder.equal(root.get("systemId"),"nengyuanzongbiao")
					);
				};
				PageRequest pageable = PageRequest.of(0, count, Sort.by(Sort.Direction.DESC, "countDataTime"));
				AiLoadRepository repository = SpringBeanHelper.getBeanOrThrow(AiLoadRepository.class);
				if (colName.equals("predict_value")) {
					return repository.findAll(spec, pageable).getContent();
				}
			}
			default: {
				throw new IllegalArgumentException("formula not defined.");
			}
		}
	}

	private Map<Date, ?> getValuesFromDb(String[] formula, Date st, Date et) {
		String tableName = formula[0];
		String nodeId = formula[1].replaceAll("_", "");
		String colName = formula[2];

		switch (tableName) {
			case "node": {
				throw new IllegalArgumentException("point model search by time not supported");
			}
			case "cfg_storage_energy_strategy_power_96": {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
				Date startDate = TimeUtil.strFormat(dateFormat.format(st));
				startDate.setTime(startDate.getTime() - 1);
				Date endDate = TimeUtil.strFormat(dateFormat.format(et));
				endDate.setTime(endDate.getTime() + 86400000 - 1);

				Specification<CfgStorageEnergyStrategyPower96> spec = (root, query, criteriaBuilder) -> {
					// 构建查询条件
					return criteriaBuilder.and(criteriaBuilder.equal(root.get("nodeId"), nodeId),
							criteriaBuilder.lessThanOrEqualTo(root.get("effectiveDate"), endDate),
							criteriaBuilder.greaterThanOrEqualTo(root.get("effectiveDate"), startDate));
				};
				CfgStorageEnergyStrategyPower96Repository repository =
						SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyPower96Repository.class);
				if (colName.equals("strategy")) {
					List<CfgStorageEnergyStrategyPower96> res = repository.findAll(spec);
					Map<Date, String> resultMap = new HashMap<>();
					res.forEach(o -> {
						// 将字符串转换为 LocalDate 和 LocalTime
						LocalDate localDate = o.getEffectiveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalTime time = Objects.equals(o.getETime(), "24:00") ? LocalTime.MIDNIGHT : LocalTime.parse(o.getETime());

						// 合并成一个 LocalDateTime 对象
						LocalDateTime dateTime = LocalDateTime.of(localDate, time);
						if (time.equals(LocalTime.MIDNIGHT)) {
							dateTime = dateTime.plusDays(1);
						}
						resultMap.put(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()), o.getStrategy());
					});
					return resultMap;
				}
				if (colName.equals("power")) {
					List<CfgStorageEnergyStrategyPower96> res = repository.findAll(spec);
					Map<Date, Double> resultMap = new HashMap<>();
					res.forEach(o -> {
						// 将字符串转换为 LocalDate 和 LocalTime
						LocalDate localDate = o.getEffectiveDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
						LocalTime time = Objects.equals(o.getETime(), "24:00") ? LocalTime.MIDNIGHT : LocalTime.parse(o.getETime());

						// 合并成一个 LocalDateTime 对象
						LocalDateTime dateTime = LocalDateTime.of(localDate, time);
						if (time.equals(LocalTime.MIDNIGHT)) {
							dateTime = dateTime.plusDays(1);
						}
						resultMap.put(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()),
								o.getStrategy().equals("充电") ? -o.getPower() : (o.getStrategy().equals("待机") ? 0 : o.getPower()));
					});
					return resultMap;
				}
			}
			case "ai_load_forecasting": {
				Specification<AiLoadForecasting> spec = (root, query, criteriaBuilder) -> {
					// 构建查询条件
					return criteriaBuilder.and(criteriaBuilder.equal(root.get("nodeId"), nodeId),
							criteriaBuilder.lessThan(root.get("countDataTime"), et),
							criteriaBuilder.greaterThanOrEqualTo(root.get("countDataTime"), st),
							criteriaBuilder.equal(root.get("systemId"),"nengyuanzongbiao")
					);
				};
				AiLoadRepository repository = SpringBeanHelper.getBeanOrThrow(AiLoadRepository.class);
				Map<Date, Double> resultMap = new HashMap<>();
				if (colName.equals("predict_value")) {
					List<AiLoadForecasting> res = repository.findAll(spec);

					res.forEach(o -> resultMap.put(o.getCountDataTime(), o.getPredictValue() == null ? 0.0 :
							Double.parseDouble(o.getPredictValue())));
				}
				return resultMap;
			}
			default: {
				throw new IllegalArgumentException("formula not defined.");
			}
		}
	}
}
