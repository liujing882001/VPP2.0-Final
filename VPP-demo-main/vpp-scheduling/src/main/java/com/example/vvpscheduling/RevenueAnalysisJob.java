package com.example.vvpscheduling;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.SpringBeanHelper;
import com.example.vvpdomain.*;
import com.example.vvpdomain.dto.*;
import com.example.vvpdomain.entity.PointModelMapping;
import com.example.vvpdomain.entity.RevenueAnalysis;
import com.example.vvpscheduling.vo.RAInfoVO;
import com.example.vvpservice.globalapi.service.GlobalApiService;
import com.example.vvpservice.point.service.PointService;
import com.example.vvpservice.revenue.RevenueAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.lang.Double.isNaN;

@Slf4j
@Component
@EnableAsync
public class RevenueAnalysisJob {

    @Resource
    private CfgStorageEnergyBaseInfoRepository energyBaseInfoRepository;
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

    @Scheduled(cron = "12 0 0 * * ?")
//    @Async
    public void revenueAnalysis() {
//        try {
            List<String> stations = globalApiService.findAllStationId();
            log.info("开始收益分析:{}",stations.size());
            if (!stations.isEmpty()) {
                LocalDateTime eLocalDate = LocalDateTime.now();
//		LocalDateTime eLocalDate = LocalDateTime.of(2025, 1, 4, 0, 0, 0, 0);
//                LocalDateTime firstDayOfMonth = eLocalDate.withDayOfMonth(1).toLocalDate().atStartOfDay();
//                LocalDateTime firstDayOfYear = eLocalDate.withDayOfYear(1).toLocalDate().atStartOfDay();
//                LocalDateTime sLocalDate = "year".equals(queryTime) ? firstDayOfYear : firstDayOfMonth;
                LocalDateTime sLocalDate = eLocalDate.withDayOfMonth(1).toLocalDate().atStartOfDay();

                List<Date> firstDays = new ArrayList<>();
                LocalDate startDate = sLocalDate.toLocalDate();
                LocalDate endDate = eLocalDate.toLocalDate();
//                LocalDate firstDayOf = startDate.withDayOfMonth(1);
//                LocalDate lastDayOf = endDate.withDayOfMonth(1);
//                for (LocalDate currentMonth = firstDayOf; !currentMonth.isAfter(lastDayOf); currentMonth = currentMonth.plusMonths(1)) {
//                    firstDays.add(java.sql.Date.valueOf(currentMonth));
//                }
                firstDays.add(java.sql.Date.valueOf(startDate));
                firstDays.add(java.sql.Date.valueOf(endDate));

                Date sDate = java.sql.Date.valueOf(startDate);
                Date eDate = java.sql.Date.valueOf(endDate);
                Date sDateTime = Date.from(sLocalDate.atZone(ZoneId.systemDefault()).toInstant());
                Date eDateTime = Date.from(eLocalDate.atZone(ZoneId.systemDefault()).toInstant());
                for (String queryNodeId: stations) {
                    try {
                        List<String> energyIds = globalApiService.findSubEnergyIdsByStationIds(queryNodeId);
                        energyIds.remove(queryNodeId);
                        if (energyIds.isEmpty()) {
                            continue;
                        }
                        String nodeOnly = energyIds.get(0);
                        long energyCount = energyIds.size();
                        List<RAEnergyBaseDTO> energyBaseInfos = energyBaseInfoRepository.findRAEnergyBaseDTOByNodeIds(energyIds);
                        Map<String, Double> energyBaseDTOMap = energyBaseInfos.stream()
                                .collect(Collectors.toMap(
                                        RAEnergyBaseDTO::getNodeId,
                                        v -> v.getStorageEnergyCapacity() * v.getMaxChargePercent(),
                                        (existing, replacement) -> existing
                                ));
                        Map<String, RAEnergyBaseDTO> energyBaseInfoMap = new HashMap<>();
                        for (RAEnergyBaseDTO energyBaseDTO : energyBaseInfos) {
                            if (isNaN(energyBaseDTO.getStorageEnergyCapacity())) {
                                continue;
                            }
                            if (isNaN(energyBaseDTO.getMaxChargePercent())) {
                                continue;
                            }
                            if (isNaN(energyBaseDTO.getMinDischargePercent())) {
                                continue;
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
                        PointModelMapping mapping = pointModelMappingRepository.findByStationIdContainingAndPointDesc(queryNodeId, "total_load");

                        PointService pointService = SpringBeanHelper.getBeanOrThrow(PointService.class);
                        Map<Long, Double> loadMap = getDateValueFromMapping(sDateTime,eDateTime,pointService,mapping);
                        if (loadMap.isEmpty()) {
                            continue;
                        }
                        List<String> pvList = globalApiService.findSubPvIdsByStationIds(queryNodeId);
                        for (String stationNode : pvList) {
                            PointModelMapping pvMapping =
                                    pointModelMappingRepository.findByStationIdContainingAndPointDesc(stationNode, "power");
                            Map<Long, Double> pvMap = getDateValueFromMapping(sDateTime, eDateTime, pointService, pvMapping);
                            if (pvMap.isEmpty()) {
                                continue;
                            }
                            for (Map.Entry<Long, Double> entry : loadMap.entrySet()) {
                                loadMap.put(entry.getKey(), loadMap.get(entry.getKey()) - pvMap.get(entry.getKey()));
                            }
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
                        List<RAInfoVO> raInfoVOS = revenueAnalysisService.revenueAnalysis(socMap,priceMap,energyBaseInfoMap.get(nodeOnly),dateProfitMap,loadMap,eLocalDate)
                                .stream().map(dto -> {
                                    RAInfoVO raInfoVO = new RAInfoVO();
                                    raInfoVO.setTime(dto.getTime());
                                    raInfoVO.setDynamic(dto.getDynamic());
                                    raInfoVO.setFixed(dto.getFixed());
                                    return raInfoVO;
                                })
                                .collect(Collectors.toList());
                        RevenueAnalysis revenueAnalysis;
                        RevenueAnalysis revenueAnalysis1 = revenueAnalysisRepository.findById(queryNodeId).orElse(null);
                        if (revenueAnalysis1 != null ) {
                            revenueAnalysis = revenueAnalysis1;
                        } else  {
                            revenueAnalysis = new RevenueAnalysis();
                            revenueAnalysis.setStationId(queryNodeId);
                        }
                        if (1 == 1) {
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
                            String nowYearRevenue = revenueAnalysis.getYearRevenue();
                            JSONArray jsonArray = JSON.parseArray(nowYearRevenue);
                            List<RAInfoVO> currentList = new ArrayList<>();
                            for (int i = 0; i < jsonArray.size(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                RAInfoVO raInfoVO = new RAInfoVO(
                                        jsonObject.getString("time"),
                                        jsonObject.getBigDecimal("dynamic"),
                                        jsonObject.getBigDecimal("fixed")
                                );
                                currentList.add(raInfoVO);
                            }
                            for (RAInfoVO newVO : monthVO) {
                                boolean found = false;
                                for (RAInfoVO existingVO : currentList) {
                                    if (existingVO.getTime().equals(newVO.getTime())) {
                                        existingVO.setDynamic(newVO.getDynamic());
                                        existingVO.setFixed(newVO.getFixed());
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    currentList.add(newVO);
                                }
                            }
                            revenueAnalysis.setYearRevenue(JSON.toJSONString(currentList));

                            String nowYearCount = revenueAnalysis.getYearCount();
                            JSONArray jsonArrayCount = JSON.parseArray(nowYearCount);
                            List<RAInfoVO> currentListCount = new ArrayList<>();
                            for (int i = 0; i < jsonArrayCount.size(); i++) {
                                JSONObject jsonObject = jsonArrayCount.getJSONObject(i);
                                RAInfoVO raInfoVO = new RAInfoVO(
                                        jsonObject.getString("time"),
                                        jsonObject.getBigDecimal("dynamic"),
                                        jsonObject.getBigDecimal("fixed")
                                );
                                currentListCount.add(raInfoVO);
                            }
                            for (RAInfoVO newVO : monthCountVO) {
                                boolean found = false;
                                for (RAInfoVO existingVO : currentListCount) {
                                    if (existingVO.getTime().equals(newVO.getTime())) {
                                        existingVO.setDynamic(newVO.getDynamic());
                                        existingVO.setFixed(newVO.getFixed());
                                        found = true;
                                        break;
                                    }
                                }
                                if (!found) {
                                    currentListCount.add(newVO);
                                }
                            }
                            revenueAnalysis.setYearCount(JSON.toJSONString(currentListCount));
                        }
                        if(2 == 2) {
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

                            revenueAnalysis.setMouthRevenue(JSON.toJSONString(raInfoVOS));
                            revenueAnalysis.setMouthCount(JSON.toJSONString(dayCountVO));
                        }
                        log.info("完成测算：{}",queryNodeId);
                        revenueAnalysisRepository.save(revenueAnalysis);
                    } catch (Exception e) {
                        log.error("计算报错", e);
                    }
                }
            }

//        } catch (Exception e) {
//            log.info("收益分析报错");
//        }

    }
    private Map<Long, Double> getDateValueFromMapping(Date startDate, Date endDate, PointService pointService,
                                                      PointModelMapping mapping)  {
        return pointService.getLDValuesByTime(mapping, startDate, endDate);
    }
}
