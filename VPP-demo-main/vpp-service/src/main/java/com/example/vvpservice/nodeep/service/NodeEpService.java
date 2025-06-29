package com.example.vvpservice.nodeep.service;
import java.util.regex.Pattern;
import java.util.Date;
import java.time.Instant;
import java.time.LocalTime;
import java.time.LocalDate;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import java.time.ZoneId;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.nodeep.model.*;
import com.example.vvpservice.nodeprofit.service.INodeProfitService;
import com.example.vvpservice.revenue.model.CopyNodeEpRequest;
import com.example.vvpservice.revenue.model.EleNodeInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
@Slf4j
public class NodeEpService {


	@Value("${xiaoda.url}")
	private String url;

	public void updateEpBatch(UpdateNodeEPBatchRequest request) {
		String nodeId = request.getNodeId();
		LocalDate now = LocalDate.now();

		Map<String, BigDecimal> priceMap = request.getPrices().stream().collect(Collectors.toMap(UpdateNodeEPBatchRequest.price::getType,
				UpdateNodeEPBatchRequest.price::getPrice));
		if (now.getYear() <= request.getDate().getYear() && now.getMonthValue() <= request.getDate().getMonthValue()) {
			ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
			LocalDateTime effectiveDate = request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId, effectiveDate);
			if (prices.isEmpty()) {
				generateEPrice(request, priceMap, now, prices);
			}
			prices.forEach(price -> price.setPrice(priceMap.get(price.getProperty())));
			electricityPriceRepository.saveAll(prices);
			Map<LocalTime, String> propertyMap = new HashMap<>();
			for (ElectricityPrice price : prices) {
				propertyMap.put(LocalTime.parse(price.getSTime()), price.getProperty());
			}
			Date etProfit = Date.from(request.getDate().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
			reCalculateProfit(nodeId, etProfit, priceMap, propertyMap);
			overWriteOldTablePrice(request.getNodeId(), Date.from(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant()),
					priceMap);
		} else {
			Date st = Date.from(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date et = Date.from(request.getDate().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());

			StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
			StationNode stationNode = stationNodeRepository.findByStationId(request.getNodeId());
			NodeProfitRepository nodeProfitRepository = SpringBeanHelper.getBeanOrThrow(NodeProfitRepository.class);
			if (stationNode.getStationCategory().contains("项目")) {
				ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
				List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId,
						request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
				if (prices.isEmpty()) {
					generateEPrice(request, priceMap, now, prices);
				}
				prices.forEach(price -> price.setPrice(priceMap.get(price.getProperty())));
				electricityPriceRepository.saveAll(prices);
				List<String> pileList = stationNodeRepository.allStationIdByParentIdAndStationType(request.getNodeId(), "充电桩项目");
				List<NodeProfit> profits = nodeProfitRepository.findByNodeIdInAndProfitDateBetween(pileList, st, et);
				profits.forEach(nodeProfit -> {
					nodeProfit.setPriceHigh(priceMap.get("尖").doubleValue());
					nodeProfit.setPricePeak(priceMap.get("峰").doubleValue());
					nodeProfit.setPriceStable(priceMap.get("平").doubleValue());
					nodeProfit.setPriceLow(priceMap.get("谷").doubleValue());
//					nodeProfit.setPriceRavine(priceMap.getOrDefault("深谷",BigDecimal.ZERO).doubleValue());
				});
				nodeProfitRepository.saveAll(profits);
				return;
			}
			overWriteOldTablePrice(request.getNodeId(), st, priceMap);
			List<NodeProfit> profits = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId, st, et);
			profits.forEach(nodeProfit -> {
				nodeProfit.setPriceHigh(priceMap.get("尖").doubleValue());
				nodeProfit.setPricePeak(priceMap.get("峰").doubleValue());
				nodeProfit.setPriceStable(priceMap.get("平").doubleValue());
				nodeProfit.setPriceLow(priceMap.get("谷").doubleValue());
//				nodeProfit.setPriceRavine(priceMap.getOrDefault("深谷",BigDecimal.ZERO).doubleValue());
			});
			nodeProfitRepository.saveAll(profits);
			Date etProfit = Date.from(request.getDate().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
			Map<LocalTime, String> propertyMap = new HashMap<>();

			if (stationNode.getStationType().equals("储能电站")) {
				CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository =
						SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyRepository.class);
				List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategies =
						cfgStorageEnergyStrategyRepository.findAllByNodeIdAndEffectiveDate(stationNode.getStationId(), st);
				for (CfgStorageEnergyStrategy price : cfgStorageEnergyStrategies) {
					priceMap.put(price.getProperty(), price.getPriceHour());
					LocalTime sTime = LocalTime.parse(price.getSTime());
					for (int i = 0; i < 4; i++) {
						propertyMap.put(sTime, price.getProperty());
						sTime = sTime.plusMinutes(15);
					}
				}
			}
			if (stationNode.getStationType().equals("光伏电站")) {
				CfgPhotovoltaicTouPriceRepository cfgPhotovoltaicTouPriceRepository =
						SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicTouPriceRepository.class);
				List<CfgPhotovoltaicTouPrice> cfgPhotovoltaicTouPrices =
						cfgPhotovoltaicTouPriceRepository.findAllByNodeIdAndEffectiveDate(stationNode.getStationId(), st);
				for (CfgPhotovoltaicTouPrice price : cfgPhotovoltaicTouPrices) {
					priceMap.put(price.getProperty(), price.getPriceHour());
					LocalTime sTime = LocalTime.parse(price.getSTime());
					for (int i = 0; i < 4; i++) {
						propertyMap.put(sTime, price.getProperty());
						sTime = sTime.plusMinutes(15);
					}
				}
			}
			if (!priceMap.isEmpty() && !propertyMap.isEmpty()) {
				reCalculateProfit(nodeId, etProfit, priceMap, propertyMap);
			}
		}
	}

	private void overWriteOldTablePrice(String nodeId, Date date, Map<String, BigDecimal> priceMap) {
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		StationNode node = stationNodeRepository.findByStationId(nodeId);
		switch (node.getStationType()) {
			case "储能电站":
				CfgStorageEnergyStrategyRepository esRepository = SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyRepository.class);
				List<CfgStorageEnergyStrategy> espriceList = esRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao",
						date);
				espriceList.forEach(o -> {
					if (priceMap.get(o.getProperty()) != null) {
						o.setPriceHour(priceMap.get(o.getProperty()));
					}
				});
				esRepository.saveAll(espriceList);
				break;
			case "光伏电站":
				CfgPhotovoltaicTouPriceRepository pvRepository = SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicTouPriceRepository.class);
				List<CfgPhotovoltaicTouPrice> pvpriceList = pvRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao",
						date);
				pvpriceList.forEach(o -> {
					if (priceMap.get(o.getProperty()) != null) {
						o.setPriceHour(priceMap.get(o.getProperty()));
					}
				});
				pvRepository.saveAll(pvpriceList);
				break;
			default:
				break;
		}
	}

	private void overWriteOldTableTime(String nodeId, Date date, Map<String, BigDecimal> priceMap, Map<String, String> timeMap) {
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		StationNode node = stationNodeRepository.findByStationId(nodeId);
		switch (node.getStationType()) {
			case "储能电站":
				CfgStorageEnergyStrategyRepository esRepository = SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyRepository.class);
				List<CfgStorageEnergyStrategy> espriceList = esRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao",
						date);
				espriceList.forEach(o -> {
					DateTimeFormatter InputFormatter = DateTimeFormatter.ofPattern("HH:mm");
					DateTimeFormatter OutputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
					LocalTime sTime = LocalTime.parse(o.getSTime(), InputFormatter);
					LocalTime eTime = LocalTime.parse(o.getETime(), InputFormatter);
					String st = sTime.format(OutputFormatter);
					String et = eTime.format(OutputFormatter);
					if (Duration.between(sTime, eTime).toHours() == 1 || Duration.between(sTime, eTime).toMinutes() == 59) {
						List<String> allKeys = generateQuarterHourKeys(sTime, eTime);
						if (allKeys.stream().allMatch(timeMap::containsKey)) {
							String property = timeMap.get(allKeys.get(0));
							o.setProperty(property);
							o.setPriceHour(priceMap.get(property));
						}
					} else {
						String timeFrame = st + "-" + et;
						if (timeMap.get(timeFrame) != null) {
							String property = timeMap.get(timeFrame);
							o.setProperty(property);
							o.setPriceHour(priceMap.get(property));
						}
					}
				});
				esRepository.saveAll(espriceList);
				break;
			case "光伏电站":
				CfgPhotovoltaicTouPriceRepository pvRepository = SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicTouPriceRepository.class);
				List<CfgPhotovoltaicTouPrice> pvpriceList = pvRepository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao",
						date);
				if (pvpriceList.isEmpty()) {

				}
				pvpriceList.forEach(o -> {
					DateTimeFormatter InputFormatter = DateTimeFormatter.ofPattern("HH:mm");
					DateTimeFormatter OutputFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
					LocalTime sTime = LocalTime.parse(o.getSTime(), InputFormatter);
					LocalTime eTime = LocalTime.parse(o.getETime(), InputFormatter);
					String st = sTime.format(OutputFormatter);
					String et = eTime.format(OutputFormatter);
					if (Duration.between(sTime, eTime).toHours() == 1 || Duration.between(sTime, eTime).toMinutes() == 59) {
						List<String> allKeys = generateQuarterHourKeys(sTime, eTime);
						if (allKeys.stream().allMatch(timeMap::containsKey)) {
							String property = timeMap.get(allKeys.get(0));
							o.setProperty(property);
							o.setPriceHour(priceMap.get(property));
						}
					} else {
						String timeFrame = st + "-" + et;
						if (timeMap.get(timeFrame) != null) {
							String property = timeMap.get(timeFrame);
							o.setProperty(property);
							o.setPriceHour(priceMap.get(property));
						}
					}
				});
				pvRepository.saveAll(pvpriceList);
				break;
			default:
				break;
		}
	}

	private static List<String> generateQuarterHourKeys(LocalTime hourStart, LocalTime hourEnd) {
		DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("HH:mm");
		return hourStart.isBefore(hourEnd)
				? Arrays.asList(
				hourStart.format(Formatter) + "-" + hourStart.plusMinutes(15).format(Formatter),
				hourStart.plusMinutes(15).format(Formatter) + "-" + hourStart.plusMinutes(30).format(Formatter),
				hourStart.plusMinutes(30).format(Formatter) + "-" + hourStart.plusMinutes(45).format(Formatter),
				hourStart.plusMinutes(45).format(Formatter) + "-" + hourStart.plusMinutes(60).format(Formatter)
		) : new ArrayList<>();
	}

	private void generateEPrice(UpdateNodeEPBatchRequest request, Map<String, BigDecimal> priceMap, LocalDate now, List<ElectricityPrice> prices) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		StationNode node = stationNodeRepository.findByStationId(request.getNodeId());
		List<ElectricityTimeSlotResponse> timeSlots;
		try {
			String city = StringUtils.isEmpty(request.getCity()) ? node.getProvince() : request.getCity();
			String type = StringUtils.isEmpty(request.getType1()) ? node.getEType().split("-")[0] : request.getType1();
			String vol = StringUtils.isEmpty(request.getVol1()) ? node.getVoltage() : request.getVol1();
			String company = node.getElectricityCompany();

			timeSlots = getElectricityTimeSlots(city, type, vol, request.getDate().getMonthValue(), request.getDate().getYear(), company);
		} catch (Exception e) {
			throw new RuntimeException("查询分时信息失败,请确认项目用电制度与电压等级填写正确！");
		}
		if (timeSlots.isEmpty()) {
			throw new RuntimeException("查询分时信息失败,请确认项目用电制度与电压等级填写正确！");
		}
		timeSlots.forEach(o -> {
			LocalTime ts = o.getStartTime();
			Duration interval = Duration.ofMinutes(15);
			int n = 0;
			// 考虑如 22:00~00:00的情况，用=判断
			for (; !ts.equals(o.getEndTime()) && n < 100; ts = ts.plus(interval), n++) {
				ElectricityPrice price = new ElectricityPrice();
				price.setNodeId(request.getNodeId());
				price.setEffectiveDate(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
				price.setSTime(formatter.format(ts));
				price.setETime(formatter.format(ts.plus(interval)));
				price.setTimeFrame(price.getSTime() + "-" + price.getETime());
				switch (o.getCategory()) {
					case "sharp":
						price.setProperty("尖");
						break;
					case "peek":
						price.setProperty("峰");
						break;
					case "shoulder":
						price.setProperty("平");
						break;
					case "off_peek":
						price.setProperty("谷");
						break;
//					case "ravine":
//						price.setProperty("深谷");
//						break;
					default:
						price.setProperty(o.getCategory());
				}
				price.setPrice(priceMap.get(price.getProperty()));
				price.setCreatedTime(now.atStartOfDay());
				price.setUpdateTime(now.atStartOfDay());
				price.setDateType("0");
				if (node.getStationType().equals("储能电站")) {
					price.setPriceUse("1");
				} else if (node.getStationType().equals("光伏电站")) {
					price.setPriceUse("2");
				} else {
					price.setPriceUse("0");
				}

				price.setId(request.getNodeId() + "_" + request.getDate().getYear() + "-" + request.getDate().getMonthValue() + "_" + price.getTimeFrame() + "_" + price.getDateType() + "_" + price.getPriceUse());
				prices.add(price);
			}

		});
	}

	public void generateEPrice(GenerateEPriceRequest request) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
		StationNode node = stationNodeRepository.findByStationId(request.getNodeId());
		ElectricityPriceResponse electricityPriceResponse;
		try {
			String city = StringUtils.isEmpty(request.getCity()) ? node.getProvince() : request.getCity();
			String type = StringUtils.isEmpty(request.getType()) ? node.getEType().replace('-', '_') : request.getType().replace('-', '_');
			String vol = StringUtils.isEmpty(request.getVoltage()) ? node.getVoltage() : request.getVoltage();
			String company = node.getElectricityCompany();

			electricityPriceResponse = getElectricityPrice(city, type, vol, request.getDate().getYear(), request.getDate().getMonthValue(), company);
		} catch (Exception e) {
			throw new RuntimeException("查询分时信息失败,请确认项目用电制度与电压等级填写正确！");
		}
		if (electricityPriceResponse == null || electricityPriceResponse.getPriceList().isEmpty()) {
			throw new RuntimeException("查询分时信息失败,请确认项目用电制度与电压等级填写正确！");
		}
		List<ElectricityPrice> prices = new ArrayList<>();
		electricityPriceResponse.getPriceList().forEach(o -> {
			String[] times = o.getStartDate().split("~");
			LocalTime ts = LocalTime.parse(times[0], formatter);
			LocalTime endTime = LocalTime.parse(times[1], formatter);
			Duration interval = Duration.ofMinutes(15);
			int n = 0;
			// 考虑如 22:00~00:00的情况，用=判断
			for (; !ts.equals(endTime) && n < 100; ts = ts.plus(interval), n++) {
				ElectricityPrice price = new ElectricityPrice();
				price.setNodeId(request.getNodeId());
				price.setEffectiveDate(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
				price.setSTime(formatter.format(ts));
				price.setETime(formatter.format(ts.plus(interval)));
				price.setTimeFrame(price.getSTime() + "-" + price.getETime());
				price.setProperty(o.getTimeCategory());
				price.setPrice(o.getPrice());
				price.setCreatedTime(LocalDate.now().atStartOfDay());
				price.setUpdateTime(LocalDate.now().atStartOfDay());
				price.setDateType("0");
				if (node.getStationType().equals("储能电站")) {
					price.setPriceUse("1");
				} else if (node.getStationType().equals("光伏电站")) {
					price.setPriceUse("2");
				} else {
					price.setPriceUse("0");
				}

				price.setId(request.getNodeId() + "_" + request.getDate().getYear() + "-" + request.getDate().getMonthValue() + "_" + price.getTimeFrame() + "_" + price.getDateType() + "_" + price.getPriceUse());
				prices.add(price);
			}

		});
		electricityPriceRepository.deleteAllByNodeIdAndEffectiveDate(request.getNodeId(),
				request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
		electricityPriceRepository.saveAll(prices);
//		setHolidays(request.getDate().getYear(), request.getDate().getMonthValue(), request.getNodeId());
	}

	public List<ElectricityTimeSlotResponse> getElectricityTimeSlots(String city, String type1, String vol1, int month, int year, String company) {
		String queryETimeInfoUrl = url + "/charge_info/queryTimeSlots";
		queryETimeInfoUrl += "?";
		queryETimeInfoUrl = queryETimeInfoUrl + "city=" + city + "&type1=" + type1 + "&voltage1=" + vol1 + "&month=" + month + "&year=" + year;
		if (StringUtils.isNotEmpty(company)) {
			queryETimeInfoUrl = queryETimeInfoUrl + "&company=" + company;
		}
		JSONObject object = JSON.parseObject(HttpUtil.okHttpGet(queryETimeInfoUrl));
		return JSON.parseArray(JSON.toJSONString(object.get("data")), ElectricityTimeSlotResponse.class);
	}

	public ElectricityPriceResponse getElectricityPrice(String city, String type, String voltage, int year, int month, String company) {
		String queryEPriceInfoUrl = url + "/charge_info/query";
		queryEPriceInfoUrl += "?";
		queryEPriceInfoUrl = queryEPriceInfoUrl + "city=" + city + "&type=" + type + "&voltage=" + voltage + "&month=" + month + "&year=" + year;
		if (StringUtils.isNotEmpty(company)) {
			queryEPriceInfoUrl = queryEPriceInfoUrl + "&company=" + company;
		}
		JSONObject object = JSON.parseObject(HttpUtil.okHttpGet(queryEPriceInfoUrl));
		return JSON.parseObject(JSON.toJSONString(object.get("data")), ElectricityPriceResponse.class);
	}

	public ElectricityHolidayResponse getElectricityHolidayInfo(String city, String type1, String type2, String voltage, String company, int year,
	                                                            int month) {
		String queryHolidayUrl = url + "/charge_info/queryHoliday";
		queryHolidayUrl += "?";
		queryHolidayUrl = queryHolidayUrl + "city=" + city + "&type1=" + type1 + "&type2=" + type2;
		if (StringUtils.isNotEmpty(voltage)) {
			queryHolidayUrl = queryHolidayUrl + "&voltage1=" + voltage;
		}
		if (StringUtils.isNotEmpty(company)) {
			queryHolidayUrl = queryHolidayUrl + "&company=" + company;
		}
		queryHolidayUrl = queryHolidayUrl + "&year=" + year + "&month=" + month;
		JSONObject object = JSON.parseObject(HttpUtil.okHttpGet(queryHolidayUrl));
		if (object == null){
			return new ElectricityHolidayResponse();
		}
		return JSON.parseObject(JSON.toJSONString(object.get("data")), ElectricityHolidayResponse.class);
	}

	private void reCalculateProfit(String nodeId, Date et, Map<String, BigDecimal> newPriceMap, Map<LocalTime, String> propertyMap) {
		INodeProfitService nodeProfitService = SpringBeanHelper.getBeanOrThrow(INodeProfitService.class);
		if (nodeProfitService.getPvNodeIdList().contains(nodeId)) {
			nodeProfitService.doCalculatePvProfit(nodeId, newPriceMap, propertyMap, et);
		}
		if (nodeProfitService.getStoreEnergyNodeIdList().contains(nodeId)) {
			nodeProfitService.doCalculateStorageEnergyProfit(nodeId, newPriceMap, propertyMap, et);
		}
		reCalculateForecastProfit(nodeId, et);
	}

	private void reCalculateForecastProfit(String nodeId, Date ts) {
		NodeProfitRepository nodeProfitRepository = SpringBeanHelper.getBeanOrThrow(NodeProfitRepository.class);
		NodeProfitDayForecastingRepository nodeProfitDayForecastingRepository =
				SpringBeanHelper.getBeanOrThrow(NodeProfitDayForecastingRepository.class);
		List<NodeProfit> nodeProfits = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId,
				TimeUtil.getMonthStart(TimeUtil.getPreMonth(ts, 0)), ts);

		List<NodeProfitDayForecasting> nodeProfitListd = new ArrayList<>();


		nodeProfits.forEach(el -> {
			NodeProfitDayForecasting npd =
					nodeProfitDayForecastingRepository.findById(el.getNodeId() + TimeUtil.toYmdStr(el.getProfitDate())).orElse(new NodeProfitDayForecasting());
			npd.setProfitId(el.getNodeId() + TimeUtil.toYmdStr(el.getProfitDate()));
			npd.setNodeId(el.getNodeId());
			npd.setProfitDateDay(el.getProfitDate());
			npd.setProfitValue(el.getProfitValue());

			nodeProfitListd.add(npd);
		});

		nodeProfitDayForecastingRepository.saveAll(nodeProfitListd);

		NodeProfitMonthForecastingRepository nodeProfitMonthForecastingRepository =
				SpringBeanHelper.getBeanOrThrow(NodeProfitMonthForecastingRepository.class);
		List<NodeProfit> monthProfit = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId,
				TimeUtil.getMonthStart(TimeUtil.getPreMonth(ts, 0)), ts);
		Map<String, List<NodeProfit>> collect =
				monthProfit.stream().collect(groupingBy(el -> el.getNodeId() + TimeUtil.toYmStr(el.getProfitDate())));

		List<NodeProfitMonthForecasting> nodeProfitListMf = new ArrayList<>();

		collect.keySet().forEach(e -> {
			NodeProfitMonthForecasting npmf = new NodeProfitMonthForecasting();
			npmf.setProfitId(e);
			npmf.setNodeId(e.substring(0, 32));
			npmf.setProfitDateMonth(TimeUtil.strYmFormat(e.substring(32)));
			npmf.setProfitValue(collect.get(e).stream().mapToDouble(NodeProfit::getProfitValue).sum());

			nodeProfitListMf.add(npmf);
		});

		nodeProfitMonthForecastingRepository.saveAll(nodeProfitListMf);
	}

	public QueryNodeEPriceResult queryEPrice(QueryNodeEPriceRequest request) {
		QueryNodeEPriceResult result = new QueryNodeEPriceResult();

		String nodeId = request.getNodeId();
		result.setNodeId(nodeId);
		result.setDate(request.getDate());
		LocalDate now = LocalDate.now();

		if (now.getYear() <= request.getDate().getYear() && now.getMonthValue() <= request.getDate().getMonthValue()) {
			ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
			LocalDateTime effectiveDate = request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId, effectiveDate);
			if (prices.isEmpty()) {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
				StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
				StationNode node = stationNodeRepository.findByStationId(request.getNodeId());
				String city =  node.getProvince();
				String type = node.getEType().split("-")[0] ;
				String vol = node.getVoltage();
				String company = node.getElectricityCompany();

				List<ElectricityTimeSlotResponse> timeSlots = getElectricityTimeSlots(city, type, vol, request.getDate().getMonthValue(),
						request.getDate().getYear(), company);
				if (timeSlots == null || timeSlots.isEmpty()) {
					throw new RuntimeException("找不到时段数据");
				}
				Map<String,String> timeSlotMap = new HashMap<>();
				timeSlots.forEach(o -> {
					LocalTime ts = o.getStartTime();
					Duration interval = Duration.ofMinutes(15);
					int n = 0;
					// 考虑如 22:00~00:00的情况，用=判断
					for (; !ts.equals(o.getEndTime()) && n < 100; ts = ts.plus(interval), n++) {
						String timeFrame= (formatter.format(ts) + "-" + formatter.format(ts.plus(interval)));
						switch (o.getCategory()) {
							case "sharp":
								timeSlotMap.put(timeFrame,"尖");
								break;
							case "peek":
								timeSlotMap.put(timeFrame,"峰");
								break;
							case "shoulder":
								timeSlotMap.put(timeFrame,"平");
								break;
							case "off_peek":
								timeSlotMap.put(timeFrame,"谷");
								break;
							default:
								timeSlotMap.put(timeFrame,o.getCategory());
						}

					}
				});
				result.setTimeSlots(timeSlotMap);
				return result;
			}
			prices.forEach(o -> {
				if (o.getProperty().equals("尖")) {
					result.setPriceSharp(o.getPrice());
				}
				if (o.getProperty().equals("峰")) {
					result.setPricePeak(o.getPrice());
				}
				if (o.getProperty().equals("平")) {
					result.setPriceShoulder(o.getPrice());
				}
				if (o.getProperty().equals("谷")) {
					result.setPriceOffPeak(o.getPrice());
				}
//				if (o.getProperty().equals("深谷")) {
//					result.setPriceRavine(o.getPrice());
//				}
			});
			Map<String, String> timeSlotMap = prices.stream().collect(Collectors.toMap(ElectricityPrice::getTimeFrame,
					ElectricityPrice::getProperty));
			result.setTimeSlots(timeSlotMap);
		} else {
			NodeProfitRepository nodeProfitRepository = SpringBeanHelper.getBeanOrThrow(NodeProfitRepository.class);
			ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
			Date st = Date.from(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
			Date et = Date.from(request.getDate().atEndOfMonth().atStartOfDay(ZoneId.systemDefault()).toInstant());
			List<NodeProfit> profits = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId, st, et);
			if (profits.isEmpty()) {
				try {
					List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId,
							request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime());
					if (prices.isEmpty()) {
						throw new RuntimeException("找不到对应数据!");
					}
					prices.forEach(o -> {
						if (o.getProperty().equals("尖")) {
							result.setPriceSharp(o.getPrice());
						}
						if (o.getProperty().equals("峰")) {
							result.setPricePeak(o.getPrice());
						}
						if (o.getProperty().equals("平")) {
							result.setPriceShoulder(o.getPrice());
						}
						if (o.getProperty().equals("谷")) {
							result.setPriceOffPeak(o.getPrice());
						}
//						if (o.getProperty().equals("深谷")) {
//							result.setPriceRavine(o.getPrice());
//						}
					});
					Map<String, String> timeSlotMap = prices.stream().collect(Collectors.toMap(ElectricityPrice::getTimeFrame,
							ElectricityPrice::getProperty));
					result.setTimeSlots(timeSlotMap);
					return result;
				} catch (Exception e) {
					throw new RuntimeException("找不到对应数据!");
				}
			}
			result.setPriceSharp(BigDecimal.valueOf(profits.get(0).getPriceHigh()));
			result.setPricePeak(BigDecimal.valueOf(profits.get(0).getPricePeak()));
			result.setPriceShoulder(BigDecimal.valueOf(profits.get(0).getPriceStable()));
			result.setPriceOffPeak(BigDecimal.valueOf(profits.get(0).getPriceLow()));
