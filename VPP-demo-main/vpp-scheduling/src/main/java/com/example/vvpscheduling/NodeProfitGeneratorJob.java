package com.example.vvpscheduling;

import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpservice.nodeep.model.ElectricityInfoModel;
import com.example.vvpservice.nodeep.service.NodeEpService;
import com.example.vvpservice.nodeprofit.service.INodeProfitService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;

/**
 * 收益生成计算调度任务
 */
@Component("nodeProfitJob")
@EnableAsync
public class NodeProfitGeneratorJob {

	private static final Logger logger = LoggerFactory.getLogger(NodeProfitGeneratorJob.class);
	@Resource
	private NodeProfitRepository nodeProfitRepository;
	@Resource
	private NodeProfitMonthForecastingRepository nodeProfitMonthForecastingRepository;
	@Resource
	private NodeProfitDayForecastingRepository nodeProfitDayForecastingRepository;
	@Resource
	private INodeProfitService iNodeProfitService;

	/**
	 * 光伏分时电价
	 */
	@Resource
	private CfgPhotovoltaicTouPriceRepository cfgPhotovoltaicTouPriceRepository;
	@Resource
	private CfgStorageEnergyStrategyRepository cfgStorageEnergyStrategyRepository;

	@Resource
	private ElectricityPriceRepository electricityPriceRepository;
	@Resource
	private StationNodeRepository stationNodeRepository;

