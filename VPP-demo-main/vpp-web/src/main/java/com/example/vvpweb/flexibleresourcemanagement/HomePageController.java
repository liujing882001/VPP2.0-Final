package com.example.vvpweb.flexibleresourcemanagement;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.*;
import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.Enum.SysParamEnum;
import com.example.vvpdomain.*;
import com.example.vvpdomain.dto.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpdomain.view.NodeInfoView;
import com.example.vvpdomain.view.SalesPerSquaremeterCommercialcomplexView;
import com.example.vvpdomain.view.SalesPerSquaremeterGovernmentOfficeGreaterThan20000View;
import com.example.vvpdomain.view.SalesPerSquaremeterGovernmentOfficeLessThan20000View;
import com.example.vvpscheduling.RevenueAnalysisJob;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.point.service.PointService;
import com.example.vvpservice.revenue.RevenueAnalysisService;
import com.example.vvpweb.demand.model.CopilotResponse;
import com.example.vvpweb.flexibleresourcemanagement.model.*;
import com.example.vvpweb.flexibleresourcemanagement.model.VO.NodeInfoCountVO;
import com.example.vvpweb.flexibleresourcemanagement.model.VO.RAInfoVO;
import com.example.vvpweb.flexibleresourcemanagement.model.VO.RevenueAnalysisVO;
import com.example.vvpweb.systemmanagement.energymodel.model.CopilotBlockResponse;
import com.example.vvpweb.tradepower.model.TradeEnvironmentConfig;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.lang.Double.isNaN;

/**
 * @author zph
 * @description 首页
 * @date 2022-06-06
 */
@RestController
@RequestMapping("/homePage")
@CrossOrigin
@Slf4j
@Api(value = "首页", tags = {"首页"})
public class HomePageController {

	@Resource
	private NodeRepository nodeRepository;
	@Resource
	private CfgStorageEnergyBaseInfoRepository energyBaseInfoRepository;
	@Resource
	private CfgPhotovoltaicBaseInfoRepository photovoltaicBaseInfoRepository;
	@Resource
	private DeviceRepository deviceRepository;
	@Resource
	private IotTsKvLastRepository iotTsKvLastRepository;
	@Resource
	private NodeInfoViewRepository nodeInfoViewRepository;
	@Resource
	private AiLoadRepository aiLoadRepository;
	@Resource
	private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
	@Resource
	private CaEmissionFactorRepository caEmissionFactorRepository;
	@Resource
	private SysParamRepository sysParamRepository;
	@Resource
	private SalesPerSquaremeterCommercialcomplexViewRepository commercialcomplexViewRepository;
	@Resource
	private SalesPerSquaremeterGovernmentOfficeGreaterThan20000ViewRepository governmentOfficeGreaterThan20000ViewRepository;
	@Resource
	private SalesPerSquaremeterGovernmentOfficeLessThan20000ViewRepository governmentOfficeLessThan20000ViewRepository;
	@Resource
	private StationNodeRepository stationNodeRepository;
	@Resource
	private GlobalApiService globalApiService;
	@Resource
	private CfgStorageEnergyStrategyPower96Repository cfgStorageEnergyStrategyPower96Repository;
	@Resource
	private BiStorageEnergyLogRepository biStorageEnergyLogRepository;
	@Resource
	private ElectricityPriceRepository electricityPriceRepository;
	@Resource
	private NodeProfitRepository nodeProfitRepository;
	@Resource
	private RevenueAnalysisService revenueAnalysisService;
	@Resource
	private PointModelMappingRepository pointModelMappingRepository;
	@Resource
	private RevenueAnalysisRepository revenueAnalysisRepository;
	@Resource
	private RevenueAnalysisJob revenueAnalysisJob;
	private static TradeEnvironmentConfig config;

	@Autowired
	public HomePageController(TradeEnvironmentConfig environmentConfig) {
		config = environmentConfig;
	}

	@ApiOperation("地图节点展示")
	@RequestMapping(value = "getNodeLocation", method = {RequestMethod.GET})
	public ResponseResult<List<GisModel>> getNodeLocation() {
		try {
			List<GisModel> gisModels = new ArrayList<>();

			Map<String, Double> loadContent = new HashMap<>();

			List<NodeInfoView> nodeInfoViews = nodeInfoViewRepository.findAllByNodePostType(NodePostTypeEnum.load.getNodePostType());
			if (nodeInfoViews != null && nodeInfoViews.size() > 0) {

				List<String> nodeItems = new ArrayList<>();
				for (NodeInfoView nodeInfo : nodeInfoViews) {
					nodeItems.add(nodeInfo.getNodeId());
				}
				if (nodeItems != null && nodeItems.size() > 0) {
					//接入负荷：负荷节点的能源总表对应的负荷
					List<IotTsKvLast> jieRuLoadItems = iotTsKvLastRepository.findLatestPointValue(nodeItems, "nengyuanzongbiao", "load", "MSG");
					if (jieRuLoadItems != null && jieRuLoadItems.size() > 0) {

						Map<String, List<IotTsKvLast>> detailsMap = jieRuLoadItems.stream().collect(Collectors.groupingBy(c -> c.getNodeId()));
						if (detailsMap != null && detailsMap.size() > 0) {
							for (String nodeId : detailsMap.keySet()) {

								List<IotTsKvLast> jieRuLoad = detailsMap.get(nodeId);
								if (jieRuLoad != null && jieRuLoad.size() > 0) {
									Double load = jieRuLoad.stream().mapToDouble(c -> Double.parseDouble(StringUtils.isNotEmpty(c.getPointValue()) ?
											c.getPointValue() : "0")).sum();
									loadContent.put(nodeId, load);
								}
							}
						}
					}

				}
			}


			Map<String, StationNode> stationNodes = stationNodeRepository.findAll().stream().collect(Collectors.toMap(StationNode::getStationId,
					stationNode -> stationNode));
			List<NodeInfoView> nodes = nodeInfoViewRepository.findAll();
			if (nodes != null && nodes.size() > 0) {

				nodes.stream().forEach(node -> {
					GisModel gisModel = new GisModel();
					gisModel.setNodeId(node.getNodeId());
					gisModel.setLatitude(node.getLatitude());
					gisModel.setLongitude(node.getLongitude());
					gisModel.setNodeName(node.getNodeName());
					try {
						gisModel.setStationCategory(stationNodes.get(node.getNodeId()).getStationCategory());
						gisModel.setStationState(stationNodes.get(node.getNodeId()).getStationState());
					} catch (Exception e) {
						log.error("node not found in station nodes :node:{}", node.getNodeId());
					}
					gisModel.setNodePostType(node.getNodePostType());
					String content = "";
					switch (node.getNodePostType()) {
						case "pv":
							content = "装机容量 " + String.format("%.2f", node.getCapacity()) + " kW";
							break;
						case "storageEnergy":
							content = "容量 " + String.format("%.2f", node.getCapacity()) + " kWh";
							break;
						case "load":
							double jieRuLoad = loadContent.containsKey(node.getNodeId()) ? loadContent.get(node.getNodeId()) : (double) 0;
							content = "可调负荷 " + String.format("%.2f", node.getCapacity()) + "kW/接入负荷 " + String.format("%.2f", jieRuLoad) + " kW";
							break;
					}
					gisModel.setContent(content);

					gisModels.add(gisModel);
				});
			}

			return ResponseResult.success(gisModels);
		} catch (Exception e) {
			log.error("error", e);
			return ResponseResult.success(null);
		}
	}

