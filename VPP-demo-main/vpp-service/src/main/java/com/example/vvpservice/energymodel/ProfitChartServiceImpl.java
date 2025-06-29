package com.example.vvpservice.energymodel;
import java.util.Date;
import java.time.Instant;

import com.example.vvpcommom.RedisUtils;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.energymodel.model.PriceData;
import com.example.vvpservice.energymodel.model.ProfitRequest;
import com.example.vvpservice.energymodel.model.ProfitResponse;
import com.example.vvpservice.point.service.mappingStrategy.impl.IOTMappingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.stream.Stream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 计算收益
 *
 * @author yym
 */
@Service
@Slf4j
public class ProfitChartServiceImpl implements ProfitChartService {


	public static final String PREDICATE = "predicate";
	public static final String ACTUAL = "actual";
	@Resource
	private CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;

	@Resource
	private CfgPhotovoltaicTouPriceRepository pvPriceRepository;

	@Resource
	private AiLoadRepository aiLoadRepository;

	@Resource
	NodeRepository nodeRepository;

	@Resource
	private RedisUtils redisUtils;
	@Resource
	private CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;

	@Override
	public List<ProfitResponse> getEnergyStorageProfitChart(ProfitRequest request, Map<String, String> energyNodeMap) {

		List<ProfitResponse> responseList = new ArrayList<>();
		List<String> nodeIds = request.getNodeId();
		String systemId = request.getSystemId();
		Date startDate = TimeUtil.strFormat(request.getStartDate());
		Date endDate = TimeUtil.strFormat(request.getEndDate());
		endDate.setTime(endDate.getTime() + 86400000 - 1);

		Date startDateM = TimeUtil.getMonthStart(startDate);

		Date st = new Date();

		for (String nodeId : nodeIds) {
			// 电价信息
			List<CfgStorageEnergyStrategy> energyStrategyList = cfgStorageEnergyStrategyRepository.findCfgStorageEnergyStrategyByNodeIdAndSystemId(nodeId, systemId, startDateM, startDateM);
			List<PriceData> prices = energyStrategyList.stream().map(o -> new PriceData(o.getTimeFrame(), o.getPriceHour())).collect(Collectors.toList());

			// 储能预测收益 此处获取为功率，需要/4 = 每15分钟消耗电量
			ProfitResponse predicateResponse = new ProfitResponse();
			CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository =
					SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyBaseInfoRepository.class);
			CfgStorageEnergyBaseInfo baseInfo = cfgStorageEnergyBaseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);

			List<Double> energyStoragePredicateList =
					cfgStorageEnergyStrategyPower96Repository.findAllBySystemIdAndNodeIde(nodeId, systemId, startDate, endDate)
							.stream().map(power -> power.getStrategy().equals("充电") ? - power.getPower() : power.getPower()).map(o -> o / 4).collect(Collectors.toList());

			BiStorageEnergyLogRepository biStorageEnergyLogRepository = SpringBeanHelper.getBeanOrThrow(BiStorageEnergyLogRepository.class);
			LocalDateTime todayStart = LocalDate.now().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			Date date = Date.from(todayStart.atZone(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).atZone(ZoneId.systemDefault()).toInstant());
			BiStorageEnergyLog biStorageEnergyLog = biStorageEnergyLogRepository.findBiStorageEnergyLogByNodeIdAndTs(nodeId,date);

			double startSoc = baseInfo.getMinDischargePercent();
			if (biStorageEnergyLog != null) {
				startSoc = biStorageEnergyLog.getSoc();
			}
			List<ProfitResponse.ProfitValue> predicateValue;
			if (date.after(startDate) || date.equals(startDate)) {
				predicateValue = calculateProfitChartWithLimit(energyStoragePredicateList, prices, startDate,
						endDate,
						startSoc, baseInfo.getMinDischargePercent(), baseInfo.getMaxChargePercent(), baseInfo.getStorageEnergyCapacity());
			} else {
				predicateValue = calculateProfitChart(energyStoragePredicateList, prices, startDate, endDate);
			}

			predicateResponse.setName(energyNodeMap.get(nodeId) + "预测收益");
			predicateResponse.setType(PREDICATE);
			predicateResponse.setDataList(predicateValue);
			responseList.add(predicateResponse);