	/**
	 * 收益生成
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 30 * 60 * 1000)
	@Async
	public void generatePvProfit() {

		ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);

		//光伏收益计算
		iNodeProfitService.getPvNodeIdList().forEach(o -> {
			try {
				// 获取当前月的第一天
				LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

				// 将 LocalDate 转换为 LocalDateTime，并设置时间为 00:00:00
				LocalDateTime firstMomentOfMonth = firstDayOfMonth.atStartOfDay();
				Map<String, BigDecimal> priceMap = new HashMap<>();
				Map<LocalTime, String> propertyMap = new HashMap<>();
				List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(o, firstMomentOfMonth);

				// 新表获取电价：96点
				if (prices.size() == 96) {
					for (ElectricityPrice price : prices) {
						priceMap.put(price.getProperty(), price.getPrice());
						propertyMap.put(LocalTime.parse(price.getSTime()), price.getProperty());
					}
					iNodeProfitService.doCalculatePvProfit(o, priceMap, propertyMap, new Date());
					// 旧表获取电价：24点转96点
				} else {
					List<CfgPhotovoltaicTouPrice> cfgPhotovoltaicTouPrices = cfgPhotovoltaicTouPriceRepository.findAllByNodeIdAndEffectiveDate(o,
							TimeUtil.getMonthStart(new Date()));
					for (CfgPhotovoltaicTouPrice price : cfgPhotovoltaicTouPrices) {
						priceMap.put(price.getProperty(), price.getPriceHour());
						LocalTime sTime = LocalTime.parse(price.getSTime());
						for (int i = 0; i < 4; i++) {
							propertyMap.put(sTime, price.getProperty());
							sTime = sTime.plusMinutes(15);
						}
					}
					iNodeProfitService.doCalculatePvProfit(o, priceMap, propertyMap, new Date());
				}
			} catch (Exception e) {
				logger.error("节点：{},生成光伏收益异常.", o, e);
			}
		});

	}

	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 30 * 60 * 1000)
	@Async
	public void generateEsProfit() {

		ElectricityPriceRepository electricityPriceRepository = SpringBeanHelper.getBeanOrThrow(ElectricityPriceRepository.class);

		//储能收益计算
		iNodeProfitService.getStoreEnergyNodeIdList().forEach(o -> {
			try {
				// 获取当前月的第一天
				LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);

				// 将 LocalDate 转换为 LocalDateTime，并设置时间为 00:00:00
				LocalDateTime firstMomentOfMonth = firstDayOfMonth.atStartOfDay();
				Map<String, BigDecimal> priceMap = new HashMap<>();
				Map<LocalTime, String> propertyMap = new HashMap<>();
				List<ElectricityPrice> prices = electricityPriceRepository.findAllByNodeIdAndEffectiveDate(o, firstMomentOfMonth);

				// 新表获取电价：96点
				if (prices.size() == 96) {
					for (ElectricityPrice price : prices) {
						priceMap.put(price.getProperty(), price.getPrice());
						propertyMap.put(LocalTime.parse(price.getSTime()), price.getProperty());
					}
					iNodeProfitService.doCalculateStorageEnergyProfit(o, priceMap, propertyMap, new Date());
					// 旧表获取电价：24点转96点
				} else {
					List<CfgStorageEnergyStrategy> cfgStorageEnergyStrategies =
							cfgStorageEnergyStrategyRepository.findAllByNodeIdAndEffectiveDate(o, TimeUtil.getMonthStart(new Date()));
					for (CfgStorageEnergyStrategy price : cfgStorageEnergyStrategies) {
						priceMap.put(price.getProperty(), price.getPriceHour());
						LocalTime sTime = LocalTime.parse(price.getSTime());
						for (int i = 0; i < 4; i++) {
							propertyMap.put(sTime, price.getProperty());
							sTime = sTime.plusMinutes(15);
						}
					}
					iNodeProfitService.doCalculateStorageEnergyProfit(o, priceMap, propertyMap, new Date());
				}
			} catch (Exception e) {
				logger.error("节点：{},生成储能收益异常.", o, e);
			}
		});

	}

	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 30 * 60 * 1000)
	@Async
	public void generateChargingPileProfit() {
		try {
			List<StationNode> stationNodes = stationNodeRepository.findAllByStationTypeIdAndStationState("chongdianzhuang");
			LocalDateTime nowTime = LocalDateTime.now();
			LocalDate firstDayOfMonth = nowTime.toLocalDate().withDayOfMonth(1);
			Map<String, String> nodes = stationNodes.stream().collect(Collectors.toMap(StationNode::getStationId, StationNode::getParentId));
			List<String> nodeIds = nodes.entrySet().stream()
					.flatMap(entry -> Stream.of(entry.getKey(), entry.getValue()))
					.filter(Objects::nonNull)
					.filter(str -> !str.trim().isEmpty())
					.distinct()
					.collect(Collectors.toList());
			Map<String, List<ElectricityPrice>> cpPropertyTypes = electricityPriceRepository
					.findAllByNodeIdAndEffectiveDate(nodeIds, firstDayOfMonth, "0")
					.stream()
					.collect(Collectors.groupingBy(ElectricityPrice::getNodeId));

			nodes.forEach((k, v) -> {
				try {
					List<ElectricityPrice> electricityPrices = cpPropertyTypes.get(k) == null ? cpPropertyTypes.get(v) : cpPropertyTypes.get(k);
					if (electricityPrices == null) {
						logger.info("没配置电价,无法计算收益：{}", k);
						return;
					}
					Map<String, ElectricityInfoModel> timeInfo = electricityPrices.stream()
							.collect(Collectors.toMap(
									epk -> convertTimeFormat(epk.getTimeFrame()),
									ep -> new ElectricityInfoModel(ep.getPrice(), ep.getProperty())
							));
					iNodeProfitService.doCalculateChargingPileProfit(k, timeInfo, nowTime, firstDayOfMonth);
				} catch (Exception e) {
					logger.error("生成节点" + k + "的充电桩收益异常" + e.getMessage());
				}
			});
		} catch (Exception e) {
			logger.error("生成充电桩收益异常" + e.getMessage());
		}
	}
	public static String convertTimeFormat(String timeFrame) {
		return timeFrame.substring(0, 5) + "-" + timeFrame.substring(9, 14);
	}
	/**
	 * 收益生成
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 30 * 60 * 1000)
	@Async
	public void generateProfitMonthForecasting() {
		List<NodeProfit> monthProfit =
				nodeProfitRepository.findAllByProfitDateBetweenOrderByProfitDateAsc(TimeUtil.getMonthStart(TimeUtil.getPreMonth(new Date(), -2)),
						new Date());
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

	/**
	 * 收益生成
	 */
	@Scheduled(initialDelay = 1000 * 5, fixedDelay = 30 * 60 * 1000)
	@Async
	public void generateProfitDayForecasting() {
		List<NodeProfit> nodeProfits =
				nodeProfitRepository.findAllByProfitDateBetweenOrderByProfitDateAsc(TimeUtil.getMonthStart(TimeUtil.getPreMonth(new Date(), -2)),
						new Date());

		List<NodeProfitDayForecasting> nodeProfitListd = new ArrayList<>();


		nodeProfits.forEach(el -> {
			NodeProfitDayForecasting npd = nodeProfitDayForecastingRepository.findById(el.getNodeId() + TimeUtil.toYmdStr(el.getProfitDate())).
					orElse(new NodeProfitDayForecasting());
			npd.setProfitId(el.getNodeId() + TimeUtil.toYmdStr(el.getProfitDate()));
			npd.setNodeId(el.getNodeId());
			npd.setProfitDateDay(el.getProfitDate());
			npd.setProfitValue(el.getProfitValue());

			nodeProfitListd.add(npd);
		});

		nodeProfitDayForecastingRepository.saveAll(nodeProfitListd);

	}

	//	@Scheduled(cron = "0 0 1 * * *")
//	@Scheduled(initialDelay = 1000, fixedDelay = 30 * 60 * 1000)
	public void getHolidayInfos() {
		LocalDate currentDate = LocalDate.now();
		int year = currentDate.getYear();
		int month = currentDate.getMonthValue();
		ElectricityHolidayRepository electricityHolidayRepository = SpringBeanHelper.getBeanOrThrow(ElectricityHolidayRepository.class);
		List<ElectricityHolidayInfo> list = new ArrayList<>();
		NodeEpService nodeEpService = SpringBeanHelper.getBeanOrThrow(NodeEpService.class);
		iNodeProfitService.getStoreEnergyNodeIdList().forEach(o -> {
			try {
				nodeEpService.setHolidays(year,month,o);
			} catch (Exception e) {
				logger.error("error", e);
			}
		});
		iNodeProfitService.getPvNodeIdList().forEach(o -> {
			try {
				nodeEpService.setHolidays(year,month,o);
			} catch (Exception e) {
				logger.error("error", e);
			}
		});

	}

}