	@ApiOperation("地图节点展示根据节点列表")
	@RequestMapping(value = "getNodesLocation", method = {RequestMethod.POST})
	public ResponseResult<List<GisModel>> getNodesLocation(@RequestBody GetNodesLocationCommand command) {
		try {
			List<GisModel> gisModels = new ArrayList<>();
			List<String> nodeIds;
			if (command.getNodes().isEmpty()) {
				nodeIds = globalApiService.useNodes();
			} else {
				nodeIds = command.getNodes();
			}
			List<StationNode> stationNodes = stationNodeRepository.findAllByNodeIdsAndSc(nodeIds);

			Calendar calendar = Calendar.getInstance();
			calendar.add(Calendar.MINUTE, -30);
			Date thirtyMinutesAgo = calendar.getTime();
			stationNodes.forEach(sn -> {
				String nodeId = sn.getStationId();
				Double loadJieRu = 0.0;
				List<String> loadNodes = globalApiService.findSubLoadIdsByStationIds(nodeId);
				List<String> jieru_kw_objects =
						iotTsKvLastRepository.findLatestPointValues(loadNodes,
								"nengyuanzongbiao", "load", "MSG",thirtyMinutesAgo);
				if (jieru_kw_objects != null && !jieru_kw_objects.isEmpty()) {
					loadJieRu = jieru_kw_objects.stream().mapToDouble(c -> Double.parseDouble(StringUtils.isNotEmpty(c) ?
							c : "0.0")).sum();
				}
				Double loadKeTiao = deviceRepository.findAllLoadKWByNodeIds(loadNodes);
				GisModel gisModel = new GisModel();
				gisModel.setNodeId(nodeId);
				gisModel.setLatitude(sn.getLatitude());
				gisModel.setLongitude(sn.getLongitude());
				gisModel.setNodeName(sn.getStationName());
				gisModel.setStationCategory(sn.getStationCategory());
				gisModel.setStationState(sn.getStationState());
				gisModel.setNodePostType(sn.getStationType());

				gisModel.setLoadKeTiao(BigDecimal.valueOf(loadKeTiao).setScale(2, RoundingMode.HALF_UP).doubleValue());
				gisModel.setLoadJieRu(BigDecimal.valueOf(loadJieRu).setScale(2, RoundingMode.HALF_UP).doubleValue());

				gisModels.add(gisModel);
			});

			return ResponseResult.success(gisModels);
		} catch (Exception e) {
			log.error("error", e);
			return ResponseResult.success(null);
		}
	}
	@ApiOperation("当日资源情况-节点信息分类统计-根据项目节点列表")
	@RequestMapping(value = "getNodesInfoCount", method = {RequestMethod.POST})
	public ResponseResult getNodesInfoCount(@RequestBody NodesInfoCountCommand command) {
		List<String> nodeIds;
		if (command.getNodes().isEmpty()) {
			nodeIds = globalApiService.useNodes();
		} else {
			nodeIds = command.getNodes();
		}
		//负荷
		int loadCount = 0;
		double loadJieRu = 0.0;
		double loadKeTiao = 0.0;
		int zszx = 0;
		int znjt = 0;
		int znzz = 0;
		int lyyq = 0;
		//储能
		int storageEnergyCount = 0;
		double storageEnergyCapacity = 0.0;
		double storageEnergyPower = 0.0;
		//发电
		int pvCount = 0;
		double pvCapacity = 0.0;
		int windCount = 0;
		double windCapacity = 0.0;
		List<Object[]> subNodes = globalApiService.findSubTreeByStationIds(nodeIds);
		List<String> loadNodes = new ArrayList<>();
		List<String> pvNodes = new ArrayList<>();
		List<String> energyNodes = new ArrayList<>();
		for (Object[] sn :subNodes) {
			String nodeId = (String) sn[0];
			String nodeTypeId = (String) sn[1];
			String systemIds = (String) sn[2];
			if (nodeTypeId.equals("zszx")) {
				zszx = zszx +1;
			} else if(nodeTypeId.equals("znjt")) {
				znjt = znjt +1;
			} else if(nodeTypeId.equals("znzz")) {
				znzz = znzz +1;
			} else if(nodeTypeId.equals("lyyq")) {
				lyyq = lyyq +1;
			}
			if (systemIds.contains("guangfu")) {
				pvNodes.add(nodeId);
			} else if (systemIds.contains("chuneng")) {
				energyNodes.add(nodeId);
			} else {
				loadNodes.add(nodeId);
			}
		}
		storageEnergyCount = energyNodes.size();
		pvCount = pvNodes.size();
		loadCount = loadNodes.size();
		loadKeTiao = deviceRepository.findAllLoadKWByNodeIds(loadNodes);
		pvCapacity = photovoltaicBaseInfoRepository.sumPhotovoltaicPowersByNodeIds(pvNodes);
		Object[] energySum = energyBaseInfoRepository.sumStorageEnergyByNodeIds(energyNodes);
		if (energySum != null && energySum.length > 0 && energySum[0] instanceof Object[]) {
			Object[] sumValues = (Object[]) energySum[0];

			if (sumValues.length > 0 && sumValues[0] instanceof Number) {
				storageEnergyPower = ((Number) sumValues[0]).doubleValue();
			} else {
				storageEnergyPower = 0.0;
			}

			if (sumValues.length > 1 && sumValues[1] instanceof Number) {
				storageEnergyCapacity = ((Number) sumValues[1]).doubleValue();
			} else {
				storageEnergyCapacity = 0.0;
			}
		} else {
			storageEnergyPower = 0.0;
			storageEnergyCapacity = 0.0;
		}
		Calendar calendar = Calendar.getInstance();
		// 将时间往回推30分钟
		calendar.add(Calendar.MINUTE, -30);
		// 获取30分钟前的时间
		Date thirtyMinutesAgo = calendar.getTime();
		//接入负荷：负荷节点的能源总表对应的负荷
		List<String> jieru_kw_objects =
				iotTsKvLastRepository.findLatestPointValues(loadNodes,
						"nengyuanzongbiao", "load", "MSG",thirtyMinutesAgo);
		if (jieru_kw_objects != null && !jieru_kw_objects.isEmpty()) {
			loadJieRu = jieru_kw_objects.stream().mapToDouble(c -> Double.parseDouble(StringUtils.isNotEmpty(c) ?
					c : "0")).sum();
		}
		subNodes.removeAll(nodeIds);
		NodeInfoCountVO vo = new NodeInfoCountVO();
		vo.setStorageEnergyCount(storageEnergyCount);
		vo.setStorageEnergyCapacity(storageEnergyCapacity);
		vo.setStorageEnergyPower(storageEnergyPower);
		vo.setPvCount(pvCount);
		vo.setPvCapacity(pvCapacity);
		vo.setLoadCount(loadCount);
		vo.setLoadJieRu(BigDecimal.valueOf(loadJieRu).setScale(2, RoundingMode.HALF_UP).doubleValue());
		vo.setLoadKeTiao(BigDecimal.valueOf(loadKeTiao).setScale(2, RoundingMode.HALF_UP).doubleValue());
		vo.setZszx(zszx);
		vo.setZnjt(znjt);
		vo.setZnzz(znzz);
		vo.setLyyq(lyyq);
		int proCount = zszx + znjt + znzz + lyyq;
		if (proCount != 0) {
			vo.setZszxPer(Math.round((double) zszx / proCount * 1000) / 10.0);
			vo.setZnjtPer(Math.round((double) znjt / proCount * 1000) / 10.0);
			vo.setZnzzPer(Math.round((double) znzz / proCount * 1000) / 10.0);
			vo.setLyyqPer(Math.round((double) lyyq / proCount * 1000) / 10.0);
		}
		vo.setWindCount(windCount);
		vo.setWindCapacity(windCapacity);
		return ResponseResult.success(vo);
	}
	@ApiOperation("收益分析-首页")
	@RequestMapping(value = "revenueAnalysis", method = {RequestMethod.POST})
	public ResponseResult revenueAnalysis(@RequestBody RevenueAnalysisCommand command) {
		return ResponseResult.success(revenueAnalysisRepository.findById(command.getNodeId()).orElse(new RevenueAnalysis()));
	}
	@ApiOperation("收益分析-首页")
	@RequestMapping(value = "revenueAnalysisTest", method = {RequestMethod.POST})
	public ResponseResult revenueAnalysisTest(@RequestBody RevenueAnalysisCommand command) {
		revenueAnalysisJob.revenueAnalysis();
		return ResponseResult.success(revenueAnalysisRepository.findById(command.getNodeId()));
	}
	@ApiOperation("收益分析-首页")
	@RequestMapping(value = "revenueAnalysisOld", method = {RequestMethod.POST})
	public ResponseResult revenueAnalysisOld(@RequestBody RevenueAnalysisCommand command) {
//		try {
		String queryNodeId = command.getNodeId();
		String queryTime = command.getTime();
		RevenueAnalysisVO vo = new RevenueAnalysisVO();
		LocalDateTime eLocalDate = LocalDateTime.now();
//		LocalDateTime eLocalDate = LocalDateTime.of(2024, 12, 31, 23, 59, 0, 0);
		LocalDateTime firstDayOfMonth = eLocalDate.withDayOfMonth(1).toLocalDate().atStartOfDay();
		LocalDateTime firstDayOfYear = eLocalDate.withDayOfYear(1).toLocalDate().atStartOfDay();
		LocalDateTime sLocalDate = "year".equals(queryTime) ? firstDayOfYear : firstDayOfMonth;
		log.info("sLocalDate:{}",sLocalDate);
		log.info("eLocalDate:{}",eLocalDate);

		List<Date> firstDays = new ArrayList<>();
		LocalDate startDate = sLocalDate.toLocalDate();
		LocalDate endDate = eLocalDate.toLocalDate();
		LocalDate firstDayOf = startDate.withDayOfMonth(1);
		LocalDate lastDayOf = endDate.withDayOfMonth(1);
		for (LocalDate currentMonth = firstDayOf; !currentMonth.isAfter(lastDayOf); currentMonth = currentMonth.plusMonths(1)) {
			firstDays.add(java.sql.Date.valueOf(currentMonth));
		}
		Date sDate = java.sql.Date.valueOf(startDate);
		Date eDate = java.sql.Date.valueOf(endDate);
		Date sDateTime = Date.from(sLocalDate.atZone(ZoneId.systemDefault()).toInstant());
		Date eDateTime = Date.from(eLocalDate.atZone(ZoneId.systemDefault()).toInstant());

		List<String> energyIds = globalApiService.findSubEnergyIdsByStationIds(queryNodeId);
		energyIds.remove(queryNodeId);
		if (energyIds.isEmpty()) {
			return ResponseResult.success(vo);
		}
		String nodeOnly = energyIds.get(0);
		long energyCount = energyIds.size();
		List<RAEnergyBaseDTO> energyBaseInfos = energyBaseInfoRepository.findRAEnergyBaseDTOByNodeIds(energyIds);
//		log.info("energyBaseInfos 的值：{}",JSONObject.toJSONString(energyBaseInfos));
		Map<String, Double> energyBaseDTOMap = energyBaseInfos.stream()
				.collect(Collectors.toMap(
						RAEnergyBaseDTO::getNodeId,
						v -> v.getStorageEnergyCapacity() * v.getMaxChargePercent(),
						(existing, replacement) -> existing
				));
		Map<String, RAEnergyBaseDTO> energyBaseInfoMap = new HashMap<>();
		for (RAEnergyBaseDTO energyBaseDTO : energyBaseInfos) {
			String nodeId = energyBaseDTO.getNodeId();

			// 简化NaN判断
			if (isNaN(energyBaseDTO.getStorageEnergyCapacity())) {
				return ResponseResult.error("节点 " + nodeId + "【储能电池容量】需配置");
			}
			if (isNaN(energyBaseDTO.getMaxChargePercent())) {
				return ResponseResult.error("节点 " + nodeId + "【最大可充电量百分比】需配置");
			}
			if (isNaN(energyBaseDTO.getMinDischargePercent())) {
				return ResponseResult.error("节点 " + nodeId + "【最小放电量百分比】需配置");
			}

			energyBaseInfoMap.compute(nodeOnly, (key, existingDTO) -> {
				if (existingDTO == null) {
					return new RAEnergyBaseDTO(
							nodeOnly,
							energyBaseDTO.getStorageEnergyCapacity(),
							energyBaseDTO.getMaxChargePercent(),
							energyBaseDTO.getMinDischargePercent()
					);
				} else {
					existingDTO.setStorageEnergyCapacity(existingDTO.getStorageEnergyCapacity() + energyBaseDTO.getStorageEnergyCapacity());
					existingDTO.setMaxChargePercent(Math.min(existingDTO.getMaxChargePercent(), energyBaseDTO.getMaxChargePercent()));
					existingDTO.setMinDischargePercent(Math.min(existingDTO.getMinDischargePercent(), energyBaseDTO.getMinDischargePercent()));
					return existingDTO;
				}
			});
		}
//			List<PointModelMapping> pointModelMappings = pointModelMappingRepository.findByStationIdContainingAndPointDesc(queryNodeId, "total_load");
//			if (pointModelMappings.isEmpty() ){
//				return ResponseResult.error("请配置节点"+queryNodeId+"的实际负荷Mapping");
//			}
//			PointModelMapping mapping = pointModelMappings.get(0);
		PointModelMapping mapping = pointModelMappingRepository.findByStationIdContainingAndPointDesc(queryNodeId, "total_load");

		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
//			long startTime = System.currentTimeMillis();
		Map<Long, Double> loadMap = getDateValueFromMapping(sDateTime,eDateTime,pointService,mapping);
//			long endTime = System.currentTimeMillis();
//			System.out.println("执行时间: " + (endTime - startTime) / 1000.0 + " 秒");
//		log.info("loadMap 的值：{}",JSONObject.toJSONString(loadMap));
		if (loadMap.isEmpty()) {
			return ResponseResult.error("请配置节点"+queryNodeId+"的实际负荷Mapping");
		}

		List<DateProfitDTO> dateProfit = nodeProfitRepository.calculateTotalProfit(energyIds,sDate,eDate);
		Map<String, Double> dateProfitMap = dateProfit.stream()
				.collect(Collectors.groupingBy(
						DateProfitDTO::getDate,
						Collectors.summingDouble(DateProfitDTO::getValue)
				));
		List<RAPower96DTO> energyStrategy96 = cfgStorageEnergyStrategyPower96Repository.findRAPower96DTOByNodeIdsAndEffDate(energyIds,sDate,eDate);
		Map<String, Map<String, List<RAPower96DTO>>> energyCountMap = new HashMap<>();
		for (RAPower96DTO power96 : energyStrategy96) {
			String nodeId = power96.getNodeId();
			String effectiveDate = String.valueOf(power96.getEffectiveDate()).split(" ")[0];
			energyCountMap
					.computeIfAbsent(effectiveDate, k -> new HashMap<>())
					.computeIfAbsent(nodeId, k -> new ArrayList<>())
					.add(power96);
		}
		for (Map<String, List<RAPower96DTO>> dateMap : energyCountMap.values()) {
			for (List<RAPower96DTO> priceList : dateMap.values()) {
				priceList.sort(Comparator.comparing(RAPower96DTO::getSTime));
			}
		}
		Map<String, BigDecimal> countDynamic = new HashMap<>();
		for (Map.Entry<String, Map<String, List<RAPower96DTO>>> entry : energyCountMap.entrySet()) {
			String entryKey = entry.getKey();
			int sum = 0;
			for (List<RAPower96DTO> mapValue : entry.getValue().values()) {
				int count = 0;
				for (RAPower96DTO mv : mapValue) {
					if (count == 0 && mv.getStrategy().contains("充")) {
						count = 1;
					} else if (count == 1 && mv.getStrategy().contains("放")) {
						count = 2;
					}
					if (count == 2) {
						sum++;
						count = 0;
					}
				}
			}
			countDynamic.put(entryKey,countDynamic.getOrDefault(entryKey, BigDecimal.ZERO).add(BigDecimal.valueOf(sum)));
		}

		Map<LocalDateTime,List<RAEnergySocDTO>> energySocs = biStorageEnergyLogRepository.findRAEnergySocDTOByNodeIdsAndDateList(energyIds,firstDays)
				.stream()
				.collect(Collectors.groupingBy(RAEnergySocDTO::getTs));
//			Map<String, Map<String, Double>> socMap = new HashMap<>();
//			for(Map.Entry<LocalDateTime, List<RAEnergySocDTO>> entry :energySocs.entrySet()){
//				String dateStr = entry.getKey().toLocalDate().toString();
//				String timeStr = entry.getKey().toLocalTime().toString();
//				Double energySum = 0.0;
//				Double socEnergySum = 0.0;
//				List<RAEnergySocDTO> raEnergySocDTOS = entry.getValue();
//				for (RAEnergySocDTO energySocDTO : raEnergySocDTOS){
//					Double energy = energyBaseDTOMap.get(energySocDTO.getNodeId());
//					energySum += energy;
//					socEnergySum += energy * energySocDTO.getSoc();
//				}
//				socMap
//						.computeIfAbsent(dateStr, k1 -> new HashMap<>())
//						.put(timeStr, socEnergySum / energySum);
//			}
		Map<String, Map<String, Double>> socMap = new HashMap<>();
		for (Map.Entry<LocalDateTime, List<RAEnergySocDTO>> entry : energySocs.entrySet()) {
			String dateStr = entry.getKey().toLocalDate().toString();
			String timeStr = entry.getKey().toLocalTime().toString();
			Double energySum = 0.0;
			Double socEnergySum = 0.0;

			for (RAEnergySocDTO energySocDTO : entry.getValue()) {
				Double energy = energyBaseDTOMap.get(energySocDTO.getNodeId());
				if (energy != null) {
					energySum += energy;
					socEnergySum += energy * energySocDTO.getSoc();
				}
			}
			socMap
					.computeIfAbsent(dateStr, k1 -> new HashMap<>())
					.put(timeStr, socEnergySum / energySum);
		}

		List<RAPriceDTO> prices = electricityPriceRepository.findRAPriceDTOByNodeIdsAndDate(energyIds,sLocalDate,eLocalDate);
		Map<String, Map<String, RAPriceDTO>> priceMap = new HashMap<>();
		Map<String, List<RAPriceDTO>> pricesCountMap = new HashMap<>();
		for (RAPriceDTO price : prices) {
			String effectiveDate = price.getEffectiveDate().toString();
			String sTime = price.getSTime();
			priceMap
					.computeIfAbsent(effectiveDate, k -> new HashMap<>())
					.put(sTime, price);
			pricesCountMap
					.computeIfAbsent(effectiveDate, k -> new ArrayList<>())
					.add(price);
		}
		for (List<RAPriceDTO> dateMap : pricesCountMap.values()) {
			dateMap.sort(Comparator.comparing(RAPriceDTO::getSTime));

		}
		Map<String, BigDecimal> countFixed = new HashMap<>();
		int sum = 0;
		for (Map.Entry<String, List<RAPriceDTO>> entry : pricesCountMap.entrySet()) {
			String entryKey = entry.getKey();
			int count = 0;
			for (RAPriceDTO mv : entry.getValue()) {
				BigDecimal strategy = mv.getStrategy();
				if (count == 0 && strategy.compareTo(BigDecimal.ZERO) < 0) {
					count = 1;
				} else if (count == 1 && strategy.compareTo(BigDecimal.ZERO) > 0) {
					count = 2;
				}
				if (count == 2) {
					sum++;
					count = 0;
				}
			}
			countFixed.put(entryKey,countFixed.getOrDefault(entryKey, BigDecimal.ZERO).add(BigDecimal.valueOf(sum * energyCount)));
		}
		log.info("socMap 的值：{}",JSONObject.toJSONString(socMap));
		log.info("priceMap 的值：{}",JSONObject.toJSONString(priceMap));
		log.info("energyBaseInfoMap.get(nodeOnly) 的值：{}",JSONObject.toJSONString(energyBaseInfoMap.get(nodeOnly)));
		log.info("dateProfitMap 的值：{}",JSONObject.toJSONString(dateProfitMap));
		log.info("loadMap 的值：{}",JSONObject.toJSONString(loadMap));
		log.info("loadMap 的值：{}",JSONObject.toJSONString(loadMap));

		List<RAInfoVO> raInfoVOS = revenueAnalysisService.revenueAnalysis(socMap,priceMap,energyBaseInfoMap.get(nodeOnly),dateProfitMap,loadMap,eLocalDate)
				.stream().map(dto -> {
					RAInfoVO raInfoVO = new RAInfoVO();
					raInfoVO.setTime(dto.getTime());
					raInfoVO.setDynamic(dto.getDynamic());
					raInfoVO.setFixed(dto.getFixed());
					return raInfoVO;
				})
				.collect(Collectors.toList());
		if (queryTime.equals("year")) {
			List<RAInfoVO> monthVO = new ArrayList<>();
			List<RAInfoVO> monthCountVO = new ArrayList<>();
			Map<String, BigDecimal> monthlyDynamic = new HashMap<>();
			Map<String, BigDecimal> monthlyFixed = new HashMap<>();
			Map<String, BigDecimal> monthCountDynamic = new HashMap<>();
			Map<String, BigDecimal> monthCountFixed = new HashMap<>();
			for(RAInfoVO record : raInfoVOS) {
				String month = YearMonth.parse(record.getTime(), DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
				monthlyDynamic.put(month, monthlyDynamic.getOrDefault(month, BigDecimal.ZERO).add(record.getDynamic()));
				monthlyFixed.put(month, monthlyFixed.getOrDefault(month, BigDecimal.ZERO).add(record.getFixed()));
			}
			for(String day : countDynamic.keySet()) {
				String month = YearMonth.parse(day, DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString();
				BigDecimal dynamicValue = countDynamic.getOrDefault(day, BigDecimal.ZERO);
				BigDecimal fixedValue = countFixed.getOrDefault(day, countFixed.getOrDefault(day.substring(0, day.lastIndexOf("-")) + "-01", BigDecimal.ZERO));
				monthCountDynamic.put(month, monthCountDynamic.getOrDefault(month, BigDecimal.ZERO).add(dynamicValue));
				monthCountFixed.put(month, monthCountFixed.getOrDefault(month, BigDecimal.ZERO).add(fixedValue));
			}
			for (String month : monthlyDynamic.keySet()) {
				BigDecimal totalDynamic = monthlyDynamic.get(month);
				BigDecimal totalFixed = monthlyFixed.get(month);
				RAInfoVO end = new RAInfoVO();
				end.setTime(month);
				end.setDynamic(totalDynamic);
				end.setFixed(totalFixed);
				monthVO.add(end);
				BigDecimal totalCountDynamic = monthCountDynamic.get(month);
				BigDecimal totalCountFixed = monthCountFixed.get(month);
				RAInfoVO countEnd = new RAInfoVO();
				countEnd.setTime(month);
				countEnd.setDynamic(totalCountDynamic);
				countEnd.setFixed(totalCountFixed);
				monthCountVO.add(countEnd);
			}
			monthVO = monthVO.stream()
					.sorted(Comparator.comparing(RAInfoVO::getTime))
					.collect(Collectors.toList());
			monthCountVO = monthCountVO.stream()
					.sorted(Comparator.comparing(RAInfoVO::getTime))
					.collect(Collectors.toList());
			vo.setYield(monthVO);
			vo.setCycleCount(monthCountVO);
			return ResponseResult.success(vo);
		} else {
			List<RAInfoVO> dayCountVO = new ArrayList<>();
			for(String day : countDynamic.keySet()) {
				BigDecimal totalDynamic = countDynamic.get(day);
				BigDecimal totalFixed;
				if (countFixed.get(day) == null ) {
					totalFixed = countFixed.get(day.substring(0, day.lastIndexOf("-")) + "-01");
				} else {
					totalFixed = countFixed.get(day);
				}
				RAInfoVO end = new RAInfoVO();
				end.setTime(day);
				end.setDynamic(totalDynamic);
				end.setFixed(totalFixed);
				dayCountVO.add(end);
			}
			raInfoVOS = raInfoVOS.stream()
					.sorted(Comparator.comparing(RAInfoVO::getTime))
					.collect(Collectors.toList());
			dayCountVO = dayCountVO.stream()
					.sorted(Comparator.comparing(RAInfoVO::getTime))
					.collect(Collectors.toList());
			vo.setYield(raInfoVOS);
			vo.setCycleCount(dayCountVO);
			log.info("vo 的值:{}",JSONObject.toJSONString(vo));
			return ResponseResult.success(vo);
		}
//		} catch (Exception e) {
//			return ResponseResult.error("请检查映射关系");
//
//		}

	}

	@ApiOperation("智能调度负荷/碳减排量/顶峰能力（kW）")
	@RequestMapping(value = "getUseEnergyCount", method = {RequestMethod.GET})
	public ResponseResult<UseEnergyModel> getUseEnergyCount() {
		try {


			SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = fmt_ymd.parse(fmt_ymd.format(new Date()));

			UseEnergyModel useEnergyModel = new UseEnergyModel();
			double total充电桩系统额定功率 = (double) 0;
			double total空调系统额定功率 = (double) 0;
			double total储能电站功率 = (double) 0;
			double total光伏发电装机容量 = (double) 0;

			double cdzRatedPower = (double) 0;
			double ktRatedPower = (double) 0;
			double storageEnergyRatedPower = (double) 0;
			double pvRatedPower = (double) 0;


			double total负荷节点可调负荷 = (double) 0;
			double 碳排放因子 = (double) 0;
			double 光伏累计发电量 = 0;


			try {
				total充电桩系统额定功率 = deviceRepository.findAllKWByContainSystemName("%充电桩%");
			} catch (Exception ex) {
			}
			try {
				total空调系统额定功率 = deviceRepository.findAllKWByContainSystemName("%空调%");
			} catch (Exception ex) {
			}
			try {
				total储能电站功率 = energyBaseInfoRepository.sumStorageEnergyPowers();
			} catch (Exception ex) {
			}
			try {
				total光伏发电装机容量 = photovoltaicBaseInfoRepository.sumPhotovoltaicPowers();
			} catch (Exception ex) {
			}
			try {
				total负荷节点可调负荷 = deviceRepository.findAllLoadKW();
			} catch (Exception ex) {
			}
			try {
				//碳减排量（t）
				List<CaEmissionFactor> caEmissionFactors = caEmissionFactorRepository.findAll();
				if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
					caEmissionFactors = caEmissionFactors.stream().filter(p -> p.getProvince().equals("浙江省") && p.getEmissionFactorName().equals(
							"外购电力") && p.getSStatus() == 1).collect(Collectors.toList());
					if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
						CaEmissionFactor caEmissionFactor = caEmissionFactors.get(0);
						碳排放因子 = caEmissionFactor == null ? (double) 0 : caEmissionFactor.getCo2();
					}
				}
			} catch (Exception ex) {
			}
			try {
				//顶峰能力参数配置
				SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.PeakCapacityParamCfg.getId());
				if (sysParam != null) {
					JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
					if (obj != null) {
						if (obj.get("pvRatedPower") != null) {
							pvRatedPower = Double.parseDouble(obj.get("pvRatedPower").toString()) * 0.01;
						}
						if (obj.get("storageEnergyRatedPower") != null) {
							storageEnergyRatedPower = Double.parseDouble(obj.get("storageEnergyRatedPower").toString()) * 0.01;
						}
						if (obj.get("cdzRatedPower") != null) {
							cdzRatedPower = (Double.parseDouble(obj.get("cdzRatedPower").toString())) * 0.01;
						}
						if (obj.get("ktRatedPower") != null) {
							ktRatedPower = Double.parseDouble(obj.get("ktRatedPower").toString()) * 0.01;
						}
					}
				}
			} catch (Exception ex) {
			}
			//负荷调度统计
			//顶峰能力（kW)
			useEnergyModel.setPeakCapacity(Double.parseDouble(String.format("%.2f",
					total充电桩系统额定功率 * cdzRatedPower + total空调系统额定功率 * ktRatedPower + total储能电站功率 * storageEnergyRatedPower + total光伏发电装机容量 * pvRatedPower)));
			//智能调度负荷（kW）
			useEnergyModel.setIntelligentScheduling(Double.parseDouble(String.format("%.2f", total储能电站功率 + total光伏发电装机容量 + total负荷节点可调负荷)));

			try {
				光伏累计发电量 = iotTsKvMeteringDevice96Repository.findALLPVNodeTotalEnergyCountByDate(dt);
			} catch (Exception ex) {
			}
			useEnergyModel.setCarbonEmissionReduction(Double.parseDouble(String.format("%.2f", (光伏累计发电量 * 碳排放因子) / 1000)));

			return ResponseResult.success(useEnergyModel);
		} catch (Exception e) {
			return ResponseResult.success(null);
		}
	}

	@ApiOperation("当日资源情况-节点信息分类统计")
	@RequestMapping(value = "getNodeInfoCount", method = {RequestMethod.GET})
	public ResponseResult<NodeInfoModel> getNodeInfoCount() {
		try {

			NodeInfoModel useEnergyModel = new NodeInfoModel();

			ExecutorService executorService = Executors.newFixedThreadPool(10);

			//储能电站功率
			double total_power_en = 0;
			//光伏发电装机容量
			double total_power_pv = 0;
			//储能电站容量
			double total_vol_en = 0;
			//负荷节点可调负荷
			double total_load_variable = 0;
			//负荷节点接入负荷
			double total_load_access = 0;

			int energy_count = 0;
			int pv_count = 0;
			int load_count;

			Future<Double> total_power_en_future = ConcurrentUtils.doJob(executorService, energyBaseInfoRepository::sumStorageEnergyPowers);
			Future<Double> total_power_pv_future = ConcurrentUtils.doJob(executorService, photovoltaicBaseInfoRepository::sumPhotovoltaicPowers);
			Future<Double> total_vol_en_future = ConcurrentUtils.doJob(executorService, energyBaseInfoRepository::sumStorageEnergyCapacity);
			Future<Double> total_load_variable_future = ConcurrentUtils.doJob(executorService, deviceRepository::findAllLoadKW);
			//当日资源情况
			Future<List<Node>> energy_count_future = ConcurrentUtils.doJob(executorService,
					() -> nodeRepository.findAllByNodePostType(NodePostTypeEnum.storageEnergy.getNodePostType()));
			Future<List<Node>> pv_count_future = ConcurrentUtils.doJob(executorService,
					() -> nodeRepository.findAllByNodePostType(NodePostTypeEnum.pv.getNodePostType()));
			Future<List<Node>> load_items_future = ConcurrentUtils.doJob(executorService,
					() -> nodeRepository.findAllByNodePostType(NodePostTypeEnum.load.getNodePostType()));


			try {
				total_power_en = ConcurrentUtils.futureGet(total_power_en_future);
			} catch (Exception ignore) {
			}
			try {
				total_power_pv = ConcurrentUtils.futureGet(total_power_pv_future);
			} catch (Exception ignore) {
			}
			try {
				total_vol_en = ConcurrentUtils.futureGet(total_vol_en_future);
			} catch (Exception ignore) {
			}
			try {
				total_load_variable = ConcurrentUtils.futureGet(total_load_variable_future);
			} catch (Exception ignore) {
			}
			try {
				energy_count = ConcurrentUtils.futureGet(energy_count_future).size();
			} catch (Exception ignore) {
			}

			try {
				pv_count = ConcurrentUtils.futureGet(pv_count_future).size();
			} catch (Exception ignore) {
			}

			List<Node> nodeItems = new ArrayList<>();
			try {
				nodeItems = ConcurrentUtils.futureGet(load_items_future);
			} catch (Exception ignore) {
			}

			useEnergyModel.setStorageEnergyCount(energy_count);
			useEnergyModel.setStorageEnergyCapacity(Double.parseDouble(String.format("%.2f", total_vol_en)));
			useEnergyModel.setStorageEnergyPower(Double.parseDouble(String.format("%.2f", total_power_en)));

			useEnergyModel.setPvCapacity(Double.parseDouble(String.format("%.2f", total_power_pv)));
			useEnergyModel.setPvCount(pv_count);


			load_count = nodeItems.size();
			//可调负荷：负荷节点所有系统中设备的额定功率之和
			useEnergyModel.setLoadKeTiao(Double.parseDouble(String.format("%.2f", total_load_variable)));
			//接入负荷：负荷节点的能源总表对应的负荷
			List<IotTsKvLast> jieru_kw_objects =
					iotTsKvLastRepository.findLatestPointValue(nodeItems.stream().map(Node::getNodeId).collect(Collectors.toList()),
							"nengyuanzongbiao", "load", "MSG");
			if (jieru_kw_objects != null && jieru_kw_objects.size() > 0) {
				total_load_access = jieru_kw_objects.stream().mapToDouble(c -> Double.parseDouble(StringUtils.isNotEmpty(c.getPointValue()) ?
						c.getPointValue() : "0")).sum();
			}
			useEnergyModel.setLoadJieRu(Double.parseDouble(String.format("%.2f", total_load_access)));
			useEnergyModel.setLoadCount(load_count);
			executorService.shutdown();
			return ResponseResult.success(useEnergyModel);
		} catch (Exception e) {
			log.error("query error", e);
			return ResponseResult.success(null);
		}
	}

	/**
	 * a)发电 （kWh）：今天0点到进入页面的时刻累计发电量
	 * b)用电（kWh）：每个接入的负荷节点对应的用电量之和
	 * c)碳排放（t）: 统计当日碳排放数据 ，即用电（kWh）对应的碳排放
	 */
	@ApiOperation("今日发电及用能统计-发电/用电/碳排放")
	@RequestMapping(value = "getTodayElectricityAndUseEnergyCount", method = {RequestMethod.GET})
	public ResponseResult<TodayElectricityModel> getTodayElectricityAndUseEnergyCount() {
		try {


			SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
			Date dt = fmt_ymd.parse(fmt_ymd.format(new Date()));


			double 碳排放因子 = (double) 0;
			double 今日发电 = 0;
			double 今日用电 = 0;

			//碳减排量（t）
			List<CaEmissionFactor> caEmissionFactors = caEmissionFactorRepository.findAll();
			if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
				caEmissionFactors =
						caEmissionFactors.stream().filter(p -> p.getProvince().equals("浙江省") && p.getEmissionFactorName().equals("外购电力") && p.getSStatus() == 1).collect(Collectors.toList());
				if (caEmissionFactors != null && caEmissionFactors.size() > 0) {
					CaEmissionFactor caEmissionFactor = caEmissionFactors.get(0);
					碳排放因子 = caEmissionFactor == null ? (double) 0 : caEmissionFactor.getCo2();
				}
			}
			//今日发电及用能统计
			try {
				今日发电 = iotTsKvMeteringDevice96Repository.findALLPVNodeEnergyCountByDate(dt);
			} catch (Exception ex) {
			}

			try {
				今日用电 = iotTsKvMeteringDevice96Repository.findALLLoadNodeEnergyCountByDate(dt);
			} catch (Exception ex) {
			}
			//负荷节点的能源总表对应的energy
			double 今日发电及用能统计_碳排放 = 今日用电 * 碳排放因子;

			TodayElectricityModel model = new TodayElectricityModel();
			model.setGenerateElectricity(Double.parseDouble(String.format("%.2f", 今日发电)));
			model.setEnergyConsumption(Double.parseDouble(String.format("%.2f", 今日用电)));
			model.setCarbonEmission(Double.parseDouble(String.format("%.2f", 今日发电及用能统计_碳排放 / 1000)));
			return ResponseResult.success(model);
		} catch (Exception e) {
			return ResponseResult.success(null);
		}
	}

	@ApiOperation("用电同比分析")
	@RequestMapping(value = "getUseEnergyM2MCount", method = {RequestMethod.GET})
	public ResponseResult<M2MModel> getUseEnergyM2MCount() {
		try {


			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// 国内时区是GMT+8
			fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			Date same_month_e_dt = fmt.parse(fmt.format(new Date()));
			Date same_month_s_dt = fmt.parse(fmt.format(TimeUtil.getMonthStart(same_month_e_dt)));

			Date last_month_e_dt = fmt.parse(fmt.format(TimeUtil.dateAddMonths(same_month_e_dt, -1)));
			Date last_month_s_dt = fmt.parse(fmt.format(TimeUtil.getMonthStart(last_month_e_dt)));


			M2MModel m2MModel = new M2MModel();

			//当月用电
			double theMonthUsingTheTotalPower = iotTsKvMeteringDevice96Repository.findALLLoadNodeEnergyCountByDateBetween(same_month_s_dt,
					same_month_e_dt);
			//上月同期
			double samePeriodLastMonth = iotTsKvMeteringDevice96Repository.findALLLoadNodeEnergyCountByDateBetween(last_month_s_dt, last_month_e_dt);

			double a = Double.parseDouble(String.format("%.2f", theMonthUsingTheTotalPower / 10000));

			m2MModel.setTheSameMonthEnergy(a);

			double b = Double.parseDouble(String.format("%.2f", samePeriodLastMonth / 10000));

			m2MModel.setTheLastMonthEnergy(b);

			m2MModel.setM2mEnergy(b == 0 ? "-" : String.format("%.2f", ((a - b) / b) * 100));
			return ResponseResult.success(m2MModel);
		} catch (Exception e) {
			return ResponseResult.success(null);
		}

	}

	@ApiOperation("逐日坪效")
	@RequestMapping(value = "getSalesPerSquareMeterList", method = {RequestMethod.GET})
	public ResponseResult<List<SalesPerSquareMeterModel>> getSalesPerSquareMeterList(@RequestParam("salesPerSquareMeter") String salesPerSquareMeter) {
		try {
			if ("commercialComplex".equals(salesPerSquareMeter) == false && "governmentOfficeGreaterThan20000".equals(salesPerSquareMeter) == false && "governmentOfficeLessThan20000".equals(salesPerSquareMeter) == false) {

				return ResponseResult.error("逐日坪效参数有误，请修改!");
			}
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			// 国内时区是GMT+8
			fmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			SimpleDateFormat fmt_y = new SimpleDateFormat("yyyy");
			fmt_y.setTimeZone(TimeZone.getTimeZone("GMT+8"));

			List<SalesPerSquareMeterModel> salesPerSquareMeterModels = new ArrayList<>();

			SysParam sysParam = sysParamRepository.findSysParamBySysParamKey(SysParamEnum.SalesPerSquareMeter.getId());
			if (sysParam != null) {
				JSONObject obj = JSONObject.parseObject(sysParam.getSysParamValue());
				switch (salesPerSquareMeter) {
					case "commercialComplex":
						double 标准坪效Complex = (double) 0;
						if (obj != null && obj.get("commercialComplex") != null && StringUtils.isNotEmpty(obj.get("commercialComplex").toString())) {
							标准坪效Complex = Double.parseDouble(obj.get("commercialComplex").toString());
						}
						List<SalesPerSquaremeterCommercialcomplexView> commercialcomplexViews =
								commercialcomplexViewRepository.findAllByOrderByCountDate();
						if (commercialcomplexViews != null && commercialcomplexViews.size() > 0) {
							double final标准坪效 = 标准坪效Complex;
							commercialcomplexViews.stream().forEach(p -> {
								try {
									Date dt = fmt.parse(fmt.format(p.getCountDate()));
									Date dt_ = fmt.parse(fmt.format(new Date()));
									if (dt.after(dt_) == false) {

										SalesPerSquareMeterModel model = new SalesPerSquareMeterModel();
										model.setTs(dt);
										model.setRealTimeSalesPerSquareMeter(0);
										model.setStandardSalesPerSquareMeter(final标准坪效);

										Date yesterdayDt = fmt.parse(fmt.format(TimeUtil.dateAddDay(dt, -1)));
										Date theBeginningOfTheYearDt =
												fmt_y.parse(fmt_y.format(TimeUtil.getYearFirstDay(Integer.parseInt(fmt_y.format(dt)))));

										List<SalesPerSquaremeterCommercialcomplexView> sales =
												commercialcomplexViews.stream().filter(c -> c.getCountDate().getTime() <= yesterdayDt.getTime() && c.getCountDate().getTime() >= theBeginningOfTheYearDt.getTime()).collect(Collectors.toList());

										double nodeArea = p.getNodeArea();
										double hTotalUsePower = (sales == null || sales.size() == 0) ? (double) 0 :
												sales.stream().mapToDouble(c -> c.getHTotalUse()).sum();
										int dayNumber = sales == null ? 0 : sales.size();
										double standardSales = nodeArea <= 0 ? 0 : (hTotalUsePower / nodeArea);
										double nianZhanBi = (dayNumber <= 0) ? 0 : (standardSales * 365) / dayNumber;

										model.setAnnualAccumulationSalesPerSquareMeter(nianZhanBi);

										salesPerSquareMeterModels.add(model);
									}
								} catch (Exception e) {
								}
							});
						}
						break;
					case "governmentOfficeGreaterThan20000":
						double 标准坪效GreaterThan20000 = (double) 0;
						if (obj != null && obj.get("governmentOfficeGreaterThan20000") != null && StringUtils.isNotEmpty(obj.get(
								"governmentOfficeGreaterThan20000").toString())) {
							标准坪效GreaterThan20000 = Double.parseDouble(obj.get("governmentOfficeGreaterThan20000").toString());
						}
						List<SalesPerSquaremeterGovernmentOfficeGreaterThan20000View> greaterThan20000Views =
								governmentOfficeGreaterThan20000ViewRepository.findAllByOrderByCountDate();
						if (greaterThan20000Views != null && greaterThan20000Views.size() > 0) {
							double final标准坪效 = 标准坪效GreaterThan20000;
							greaterThan20000Views.stream().forEach(p -> {
								try {
									Date dt = fmt.parse(fmt.format(p.getCountDate()));
									Date dt_ = fmt.parse(fmt.format(new Date()));
									if (dt.after(dt_) == false) {

										SalesPerSquareMeterModel model = new SalesPerSquareMeterModel();
										model.setTs(dt);
										model.setRealTimeSalesPerSquareMeter(0);
										model.setStandardSalesPerSquareMeter(final标准坪效);

										Date yesterdayDt = fmt.parse(fmt.format(TimeUtil.dateAddDay(dt, -1)));
										Date theBeginningOfTheYearDt =
												fmt_y.parse(fmt_y.format(TimeUtil.getYearFirstDay(Integer.parseInt(fmt_y.format(dt)))));

										List<SalesPerSquaremeterGovernmentOfficeGreaterThan20000View> sales =
												greaterThan20000Views.stream().filter(c -> c.getCountDate().getTime() <= yesterdayDt.getTime() && c.getCountDate().getTime() >= theBeginningOfTheYearDt.getTime()).collect(Collectors.toList());

										double nodeArea = p.getNodeArea();
										double hTotalUsePower = (sales == null || sales.size() == 0) ? (double) 0 :
												sales.stream().mapToDouble(c -> c.getHTotalUse()).sum();
										int dayNumber = sales == null ? 0 : sales.size();
										double standardSales = nodeArea <= 0 ? 0 : (hTotalUsePower / nodeArea);
										double nianZhanBi = (dayNumber <= 0) ? 0 : (standardSales * 365) / dayNumber;

										model.setAnnualAccumulationSalesPerSquareMeter(nianZhanBi);

										salesPerSquareMeterModels.add(model);
									}
								} catch (Exception e) {
								}
							});
						}
						break;
					case "governmentOfficeLessThan20000":
						double 标准坪效LessThan20000 = (double) 0;
						if (obj != null && obj.get("governmentOfficeLessThan20000") != null && StringUtils.isNotEmpty(obj.get(
								"governmentOfficeLessThan20000").toString())) {
							标准坪效LessThan20000 = Double.parseDouble(obj.get("governmentOfficeLessThan20000").toString());
						}
						List<SalesPerSquaremeterGovernmentOfficeLessThan20000View> lessThan20000Views =
								governmentOfficeLessThan20000ViewRepository.findAllByOrderByCountDate();
						if (lessThan20000Views != null && lessThan20000Views.size() > 0) {
							double final标准坪效 = 标准坪效LessThan20000;
							lessThan20000Views.stream().forEach(p -> {
								try {
									Date dt = fmt.parse(fmt.format(p.getCountDate()));
									Date dt_ = fmt.parse(fmt.format(new Date()));
									if (dt.after(dt_) == false) {

										SalesPerSquareMeterModel model = new SalesPerSquareMeterModel();
										model.setTs(dt);
										model.setRealTimeSalesPerSquareMeter(0);
										model.setStandardSalesPerSquareMeter(final标准坪效);

										Date yesterdayDt = fmt.parse(fmt.format(TimeUtil.dateAddDay(dt, -1)));
										Date theBeginningOfTheYearDt =
												fmt_y.parse(fmt_y.format(TimeUtil.getYearFirstDay(Integer.parseInt(fmt_y.format(dt)))));

										List<SalesPerSquaremeterGovernmentOfficeLessThan20000View> sales =
												lessThan20000Views.stream().filter(c -> c.getCountDate().getTime() <= yesterdayDt.getTime() && c.getCountDate().getTime() >= theBeginningOfTheYearDt.getTime()).collect(Collectors.toList());

										double nodeArea = p.getNodeArea();
										double hTotalUsePower = (sales == null || sales.size() == 0) ? (double) 0 :
												sales.stream().mapToDouble(c -> c.getHTotalUse()).sum();
										int dayNumber = sales == null ? 0 : sales.size();
										double standardSales = nodeArea <= 0 ? 0 : (hTotalUsePower / nodeArea);
										double nianZhanBi = (dayNumber <= 0) ? 0 : (standardSales * 365) / dayNumber;

										model.setAnnualAccumulationSalesPerSquareMeter(nianZhanBi);

										salesPerSquareMeterModels.add(model);
									}
								} catch (Exception e) {
								}
							});
						}
						break;
				}
			}
			List<SalesPerSquareMeterModel> items =
					salesPerSquareMeterModels.stream().sorted(Comparator.comparing(p -> p.getTs())).collect(Collectors.toList());

			return ResponseResult.success(items);
		} catch (Exception e) {
			return ResponseResult.success(null);
		}
	}


	@Resource
	DeviceInfoViewRepository deviceInfoViewRepository;

	@ApiOperation("设备调节能力-空调")
	@RequestMapping(value = "getAirConditioning", method = {RequestMethod.GET})
	public ResponseResult<DeviceAdjustmentModel> getAirConditioning() {
		double value = 0d;
		try {
			value = deviceInfoViewRepository.getAirConditioning();
		} catch (Exception ex) {
		}
		DeviceAdjustmentModel vm = new DeviceAdjustmentModel();
		vm.setDeviceKey("air_conditioning");
		vm.setDeviceKeyDesc("空调");
		vm.setDeviceValue(Double.parseDouble(String.format("%.2f", value)));
		return ResponseResult.success(vm);
	}

	@ApiOperation("设备调节能力-照明")
	@RequestMapping(value = "getLighting", method = {RequestMethod.GET})
	public ResponseResult<DeviceAdjustmentModel> getLighting() {
		double value = 0d;
		try {
			value = deviceInfoViewRepository.getLighting();
		} catch (Exception ex) {
		}
		DeviceAdjustmentModel vm = new DeviceAdjustmentModel();
		vm.setDeviceKey("lighting");
		vm.setDeviceKeyDesc("照明");
		vm.setDeviceValue(Double.parseDouble(String.format("%.2f", value)));
		return ResponseResult.success(vm);
	}

	@ApiOperation("设备调节能力-充电桩")
	@RequestMapping(value = "getChargingPiles", method = {RequestMethod.GET})
	public ResponseResult<DeviceAdjustmentModel> getChargingPiles() {
		double value = 0d;
		try {
			value = deviceInfoViewRepository.getChargingPiles();
		} catch (Exception ex) {
		}
		DeviceAdjustmentModel vm = new DeviceAdjustmentModel();
		vm.setDeviceKey("charging_piles");
		vm.setDeviceKeyDesc("充电桩");
		vm.setDeviceValue(Double.parseDouble(String.format("%.2f", value)));
		return ResponseResult.success(vm);
	}

	@ApiOperation("设备调节能力-其它")
	@RequestMapping(value = "getOthers", method = {RequestMethod.GET})
	public ResponseResult<DeviceAdjustmentModel> getOthers() {
		double value = 0d;
		try {
			value = deviceInfoViewRepository.getOthers();
		} catch (Exception ex) {
		}
		DeviceAdjustmentModel vm = new DeviceAdjustmentModel();
		vm.setDeviceKey("others");
		vm.setDeviceKeyDesc("其他");
		vm.setDeviceValue(Double.parseDouble(String.format("%.2f", value)));
		return ResponseResult.success(vm);
	}

	public ResponseResult<List<CopilotBlockResponse>> energyBlockTrendByNodeIds(@RequestBody EnergyBlockTrendCommand request) throws ParseException {

		List<CopilotBlockResponse> res = new ArrayList<>();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(request.getStartDate());
		Date endDate = sdf.parse(request.getEndDate());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		// todo 需要修改，从项目ID查询系统ID
		List<StationNode> nodes = stationNodeRepository.findAllByStationType("光伏电站");
		nodes.removeIf(node -> !request.getNodeId().contains(node.getStationId()));
		nodes.forEach(o -> {
			Map<Date, Double> pvMap = iotTsKvMeteringDevice96Repository.findAllByNodeIdAndPointDescAndCountDataTimeBetween(o.getStationId(), "load",
					startDate,
					calendar.getTime()).stream().filter(device96 -> device96.getCountDataTime() != null && device96.getHTotalUse() != null).collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse));
			try {
				CopilotBlockResponse pv = new CopilotBlockResponse(o.getStationName() + "实际功率", true,
						generateResponses(startDate, calendar.getTime()).stream().peek(copilotResponse -> {
							Double value = pvMap.get(copilotResponse.getDate());
							if (value != null) {
								value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
							}
							copilotResponse.setValue(value);
						}).collect(Collectors.toList()));
				res.add(pv);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		});


		return ResponseResult.success(res);


	}

	@ApiOperation("首页能量块趋势图")
	@UserLoginToken
	@RequestMapping(value = "energyBlockTrendOld", method = {RequestMethod.POST})
	public ResponseResult<List<CopilotBlockResponse>> energyBlockTrendOld(@RequestBody EnergyBlockTrendCommand request) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startDate = sdf.parse(request.getStartDate());
		Date endDate = sdf.parse(request.getEndDate());

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		//光伏实际
		List<CopilotResponse> pvReal1list = generateResponses(startDate, calendar.getTime());
		Map<Date, Double> pvReal1Map = iotTsKvMeteringDevice96Repository.findAllBySystemIdAndNodeIde(config.getPvNode1(), "nengyuanzongbiao", "JL" +
						"-001-load", "load", startDate, calendar.getTime())
				.stream().filter(device -> device.getCountDataTime() != null && device.getHTotalUse() != null)
				.collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse,
						(existing, replacement) -> replacement));
		CopilotBlockResponse pvReal1 = new CopilotBlockResponse("光伏001实际功率", true,
				pvReal1list.stream().peek(response -> {
					Double value = pvReal1Map.get(response.getDate());
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));

		//光伏预测
		List<CopilotResponse> pvFore1list = generateResponses(startDate, calendar.getTime());
		Map<Date, String> pvFore1Map = aiLoadRepository.findByDateNodeIdSystemId(config.getPvNode1(), "nengyuanzongbiao", startDate,
						calendar.getTime())
				.stream().filter(device -> device.getCountDataTime() != null && device.getPredictValue() != null)
				.collect(Collectors.toMap(AiLoadForecasting::getCountDataTime, AiLoadForecasting::getPredictValue,
						(existing, replacement) -> replacement));
		CopilotBlockResponse pvFore1 = new CopilotBlockResponse("光伏001预测功率", true,
				pvFore1list.stream().peek(response -> {
					Double value = pvFore1Map.get(response.getDate()) == null ? null : Double.valueOf(pvFore1Map.get(response.getDate()));
					if (value != null) {
						value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
					}
					response.setValue(value);
				}).collect(Collectors.toList()));
		//储能实际
		Map<Date, Double> energyReal1Map = iotTsKvMeteringDevice96Repository.findAllBySystemIdAndNodeIde(config.getEnergyNode1(), "chuneng",
						"load001", "load", startDate, calendar.getTime())
				.stream().filter(device -> device.getCountDataTime() != null && device.getHTotalUse() != null)
				.collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse,
						(existing, replacement) -> replacement));

		Map<Date, Double> energyReal2Map = iotTsKvMeteringDevice96Repository.findAllBySystemIdAndNodeIde(config.getEnergyNode2(), "chuneng",
						"load002", "load", startDate, calendar.getTime())
				.stream().filter(device -> device.getCountDataTime() != null && device.getHTotalUse() != null)
				.collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse,
						(existing, replacement) -> replacement));

		//关口表实际
		List<CopilotResponse> gateReal1list = generateResponses(startDate, calendar.getTime());
		Map<Date, Double> gateReal1Map = iotTsKvMeteringDevice96Repository.findAllBySystemIdAndNodeIde(config.getLoadNode1(), "nengyuanzongbiao",
				"GKB-load", "load", startDate, calendar.getTime()).stream().filter(device -> device.getCountDataTime() != null && device.getHTotalUse() != null).collect(Collectors.toMap(IotTsKvMeteringDevice96::getCountDataTime, IotTsKvMeteringDevice96::getHTotalUse, (existing, replacement) -> replacement));
		CopilotBlockResponse gateReal1 = new CopilotBlockResponse("实际负荷", true, gateReal1list.stream().peek(response -> {
			Double gateValue = gateReal1Map.get(response.getDate());
			Double energy1Value = energyReal1Map.get(response.getDate());
			Double energy2Value = energyReal2Map.get(response.getDate());
			if (gateValue == null && energy1Value == null && energy2Value == null) {
				response.setValue(null);
			} else {
				Double finalGateValue = Optional.ofNullable(gateValue).orElse(0.0);
				Double finalEnergy1Value = Optional.ofNullable(energy1Value).orElse(0.0);
				Double finalEnergy2Value = Optional.ofNullable(energy2Value).orElse(0.0);
				Double value = finalGateValue + finalEnergy1Value + finalEnergy2Value;
//				log.info("response.getDate():{}finalGateValue:{},finalEnergy1Value:{},finalEnergy2Value:{},value:{}",
//						response.getDate(),finalGateValue,finalEnergy1Value,finalEnergy2Value,value);
				value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;

				response.setValue(value);
			}
		}).collect(Collectors.toList()));

		//关口表预测
		List<CopilotResponse> gateFore1list = generateResponses(startDate, calendar.getTime());
		Map<Date, String> gateFore1Map = aiLoadRepository.findByDateNodeIdSystemId(config.getLoadNode1(), "nengyuanzongbiao", startDate,
				calendar.getTime()).stream().filter(device -> device.getCountDataTime() != null && device.getPredictValue() != null).collect(Collectors.toMap(AiLoadForecasting::getCountDataTime, AiLoadForecasting::getPredictValue, (existing, replacement) -> replacement));
		CopilotBlockResponse gateFore1 = new CopilotBlockResponse("预测负荷", true, gateFore1list.stream().peek(response -> {
			Double value = gateFore1Map.get(response.getDate()) == null ? null : Double.valueOf(gateFore1Map.get(response.getDate()));
			if (value != null) {
				value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
			}
			response.setValue(value);
		}).collect(Collectors.toList()));

		List<CopilotBlockResponse> copilotResponseList = new ArrayList<>();
		copilotResponseList.add(gateReal1);
		copilotResponseList.add(gateFore1);
		copilotResponseList.add(pvReal1);
		copilotResponseList.add(pvFore1);

		return ResponseResult.success(copilotResponseList);
	}

	@ApiOperation("首页能量块趋势图")
	@UserLoginToken
	@RequestMapping(value = "energyBlockTrend", method = {RequestMethod.POST})
	public ResponseResult<List<CopilotBlockResponse>> energyBlockTrend(@RequestBody EnergyBlockTrendCommand request) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(request.getStartDate());
		Date endDate = sdf.parse(request.getEndDate());
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(endDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		List<CopilotBlockResponse> copilotResponseList = new ArrayList<>();


		PointModelMappingRepository modelMappingRepository = SpringBeanHelper.getBeanOrThrow(PointModelMappingRepository.class);

		PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
		List<StationNode> list = pointService.findAllNodesByStationNode(request.getNodeId());
		List<StationNode> pvList = list.stream().filter(stationNode -> stationNode.getSystemIds().contains("guangfu")).collect(Collectors.toList());

		List<PointModelMapping> mapping = modelMappingRepository.findAllByStation_StationIdAndPointModel_PointDesc(request.getNodeId(),
				"total_load");
		getValueFromMapping(startDate, calendar, copilotResponseList, pointService, mapping, "实际负荷","load");

		mapping = modelMappingRepository.findAllByStation_StationIdAndPointModel_PointDesc(request.getNodeId(),
				"predict_value");
		getValueFromMapping(startDate, calendar, copilotResponseList, pointService, mapping, "预测负荷","load");


		for (StationNode stationNode : pvList) {
			List<PointModelMapping> pvMapping = modelMappingRepository.findAllByStation_StationIdAndPointModel_PointDesc(stationNode.getStationId(),
					"load");
			getValueFromMapping(startDate, calendar, copilotResponseList, pointService, pvMapping, "实际功率","pv");

			pvMapping = modelMappingRepository.findAllByStation_StationIdAndPointModel_PointDesc(stationNode.getStationId(),
					"predict_value");
			getValueFromMapping(startDate, calendar, copilotResponseList, pointService, pvMapping, "预测功率","pv");
		}

		return ResponseResult.success(copilotResponseList);
	}

	private void getValueFromMapping(Date startDate, Calendar calendar, List<CopilotBlockResponse> copilotResponseList, PointService pointService,
									 List<PointModelMapping> mapping, String name, String type) throws ParseException {
		if (!mapping.isEmpty()) {
			Map<Date, ?> res = pointService.getDValuesByTime(mapping.get(0).getMappingId(), startDate, calendar.getTime());
			List<CopilotResponse> pvReal1list = generateResponses(startDate, calendar.getTime());
			CopilotBlockResponse real = new CopilotBlockResponse(mapping.get(0).getStation().getStationName() + name, true,
					pvReal1list.stream().peek(response -> {
						Double value = (Double) res.get(response.getDate());
						if (value != null) {
							value = value >= 0 ? Double.valueOf(String.format("%.2f", value)) : null;
						}
						response.setValue(value);
					}).collect(Collectors.toList()));
			real.setType(type);
			copilotResponseList.add(real);
		}
	}

	private Map<Long, Double> getDateValueFromMapping(Date startDate, Date endDate, PointService pointService,
													  PointModelMapping mapping)  {
		return pointService.getLDValuesByTime(mapping, startDate, endDate);
	}
	private Map<Long, Double> getDateValueFromMappingTest(Date startDate, Date endDate, PointService pointService,
														  PointModelMapping mapping) throws ParseException {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		startDate = sdf.parse(sdf.format(startDate));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DAY_OF_MONTH, 1);
		Map<Date, ?> tempRes = pointService.getDValuesByTime(mapping.getMappingId(), startDate, calendar.getTime());
		Map<Long, Double> res = new HashMap<>();
		for (Map.Entry<Date, ?> entry : tempRes.entrySet()) {
			res.put(entry.getKey().getTime(), (Double) entry.getValue());
		}
		return res;
	}

	public static List<CopilotResponse> generateResponses(Date startDate, Date endDate) throws ParseException {
		List<CopilotResponse> responses = new ArrayList<>();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);

		while (!calendar.getTime().equals(endDate)) {
			responses.add(new CopilotResponse(sdf.parse(sdf.format(calendar.getTime())), null));
			calendar.add(Calendar.MINUTE, 15);
		}

		return responses;
	}

}