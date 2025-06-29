package com.example.vvpservice.nodeprofit.service;
import java.util.Date;
import java.time.Instant;
import java.time.LocalTime;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import java.time.ZoneId;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.Enum.ElectricityBillNodeEnum;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.nodeep.model.ElectricityInfoModel;
import com.example.vvpservice.nodeprofit.model.BillNodeProfit;
import com.example.vvpservice.point.service.mappingStrategy.impl.IOTMappingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class NodeProfitServiceImpl implements INodeProfitService {


	private static final String SYSTEMID = "nengyuanzongbiao";
	private static final String POINTDESC = "energy";

	/**
	 * 负荷集成商
	 */
	private static final String LOADAGGREGATOR = "loadAggregator";
	/**
	 * 电力用户
	 */
	private static final String ELECTRICITYCONSUMERS = "electricityConsumers";
	private static Logger logger = LoggerFactory.getLogger(NodeProfitServiceImpl.class);
	@Autowired
	private NodeRepository nodeRepository;

	@Autowired
	private SysDictTypeRepository sysDictTypeRepository;


	@Autowired
	private NodeProfitRepository nodeProfitRepository;

	@Resource
	private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
	@Resource
	private CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository;
	@Resource
	private DevicePointRepository devicePointRepository;


	@Override
	public String getLoadIntegrator(String nodeId) {
		return getNames(nodeId, LOADAGGREGATOR);

	}

	@Override
	public String getConsumer(String nodeId) {
		return getNames(nodeId, ELECTRICITYCONSUMERS);
	}

	@Override
	public List<String> getPvNodeIdList() {
		return nodeIdList(Arrays.asList("guangfu"));
	}

	@Override
	public List<String> getStoreEnergyNodeIdList() {
		return nodeIdList(Arrays.asList("chuneng"));
	}

	@Override
	public List<String> getChargingPileNodeIdList() {
		List<String> responses = new ArrayList<>();
		List<Node> all = nodeRepository.findAllByNodeType_NodeTypeIdAndNodePostType(ElectricityBillNodeEnum.chargingPile.getNodeTypeId(),
				ElectricityBillNodeEnum.chargingPile.getNodeTypeId());
		all.forEach(e -> responses.add(e.getNodeId()));
		return responses;
	}

	@Override
	public BillNodeProfit getBillNodeProfit(String nodeId, Date start, Date end) {
		BillNodeProfit billNodeProfit = new BillNodeProfit();
		List<NodeProfit> allByNodeIdAndProfitDateBetween = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId,
				start, end);
//        logger.info("allByNodeIdAndProfitDateBetween:{}",JSONObject.toJSONString(allByNodeIdAndProfitDateBetween));
		if (allByNodeIdAndProfitDateBetween != null && !allByNodeIdAndProfitDateBetween.isEmpty()) {
			allByNodeIdAndProfitDateBetween.stream().forEach(l -> {
				if (billNodeProfit.getPriceHigh() == 0 && l.getPriceHigh() != 0) {
					billNodeProfit.setPriceHigh(l.getPriceHigh());
				}
				if (billNodeProfit.getPriceLow() == 0 && l.getPriceLow() != 0) {
					billNodeProfit.setPriceLow(l.getPriceLow());
				}
				if (billNodeProfit.getPricePeak() == 0 && l.getPricePeak() != 0) {
					billNodeProfit.setPricePeak(l.getPricePeak());
				}
				if (billNodeProfit.getPriceStable() == 0 && l.getPriceStable() != 0) {
					billNodeProfit.setPriceStable(l.getPriceStable());
				}
//				if (billNodeProfit.getPriceRavine() == 0 && l.getPriceRavine() != 0) {
//					billNodeProfit.setPriceRavine(l.getPriceRavine());
//				}
			});

			double sum = 0.0;
			double result = 0.0;
			double sum1 = 0.0;
			double result1 = 0.0;
			double sum2 = 0.0;
			double result2 = 0.0;
			double sum3 = 0.0;
			double result3 = 0.0;
			double sum4 = 0.0;
			double result4 = 0.0;

			double sumPrice = 0.0;
			double resultPrice = 0.0;
			double sum1Price = 0.0;
			double result1Price = 0.0;
			double sum2Price = 0.0;
			double result2Price = 0.0;
			double sum3Price = 0.0;
			double result3Price = 0.0;
			double sum4Price = 0.0;
			double result4Price = 0.0;
			for (NodeProfit nodeProfit : allByNodeIdAndProfitDateBetween) {
				double inElectricityHigh = nodeProfit.getInElectricityHigh();
				sum += inElectricityHigh;
				sumPrice += nodeProfit.getPriceHigh() * inElectricityHigh;

				double inElectricityPeak = nodeProfit.getInElectricityPeak();
				result += inElectricityPeak;
				resultPrice += inElectricityPeak * nodeProfit.getPricePeak();

				double inElectricityStable = nodeProfit.getInElectricityStable();
				sum1 += inElectricityStable;
				sum1Price += inElectricityStable * nodeProfit.getPriceStable();

				double inElectricityLow = nodeProfit.getInElectricityLow();
				result1 += inElectricityLow;
				result1Price += inElectricityLow * nodeProfit.getPriceLow();

//				double inElectricityRavine = nodeProfit.getInElectricityRavine();
//				sum4 += inElectricityRavine;
//				sum4Price += inElectricityRavine * nodeProfit.getPriceRavine();

				double outElectricityHigh = nodeProfit.getOutElectricityHigh();
				sum2 += outElectricityHigh;
				sum2Price += outElectricityHigh * nodeProfit.getPriceHigh();

				double outElectricityPeak = nodeProfit.getOutElectricityPeak();
				result2 += outElectricityPeak;
				result2Price += outElectricityPeak * nodeProfit.getPricePeak();

				double outElectricityStable = nodeProfit.getOutElectricityStable();
				sum3 += outElectricityStable;
				sum3Price += outElectricityStable * nodeProfit.getPriceStable();

				double outElectricityLow = nodeProfit.getOutElectricityLow();
				result3 += outElectricityLow;
				result3Price += outElectricityLow * nodeProfit.getPriceLow();

//				double outElectricityRavine = nodeProfit.getOutElectricityRavine();
//				result4 += outElectricityRavine;
//				result4Price += outElectricityRavine * nodeProfit.getPriceRavine();

			}
			billNodeProfit.setInElectricityHigh(sum);
			billNodeProfit.setInElectricityHighPrice(sumPrice);
			billNodeProfit.setInElectricityPeak(result);
			billNodeProfit.setInElectricityPeakPrice(resultPrice);
			billNodeProfit.setInElectricityStable(sum1);
			billNodeProfit.setInElectricityStablePrice(sum1Price);
			billNodeProfit.setInElectricityLow(result1);
			billNodeProfit.setInElectricityLowPrice(result1Price);
//			billNodeProfit.setInElectricityRavine(sum4);
//			billNodeProfit.setInElectricityRavinePrice(sum4Price);

			billNodeProfit.setOutElectricityHigh(sum2);
			billNodeProfit.setOutElectricityHighPrice(sum2Price);
			billNodeProfit.setOutElectricityPeak(result2);
			billNodeProfit.setOutElectricityPeakPrice(result2Price);
			billNodeProfit.setOutElectricityStable(sum3);
			billNodeProfit.setOutElectricityStablePrice(sum3Price);
			billNodeProfit.setOutElectricityLow(result3);
			billNodeProfit.setOutElectricityLowPrice(result3Price);
//			billNodeProfit.setOutElectricityRavine(result4);
//			billNodeProfit.setOutElectricityRavinePrice(result4Price);

		}

		return billNodeProfit;

	}

	private List<String> nodeIdList(List<String> systemIds) {
		List<String> sysIds =
				sysDictTypeRepository.findAllBySystemIdIn(systemIds).stream().map(SysDictType::getSystemId).collect(Collectors.toList());
		List<String> result = new ArrayList<>();
		List<Node> all = nodeRepository.findAll();
		all.forEach(e -> {
			List<String> syss = JSONObject.parseArray(e.getSystemIds(), String.class);
			syss.retainAll(sysIds);

			if (!syss.isEmpty()) {
				result.add(e.getNodeId());
			}
		});
		return result;
	}

	private String getNames(String nodeId, String type) {
		List<String> userNameList = new ArrayList<>();

//        Optional<Node> byId = nodeRepository.findById(nodeId);
//        if(byId.isPresent()){
//            Node node = byId.get();
//            List<MarketSubject> marketSubjectList = node.getMarketSubjectList();
//
//            if(marketSubjectList!=null){
//                marketSubjectList.forEach(e->{
//                    if(type.equals(e.getMarketSubjectType())){
//                        userNameList.add(e.getMarketSubjectName());
//                    }
//                });
//            }
//
//        }

		return userNameList.stream().collect(Collectors.joining(","));
	}


	public void doCalculatePvProfit(String nodeId, Map<String, BigDecimal> priceMap, Map<LocalTime, String> propertyMap,
	                                Date date) {
		String energyKey = "energy";

//		ElectricityHolidayRepository electricityHolidayRepository = SpringBeanHelper.getBeanOrThrow(ElectricityHolidayRepository.class);
//		LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate startOfDate = today.withDayOfMonth(1);
//		LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
//		List<ElectricityHolidayInfo> ravineInfos = electricityHolidayRepository.findAllByPk_NodeIdAndPk_DateBetween(nodeId, startOfDate,
//				lastDayOfMonth);
//		ravineInfos.forEach(o -> priceMap.put(o.getType(), o.getPrice()));
//		Map<String, List<LocalTime>> ravineMap = generateRavineTimeMap(ravineInfos, 15);

		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals(
						energyKey)).collect(Collectors.toList());
		Map<String, Map<Date, Double>> resMap = new HashMap<>();
		IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
		mappings.forEach(o -> {
			Map<Date, Double> energyData = iotMappingStrategy.getValues(o, TimeUtil.getMonthStart(date), date);
			resMap.put(o.getPointModel().getKey(), energyData);
		});
		Map<String, NodeProfit> nodeProfitMap = new HashMap<>();
		List<String> errorDate = new ArrayList<>();
		List<Date> countDateTimeList = resMap.get(energyKey).keySet().stream().sorted().collect(Collectors.toList());
		for (Date countDateTime : countDateTimeList) {
			String countDate = TimeUtil.toYmdStr(countDateTime);
			String eid = nodeId + TimeUtil.toYmdStr(countDateTime);
			NodeProfit enodeProfit = nodeProfitMap.getOrDefault(countDate, new NodeProfit(eid, nodeId, countDateTime));
			LocalTime time = LocalTime.parse(TimeUtil.toHHmmStr(countDateTime));
			String property;
//			if (ravineMap.get(countDate) != null && ravineMap.get(countDate).contains(time)) {
//				property = "深谷";
//			} else {
			property = propertyMap.get(time);
//			}
			BigDecimal price = priceMap.get(property);
			for (Map.Entry<String, Map<Date, Double>> entry : resMap.entrySet()) {
				Double value = entry.getValue().get(countDateTime);
				if (value == null) {
					errorDate.add(countDate);
					continue;
				}
				if ("尖".equals(property)) {
					enodeProfit.setPriceHigh(price.abs().doubleValue());
					enodeProfit.setOutElectricityHigh(BigDecimal.valueOf(enodeProfit.getOutElectricityHigh()).add(BigDecimal.valueOf(value)).doubleValue());
				}
				if ("峰".equals(property)) {
					enodeProfit.setPricePeak(price.abs().doubleValue());
					enodeProfit.setOutElectricityPeak(BigDecimal.valueOf(enodeProfit.getOutElectricityPeak()).add(BigDecimal.valueOf(value)).doubleValue());
				}

				if ("平".equals(property)) {
					enodeProfit.setPriceStable(price.abs().doubleValue());
					enodeProfit.setOutElectricityStable(BigDecimal.valueOf(enodeProfit.getOutElectricityStable()).add(BigDecimal.valueOf(value)).doubleValue());
				}

				if ("谷".equals(property)) {
					enodeProfit.setPriceLow(price.abs().doubleValue());
					enodeProfit.setOutElectricityLow(BigDecimal.valueOf(enodeProfit.getOutElectricityLow()).add(BigDecimal.valueOf(value)).doubleValue());
				}

//				if ("深谷".equals(property)) {
//					enodeProfit.setPriceRavine(price.abs().doubleValue());
//					enodeProfit.setOutElectricityRavine(BigDecimal.valueOf(enodeProfit.getOutElectricityRavine()).add(BigDecimal.valueOf(value)).doubleValue());
//				}
				enodeProfit.setProfitValue(BigDecimal.valueOf(enodeProfit.getProfitValue()).add(BigDecimal.valueOf(value).multiply(price)).doubleValue());
			}
			nodeProfitMap.put(countDate, enodeProfit);
		}

		errorDate = errorDate.stream().distinct().collect(Collectors.toList());
		for (String s : errorDate) {
			nodeProfitMap.remove(s);
			logger.error("光伏收益计算错误,node:{},date:{}", nodeId, s);
		}
	}

	/**
	 * @param nodeId      节点id
	 * @param priceMap    kv形如 尖-0.99 形式的 属性-电价表
	 * @param propertyMap 形如 11:00-峰 形式的 时间-电费属性表
	 * @param date        计算月初到该日期
	 */
	public void doCalculateStorageEnergyProfit(String nodeId, Map<String, BigDecimal> priceMap, Map<LocalTime, String> propertyMap, Date date) {
		String forwardEnergyKey = "forward_active_energy";
		String backwardEnergyKey = "backward_active_energy";

//		ElectricityHolidayRepository electricityHolidayRepository = SpringBeanHelper.getBeanOrThrow(ElectricityHolidayRepository.class);
//		LocalDate today = date.toInstant().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
//		LocalDate startOfDate = today.withDayOfMonth(1);
//		LocalDate lastDayOfMonth = today.with(TemporalAdjusters.lastDayOfMonth());
//		List<ElectricityHolidayInfo> ravineInfos = electricityHolidayRepository.findAllByPk_NodeIdAndPk_DateBetween(nodeId, startOfDate,
//				lastDayOfMonth);
//		ravineInfos.forEach(o -> priceMap.put(o.getType(), o.getPrice()));
//		Map<String, List<LocalTime>> ravineMap = generateRavineTimeMap(ravineInfos, 15);

		PointModelMappingRepository pointModelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);
		List<PointModelMapping> mappings =
				pointModelMappingRepository.findAllByStation_StationId(nodeId).stream().filter(o -> o.getPointModel().getKey().equals(
						forwardEnergyKey) || o.getPointModel().getKey().equals(backwardEnergyKey)).collect(Collectors.toList());
		Map<String, Map<Date, Double>> resMap = new HashMap<>();
		IOTMappingStrategy iotMappingStrategy = SpringBeanHelper.getBeanOrThrow(IOTMappingStrategy.class);
		mappings.forEach(o -> {
			Map<Date, Double> energyData = iotMappingStrategy.getValues(o, TimeUtil.getMonthStart(date), date);
			resMap.put(o.getPointModel().getKey(), energyData);
		});
		Map<String, NodeProfit> nodeProfitMap = new HashMap<>();
		List<String> errorDate = new ArrayList<>();
		List<Date> countDateTimeList = resMap.get(forwardEnergyKey).keySet().stream().sorted().collect(Collectors.toList());
		for (Date countDateTime : countDateTimeList) {
			String countDate = TimeUtil.toYmdStr(countDateTime);
			String eid = nodeId + TimeUtil.toYmdStr(countDateTime);
			NodeProfit enodeProfit = nodeProfitMap.getOrDefault(countDate, new NodeProfit(eid, nodeId, countDateTime));
			LocalTime time = LocalTime.parse(TimeUtil.toHHmmStr(countDateTime));
			String property;
//			if (ravineMap.get(countDate) != null && ravineMap.get(countDate).contains(time)) {
//				property = "深谷";
//			} else {
			property = propertyMap.get(time);
//			}
			BigDecimal price = priceMap.get(property);

			for (Map.Entry<String, Map<Date, Double>> entry : resMap.entrySet()) {
				Double value = entry.getValue().get(countDateTime);
				if (value == null) {
					errorDate.add(countDate);
					continue;
				}
				if (entry.getKey().equals(forwardEnergyKey)) {
					if ("尖".equals(property)) {
						enodeProfit.setPriceHigh(price.abs().doubleValue());
						enodeProfit.setInElectricityHigh(BigDecimal.valueOf(enodeProfit.getInElectricityHigh()).add(BigDecimal.valueOf(value)).doubleValue());

					}
					if ("峰".equals(property)) {
						enodeProfit.setPricePeak(price.abs().doubleValue());
						enodeProfit.setInElectricityPeak(BigDecimal.valueOf(enodeProfit.getInElectricityPeak()).add(BigDecimal.valueOf(value)).doubleValue());

					}

					if ("平".equals(property)) {
						enodeProfit.setPriceStable(price.abs().doubleValue());
						enodeProfit.setInElectricityStable(BigDecimal.valueOf(enodeProfit.getInElectricityStable()).add(BigDecimal.valueOf(value)).doubleValue());

					}

					if ("谷".equals(property)) {
						enodeProfit.setPriceLow(price.abs().doubleValue());
						enodeProfit.setInElectricityLow(BigDecimal.valueOf(enodeProfit.getInElectricityLow()).add(BigDecimal.valueOf(value)).doubleValue());

					}

//					if ("深谷".equals(property)) {
//						enodeProfit.setPriceRavine(price.abs().doubleValue());
//						enodeProfit.setInElectricityRavine(BigDecimal.valueOf(enodeProfit.getInElectricityRavine()).add(BigDecimal.valueOf(value)).doubleValue());
//					}

					enodeProfit.setProfitValue(BigDecimal.valueOf(enodeProfit.getProfitValue()).add(BigDecimal.valueOf(value).multiply(price.negate())).doubleValue());
				}

				if (entry.getKey().equals(backwardEnergyKey)) {
					if ("尖".equals(property)) {
						enodeProfit.setPriceHigh(price.abs().doubleValue());
						enodeProfit.setOutElectricityHigh(BigDecimal.valueOf(enodeProfit.getOutElectricityHigh()).add(BigDecimal.valueOf(value)).doubleValue());

					}
					if ("峰".equals(property)) {
						enodeProfit.setPricePeak(price.abs().doubleValue());
						enodeProfit.setOutElectricityPeak(BigDecimal.valueOf(enodeProfit.getOutElectricityPeak()).add(BigDecimal.valueOf(value)).doubleValue());

					}

					if ("平".equals(property)) {
						enodeProfit.setPriceStable(price.abs().doubleValue());
						enodeProfit.setOutElectricityStable(BigDecimal.valueOf(enodeProfit.getOutElectricityStable()).add(BigDecimal.valueOf(value)).doubleValue());

					}

					if ("谷".equals(property)) {
						enodeProfit.setPriceLow(price.abs().doubleValue());
						enodeProfit.setOutElectricityLow(BigDecimal.valueOf(enodeProfit.getOutElectricityLow()).add(BigDecimal.valueOf(value)).doubleValue());

					}

//					if ("深谷".equals(property)) {
//						enodeProfit.setPriceRavine(price.abs().doubleValue());
//						enodeProfit.setOutElectricityRavine(BigDecimal.valueOf(enodeProfit.getOutElectricityRavine()).add(BigDecimal.valueOf(value)).doubleValue());
//					}

					enodeProfit.setProfitValue(BigDecimal.valueOf(enodeProfit.getProfitValue()).add(BigDecimal.valueOf(value).multiply(price)).doubleValue());

				}
			}
			nodeProfitMap.put(countDate, enodeProfit);
		}

		errorDate = errorDate.stream().distinct().collect(Collectors.toList());
		for (String s : errorDate) {
			nodeProfitMap.remove(s);
			logger.error("储能收益计算错误,node:{},date:{}", nodeId, s);
		}
		nodeProfitRepository.saveAll(nodeProfitMap.values());
		logger.info("储能收益计算结束，node:{}", nodeId);

	}

	@Override
	public void doCalculateChargingPileProfit(String nodeId, Map<String, ElectricityInfoModel> timeInfo, LocalDateTime nowTime,
	                                          LocalDate firstDayOfMonth) {
		Date startDate = Date.from(firstDayOfMonth.atStartOfDay(ZoneId.systemDefault()).toInstant());
		List<IotTsKvMeteringDevice96> allByNodeId = iotTsKvMeteringDevice96Repository.findAllByNodeIdAndPointDescAndCountDateGreaterThanEqual(nodeId
				, POINTDESC, startDate);
		if (allByNodeId.isEmpty()) {
			return;
		}
		Map<String, List<IotTsKvMeteringDevice96>> collect = allByNodeId.stream().collect(Collectors.groupingBy((px) -> {
			String time = TimeUtil.toYmdStr(px.getCountDate());
			return px.getNodeId() + time;
		}));
		List<NodeProfit> nodeProfitList = new ArrayList<>();
		for (Map.Entry<String, List<IotTsKvMeteringDevice96>> entry : collect.entrySet()) {
			List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96s = entry.getValue();
			if (iotTsKvMeteringDevice96s != null && !iotTsKvMeteringDevice96s.isEmpty()) {
				NodeProfit enodeProfit = new NodeProfit();
				Date countDate = iotTsKvMeteringDevice96s.get(0).getCountDate();
				enodeProfit.setProfitId(entry.getKey());
				enodeProfit.setNodeId(nodeId);
				enodeProfit.setProfitDate(countDate);
				enodeProfit.setProfitValue(BigDecimal.ZERO.doubleValue());
				iotTsKvMeteringDevice96s.forEach(l -> {
					ElectricityInfoModel electricityInfoModel = timeInfo.get(l.getTimeScope());
					String pp = electricityInfoModel.getProperty();
					BigDecimal bigDecimal = electricityInfoModel.getPrice();
					Double use = l.getHTotalUse();

					if (l.getHTotalUse() != null) {
						if ("尖".equals(pp)) {
							enodeProfit.setPriceHigh(bigDecimal.abs().doubleValue());
							enodeProfit.setOutElectricityHigh(BigDecimal.valueOf(enodeProfit.getOutElectricityHigh()).add(BigDecimal.valueOf(use)).doubleValue());

						}
						if ("峰".equals(pp)) {
							enodeProfit.setPricePeak(bigDecimal.abs().doubleValue());
							enodeProfit.setOutElectricityPeak(BigDecimal.valueOf(enodeProfit.getOutElectricityPeak()).add(BigDecimal.valueOf(use)).doubleValue());

						}

						if ("平".equals(pp)) {
							enodeProfit.setPriceStable(bigDecimal.abs().doubleValue());
							enodeProfit.setOutElectricityStable(BigDecimal.valueOf(enodeProfit.getOutElectricityStable()).add(BigDecimal.valueOf(use)).doubleValue());

						}

						if ("谷".equals(pp)) {
							enodeProfit.setPriceLow(bigDecimal.abs().doubleValue());
							enodeProfit.setOutElectricityLow(BigDecimal.valueOf(enodeProfit.getOutElectricityLow()).add(BigDecimal.valueOf(use)).doubleValue());

						}

						enodeProfit.setProfitValue(BigDecimal.valueOf(enodeProfit.getProfitValue()).add(BigDecimal.valueOf(use).multiply(bigDecimal)).doubleValue());

					}

				});
				nodeProfitList.add(enodeProfit);
			}

		}
//        logger.info("充电桩:{},收益存储：{}",nodeId,JSONObject.toJSON(nodeProfitList));

		nodeProfitRepository.saveAll(nodeProfitList);
	}

	public static Map<String, List<LocalTime>> generateRavineTimeMap(List<ElectricityHolidayInfo> list, int intervalMinutes) {
		Duration interval = Duration.ofMinutes(intervalMinutes);
		Map<String, List<LocalTime>> result = new HashMap<>();
		for (ElectricityHolidayInfo electricityHolidayInfo : list) {
			List<LocalTime> localTimes = new ArrayList<>();
			LocalTime curr = electricityHolidayInfo.getPk().getSt();
			while (curr != electricityHolidayInfo.getPk().getEt()) {
				localTimes.add(curr);
				curr = curr.plus(interval);
			}
			String date = TimeUtil.toYmdStr(Date.from(electricityHolidayInfo.getPk().getDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
			result.put(date, localTimes);
		}
		return result;
	}
}