//			result.setPriceRavine(BigDecimal.valueOf(profits.get(0).getPriceRavine()));
			LocalDateTime effectiveDate = request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId, effectiveDate);
			if (prices.isEmpty()) {
				// 查询旧电价表
				INodeProfitService nodeProfitService = SpringBeanHelper.getBeanOrThrow(INodeProfitService.class);
				// 光伏旧表
				if (nodeProfitService.getPvNodeIdList().contains(nodeId)) {
					CfgPhotovoltaicTouPriceRepository repository = SpringBeanHelper.getBeanOrThrow(CfgPhotovoltaicTouPriceRepository.class);
					List<CfgPhotovoltaicTouPrice> priceList = repository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao", st);
					Map<String, String> timeSlotMap = priceList.stream().collect(Collectors.toMap(CfgPhotovoltaicTouPrice::getTimeFrame,
							CfgPhotovoltaicTouPrice::getProperty));
					result.setTimeSlots(timeSlotMap);
				}
				// 储能旧表
				else if (nodeProfitService.getStoreEnergyNodeIdList().contains(nodeId)) {
					CfgStorageEnergyStrategyRepository repository = SpringBeanHelper.getBeanOrThrow(CfgStorageEnergyStrategyRepository.class);
					List<CfgStorageEnergyStrategy> priceList = repository.findAllByNodeIdAndSystemIdAndEffectiveDate(nodeId, "nengyuanzongbiao", st);
					Map<String, String> timeSlotMap = priceList.stream().collect(Collectors.toMap(CfgStorageEnergyStrategy::getTimeFrame,
							CfgStorageEnergyStrategy::getProperty));
					result.setTimeSlots(timeSlotMap);
				}
				return result;
			}
			Map<String, String> timeSlotMap = prices.stream().collect(Collectors.toMap(ElectricityPrice::getTimeFrame,
					ElectricityPrice::getProperty));
			result.setTimeSlots(timeSlotMap);
		}
		return result;
	}

	/**
	 * 只复制电价，不复制时段
	 * 时段从电价库获取
	 *
	 * @param request
	 */
	public void copyEpLastMonth(CopyNodeEpRequest request) {
		ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
		NodeProfitRepository nodeProfitRepository = SpringBeanHelper.getBeanOrThrow(NodeProfitRepository.class);
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);

		StationNode stationNode = stationNodeRepository.findByStationId(request.getNodeId());

		// 上个月开始时间
		Date st = Date.from(request.getDate().atDay(1).minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		Date et = Date.from(request.getDate().atEndOfMonth().minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

		LocalDate now = LocalDate.now();
		LocalDateTime nowMonth = request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		Map<String, BigDecimal> priceMap = new HashMap<>();
		Map<LocalTime, String> propertyMap = new HashMap<>();

		List<ElectricityPrice> res;

		// 项目电价，维护自己的表
		if (stationNode.getStationCategory().contains("项目")) {
			LocalDateTime effectiveDate = request.getDate().atDay(1).minusMonths(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
			List<ElectricityPrice> prices =
					electricityPriceRepository.findAllByNodeIdAndEffectiveDate(request.getNodeId(), effectiveDate).stream().sorted(Comparator.comparing(ElectricityPrice::getSTime)).collect(Collectors.toList());
			if (prices.isEmpty()) {
				throw new RuntimeException("无上月电价信息！");
			}
			prices.forEach(o -> priceMap.put(o.getProperty(), o.getPrice()));

			// 非项目电价，从收益表中获取
		} else {
			List<NodeProfit> profits = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(request.getNodeId(), st, et);
			if (profits == null || profits.isEmpty()) {
				throw new RuntimeException("无上月电价信息！");
			}
			priceMap.put("尖", BigDecimal.valueOf(profits.get(0).getPriceHigh()));
			priceMap.put("峰", BigDecimal.valueOf(profits.get(0).getPricePeak()));
			priceMap.put("平", BigDecimal.valueOf(profits.get(0).getPriceStable()));
			priceMap.put("谷", BigDecimal.valueOf(profits.get(0).getPriceLow()));
		}

		res = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(request.getNodeId(), nowMonth);
		if (res.isEmpty()) {
			UpdateNodeEPBatchRequest updateNodeEPBatchRequest = new UpdateNodeEPBatchRequest();
			updateNodeEPBatchRequest.setNodeId(request.getNodeId());
			updateNodeEPBatchRequest.setCity(stationNode.getProvince());
			updateNodeEPBatchRequest.setDate(request.getDate());
			generateEPrice(updateNodeEPBatchRequest, priceMap, now, res);
		} else {
			for (ElectricityPrice price : res) {
				price.setPrice(priceMap.get(price.getProperty()));
			}
		}
		electricityPriceRepository.saveAll(res);

		for (ElectricityPrice price : res) {
			priceMap.put(price.getProperty(), price.getPrice());
			propertyMap.put(LocalTime.parse(price.getSTime()), price.getProperty());
		}

		if (priceMap.isEmpty() || propertyMap.isEmpty()) {
			throw new RuntimeException("无上月电价信息!");
		}

		// 更新收益、写入旧表
			Date etProfit = Date.from(request.getDate().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
		Date effectiveDate = Date.from(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
		// 异步调用
		CompletableFuture.runAsync(() -> reCalculateProfit(request.getNodeId(), etProfit, priceMap, propertyMap));
		CompletableFuture.runAsync(() -> overWriteOldTablePrice(request.getNodeId(), effectiveDate, priceMap));
//		CompletableFuture.runAsync(() -> setHolidays(request.getDate().getYear(), request.getDate().getMonthValue(), request.getNodeId()));
	}

	public void copyEpFromProject(CopyNodeEpRequest request) {
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		StationNode stationNode = stationNodeRepository.findByStationId(request.getNodeId());
		if (stationNode.getStationCategory().contains("项目")) {
			throw new RuntimeException("只有系统节点能复制项目电价");
		}

		LocalDateTime effectiveDate = request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

		List<Object[]> nodeParent = stationNodeRepository.findNodeHierarchy(request.getNodeId());
		List<String> parentList = nodeParent.stream().filter(o -> o[1] != null).map(o -> String.valueOf(o[1])).collect(Collectors.toList());
		// 逐级向上查找
		for (String parentId : parentList) {
			ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
			List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(parentId, effectiveDate);
			if (prices.size() != 96) {
				continue;
			}
			List<ElectricityPrice> res = new ArrayList<>();
			for (ElectricityPrice electricityPrice : prices) {
				ElectricityPrice price = new ElectricityPrice(electricityPrice);
				price.setNodeId(request.getNodeId());
				price.setId(price.getNodeId() + "_" + request.getDate().getYear() + "-" + request.getDate().getMonthValue() + "_" + price.getTimeFrame() + "_" + price.getDateType() + "_" + price.getPriceUse());
				res.add(price);
			}
			electricityPriceRepository.deleteAllByNodeIdAndEffectiveDate(request.getNodeId(), effectiveDate);
			electricityPriceRepository.saveAll(res);
			Map<String, BigDecimal> priceMap = new HashMap<>();
			prices.forEach(o -> priceMap.put(o.getProperty(), o.getPrice()));
			Map<String, String> timeMap = new HashMap<>();
			prices.forEach(o -> timeMap.put(o.getTimeFrame(), o.getProperty()));
			Map<LocalTime, String> propertyMap = new HashMap<>();
			prices.forEach(o -> propertyMap.put(LocalTime.parse(o.getSTime()), o.getProperty()));
			Date etProfit = Date.from(request.getDate().atZone(ZoneId.systemDefault()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
			Date st = Date.from(request.getDate().atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

			// 异步调用
			CompletableFuture.runAsync(() -> reCalculateProfit(request.getNodeId(), etProfit, priceMap, propertyMap));
			CompletableFuture.runAsync(() -> overWriteOldTableTime(request.getNodeId(), st, priceMap, timeMap));
//			CompletableFuture.runAsync(() -> setHolidays(request.getDate().getYear(), request.getDate().getMonthValue(), request.getNodeId()));
			return;
		}
		throw new RuntimeException("找不到项目电价");
	}

	public List<EleNodeInfo> queryCityInfo(String city, String province) {
		JSONObject response = JSON.parseObject(HttpUtil.okHttpGet(url + "/charge_info/queryCityEleSystem"));
		JSONArray jsonArray = (JSONArray) response.get("data");
		List<EleNodeInfo> nodeInfo = jsonArray.toJavaList(EleNodeInfo.class);

		List<EleNodeInfo> res = new ArrayList<>();
		for (EleNodeInfo eleNodeInfo : nodeInfo) {
			if (eleNodeInfo.getField().equals("city") && eleNodeInfo.getValue().equals(city)) {
				res = eleNodeInfo.getChildren();
			}
		}
		if (res.isEmpty() && !StringUtils.isEmpty(province)) {
			for (EleNodeInfo eleNodeInfo : nodeInfo) {
				if (eleNodeInfo.getField().equals("city") && eleNodeInfo.getValue().equals(province)) {
					res = eleNodeInfo.getChildren();
				}
			}
		}

		return res;
	}

	public String queryElectricityCompany(String city) {
		return HttpUtil.okHttpGet(url + "/charge_info/queryElectricityCompany" + "?city=" + city);
	}

	public void setHolidays(int year, int month, String nodeId) {
		List<ElectricityHolidayInfo> list = new ArrayList<>();
		StationNodeRepository stationNodeRepository = SpringBeanHelper.getBeanOrThrow(StationNodeRepository.class);
		StationNode stationNode = stationNodeRepository.findByStationId(nodeId);
		if (com.example.vvpcommom.StringUtils.isEmpty(stationNode.getEType())) {
			return;
		}
		String[] type = stationNode.getEType().split("-");
		if (type.length != 2) {
			return;
		}
		ElectricityHolidayResponse res = getElectricityHolidayInfo(stationNode.getProvince(), type[0], type[1],
				stationNode.getVoltage(),
				stationNode.getElectricityCompany(), year, month);
		if (res == null || res.getHolidayInfos().isEmpty()) {
			return;
		}
		res.getHolidayInfos().forEach(holidayInfo -> {
			ElectricityHolidayInfo info = new ElectricityHolidayInfo();
			info.getPk().setNodeId(nodeId);
			info.getPk().setDate(holidayInfo.getDate());
			info.getPk().setSt(holidayInfo.getSt());
			info.getPk().setEt(holidayInfo.getEt());
			info.setType(holidayInfo.getCateGory());
			info.setPrice(holidayInfo.getPrice());
			list.add(info);
		});
		ElectricityHolidayRepository electricityHolidayRepository = SpringBeanHelper.getBeanOrThrow(ElectricityHolidayRepository.class);
		electricityHolidayRepository.saveAll(list);
	}

	public Map<String, BigDecimal> getPriceMap(String nodeId, LocalDateTime effectiveDate) {
		if (effectiveDate == null) {
			// 获取当前月的第一天
			LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
			// 将 LocalDate 转换为 LocalDateTime，并设置时间为 00:00:00
			effectiveDate = firstDayOfMonth.atStartOfDay();
		}
		Map<String, BigDecimal> priceMap = new HashMap<>();
		ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);
		List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(nodeId, effectiveDate);
		for (ElectricityPrice price : prices) {
			priceMap.put(price.getProperty(), price.getPrice());
		}

		ElectricityHolidayRepository electricityHolidayRepository = SpringBeanHelper.getBeanOrThrow(ElectricityHolidayRepository.class);
		LocalDate firstDayOfMonth = LocalDate.from(effectiveDate).withDayOfMonth(1);
		LocalDate lastDayOfMonth = LocalDate.from(effectiveDate).with(TemporalAdjusters.lastDayOfMonth());
		List<ElectricityHolidayInfo> holidayInfos = electricityHolidayRepository.findAllByPk_NodeIdAndPk_DateBetween(nodeId, firstDayOfMonth,
				lastDayOfMonth);
		holidayInfos.forEach(o -> priceMap.put(o.getType(), o.getPrice()));
		return priceMap;
	}

}