			// 储能实际收益
			ProfitResponse actualResponse = new ProfitResponse();
			List<ProfitResponse.ProfitValue> actualValue = new ArrayList<>();
			try {
				List<Double> energyStorageActualList = getEnergyStorageActualData(nodeId, startDate, endDate);
				actualValue = calculateProfitChart(energyStorageActualList, prices, startDate, endDate);
			} catch (Exception e) {

				log.error("获取储能实际收益失败，设为空值", e);
			}

			actualResponse.setName(energyNodeMap.get(nodeId) + "实际收益");
			actualResponse.setType(ACTUAL);
			actualResponse.setDataList(actualValue);
			responseList.add(actualResponse);
		}

		log.info("储能收益计算耗时：{}", new Date().getTime() - st.getTime());

		return responseList;
	}

	@Override
	public List<ProfitResponse> getPvProfitChart(ProfitRequest request, Map<String, String> energyNodeMap) {
		List<ProfitResponse> responseList = new ArrayList<>();
		String systemId = request.getSystemId();

		Date startDate = TimeUtil.strFormat(request.getStartDate());
		Date endDate = TimeUtil.strFormat(request.getEndDate());
		endDate.setTime(endDate.getTime() + 86400000 - 1);
		Date startDateM = TimeUtil.getMonthStart(startDate);

		Date st = new Date();

		List<String> nodeIds = request.getNodeId();
		for (String nodeId : nodeIds) {
			Node nodeInfo = nodeRepository.findByNodeId(nodeId);
			if (nodeInfo == null || !nodeInfo.getNodePostType().equals("pv")) {
				continue;
			}

			// 光伏价格
			List<CfgPhotovoltaicTouPrice> pvPrice = pvPriceRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, systemId, startDateM);
			List<PriceData> prices = pvPrice.stream().map(o -> new PriceData(o.getTimeFrame(), o.getPriceHour())).collect(Collectors.toList());

			// 光伏预测
			ProfitResponse pvPredicateResponse = new ProfitResponse();

			// 此处获取为功率，需要/4 = 每15分钟消耗电量
			List<Object[]> pvForecastData = aiLoadRepository.findAllByNodeIdAndSystemIdAndCountDataTimeBetween(nodeId, systemId, startDate, endDate);
			pvForecastData.sort(Comparator.comparing(o -> o[1].toString()));

			List<Double> predicateList = pvForecastData.stream().map(o -> Double.parseDouble((String) o[0]) / 4).collect(Collectors.toList());

			List<ProfitResponse.ProfitValue> predicateValue3 = calculateProfitChart(predicateList, prices, startDate, endDate);
			pvPredicateResponse.setName(energyNodeMap.get(nodeId)+"预测收益");
			pvPredicateResponse.setType(PREDICATE);
			pvPredicateResponse.setDataList(predicateValue3);
			responseList.add(pvPredicateResponse);

			// 光伏实际
			ProfitResponse pvActualResponse = new ProfitResponse();
			PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
			String energyKey = "energy";
			List<PointModelMapping> mappings =
					pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals(
							energyKey)).collect(Collectors.toList());
			Map<String, Map<Date, Double>> resMap = new HashMap<>();
			IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
			mappings.forEach(o -> {
				Map<Date, Double> energyData = iotMappingStrategy.getValues(o, startDate, endDate);
				resMap.put(o.getPointModel().getKey(), energyData);
			});
			List<Date> keyList = new ArrayList<>(resMap.get(energyKey).keySet()).stream().sorted().collect(Collectors.toList());
			List<Double> actualList = new ArrayList<>();
			for (Date date : keyList) {
				actualList.add(resMap.get(energyKey).get(date));
			}

			List<ProfitResponse.ProfitValue> predicateValue4 = calculateProfitChart(actualList, prices, startDate, endDate);
			pvActualResponse.setName(energyNodeMap.get(nodeId)+"实际收益");
			pvActualResponse.setType(ACTUAL);
			pvActualResponse.setDataList(predicateValue4);
			responseList.add(pvActualResponse);
		}

		log.info("光伏收益计算耗时：{}", new Date().getTime() - st.getTime());
		return responseList;
	}

	@Override
	public List<ProfitResponse> getProfitChartAll(Map<String, String> loadNodeMap,Map<String, String> energyNodeMap,List<String> pvNodeIdList, List<String> enNodeIdList, String systemId, String startDate, String endDate) {
		List<ProfitResponse> responseList = new ArrayList<>();
		ProfitRequest pvRequest = new ProfitRequest();
		pvRequest.setNodeId(pvNodeIdList);
		pvRequest.setSystemId(systemId);
		pvRequest.setStartDate(startDate);
		pvRequest.setEndDate(endDate);
		List<ProfitResponse> pv = getPvProfitChart(pvRequest,loadNodeMap);
		ProfitRequest enRequest = new ProfitRequest();
		enRequest.setNodeId(enNodeIdList);
		enRequest.setSystemId(systemId);
		enRequest.setStartDate(startDate);
		enRequest.setEndDate(endDate);
		List<ProfitResponse> en = getEnergyStorageProfitChart(enRequest,energyNodeMap);
		pv.addAll(en);
		List<ProfitResponse.ProfitValue> predicateAll = new ArrayList<>();
		ProfitResponse preResponse = new ProfitResponse();
		List<ProfitResponse.ProfitValue> actAll = new ArrayList<>();
		ProfitResponse actResponse = new ProfitResponse();
		for (ProfitResponse profitResponse : pv) {
			if (PREDICATE.equals(profitResponse.getType())) {
				for (int i = 0; i < profitResponse.getDataList().size(); i++) {
					if (predicateAll.size() <= i) {
						predicateAll.add(profitResponse.getDataList().get(i));
					} else {
						ProfitResponse.ProfitValue value = profitResponse.getDataList().get(i);
						predicateAll.get(i).setValue((predicateAll.get(i).getValue() == null ? 0 : predicateAll.get(i).getValue())
								+ (value.getValue() == null ? 0 : value.getValue()));
					}
				}
			}
			if (ACTUAL.equals(profitResponse.getType())) {
				for (int i = 0; i < profitResponse.getDataList().size(); i++) {
					if (actAll.size() <= i) {
						actAll.add(profitResponse.getDataList().get(i));
					} else {
						ProfitResponse.ProfitValue value = profitResponse.getDataList().get(i);
						if (actAll.get(i).getValue() == null && value.getValue() == null) {
							actAll.get(i).setValue(null);
						} else {
							actAll.get(i).setValue((actAll.get(i).getValue() == null ? 0 : actAll.get(i).getValue()) + (value.getValue() == null ?
									0 : value.getValue()));
						}

					}
				}
			}
		}
		preResponse.setName("总预测收益");
		preResponse.setType(PREDICATE);
		preResponse.setDataList(predicateAll);
		responseList.add(preResponse);
		actResponse.setName("总实际收益");
		actResponse.setType(ACTUAL);
		actResponse.setDataList(actAll);
		responseList.add(actResponse);
		return responseList;
	}

	private List<Double> getEnergyStorageActualData(String nodeId, Date startDate, Date endDate) {
		String forwardEnergyKey = "forward_active_energy";
		String backwardEnergyKey = "backward_active_energy";

		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals(
						forwardEnergyKey) || o.getPointModel().getKey().equals(backwardEnergyKey)).collect(Collectors.toList());
		Map<String, Map<Date, Double>> resMap = new HashMap<>();
		IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
		mappings.forEach(o -> {
			Map<Date, Double> energyData = iotMappingStrategy.getValues(o, startDate, endDate);
			resMap.put(o.getPointModel().getKey(), energyData);
		});
		List<Date> dateList = new ArrayList<>(resMap.get(forwardEnergyKey).keySet()).stream().sorted().collect(Collectors.toList());
		List<Double> energyStorageActualList = new ArrayList<>();
		for (Date date : dateList) {
			Double val = resMap.get(backwardEnergyKey).get(date) - resMap.get(forwardEnergyKey).get(date);
			energyStorageActualList.add(val);

		}
		return energyStorageActualList;
	}

	private List<Double> getEnergyStartegyFromRedis(String nodeId) {
		List<Double> energyStoragePredicateList;
		long startTime = System.currentTimeMillis();
		while (true) {
			energyStoragePredicateList = (List<Double>) redisUtils.get("AIStorageEnergystrategy-" + nodeId);
			if (energyStoragePredicateList != null && energyStoragePredicateList.size() > 0) {
				break;
			} else {
				if (System.currentTimeMillis() - startTime > 60000) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// 充放电策略
		if (energyStoragePredicateList == null || energyStoragePredicateList.size() != 96) {
			throw new RuntimeException("查找充放电策略失败");
		}
		return energyStoragePredicateList;
	}


	/**
	 * 根据功率列表(96个点，时间跨度15分钟)与价格列表(24个点，时间跨度1小时)计算收益
	 */
	private static List<ProfitResponse.ProfitValue> calculateProfitChart(List<Double> startegyList, List<PriceData> priceDataList, Date date, Date endDate) {
		BigDecimal sum = BigDecimal.ZERO;
		priceDataList.sort(Comparator.comparing(PriceData::getTimeFrame));
		List<ProfitResponse.ProfitValue> values = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		for (int i = 0, j = 0; (i < startegyList.size()) & (j < priceDataList.size()); ) {
			ProfitResponse.ProfitValue value = new ProfitResponse.ProfitValue();
			j = i / 4;
			while (j >= 24) {
				j -= 24;
			}
			BigDecimal v = BigDecimal.valueOf(startegyList.get(i)).multiply(priceDataList.get(j).getPrice());
			sum = sum.add(v);
			value.setValue(v.doubleValue());
			value.setTime(TimeUtil.toYmdHHmmStr_threadSafety(calendar.getTime()));
			values.add(value);
			i++;
			calendar.add(Calendar.MINUTE, 15);
		}
		// 填充无数据部分
		ProfitResponse.ProfitValue end = new ProfitResponse.ProfitValue();
		if (!values.isEmpty()) {
			end = values.get(values.size() - 1);
		}

		while (calendar.getTime().before(endDate)) {
			ProfitResponse.ProfitValue addPart = new ProfitResponse.ProfitValue(end);
			addPart.setTime(TimeUtil.toYmdHHmmStr_threadSafety(calendar.getTime()));
			addPart.setValue(null);
			values.add(addPart);
			calendar.add(Calendar.MINUTE, 15);
		}

		log.info(String.valueOf(sum));
		return values;
	}

	private static List<ProfitResponse.ProfitValue> calculateProfitChartWithLimit(List<Double> startegyList, List<PriceData> priceDataList,
	                                                                              Date date,
	                                                                              Date endDate, double soc, double min, double max,
	                                                                              double capacity) {
		BigDecimal sum = BigDecimal.ZERO;
		priceDataList.sort(Comparator.comparing(PriceData::getTimeFrame));
		List<ProfitResponse.ProfitValue> values = new ArrayList<>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		for (int i = 0, j = 0; (i < startegyList.size()) & (j < priceDataList.size()); ) {
			ProfitResponse.ProfitValue value = new ProfitResponse.ProfitValue();
			j = i / 4;
			while (j >= 24) {
				j -= 24;
			}
			soc -= 100 * startegyList.get(i) / (capacity);
			BigDecimal v;
			if (soc >= max) {
				soc = max;
				v = BigDecimal.valueOf(capacity * (max - soc) / 100).multiply(priceDataList.get(j).getPrice());
			} else if (soc <= min) {
				soc = min;
				v = BigDecimal.valueOf(capacity * (soc - min) / 100).multiply(priceDataList.get(j).getPrice());
			} else {
				v = BigDecimal.valueOf(startegyList.get(i)).multiply(priceDataList.get(j).getPrice());
			}

			sum = sum.add(v);
			value.setValue(v.doubleValue());
			value.setTime(TimeUtil.toYmdHHmmStr_threadSafety(calendar.getTime()));
			values.add(value);
			i++;
			calendar.add(Calendar.MINUTE, 15);
		}
		// 填充无数据部分
		ProfitResponse.ProfitValue end = new ProfitResponse.ProfitValue();
		if (!values.isEmpty()) {
			end = values.get(values.size() - 1);
		}

		while (calendar.getTime().before(endDate)) {
			ProfitResponse.ProfitValue addPart = new ProfitResponse.ProfitValue(end);
			addPart.setTime(TimeUtil.toYmdHHmmStr_threadSafety(calendar.getTime()));
			addPart.setValue(null);
			values.add(addPart);
			calendar.add(Calendar.MINUTE, 15);
		}

		log.info(String.valueOf(sum));
		return values;
	}

}
