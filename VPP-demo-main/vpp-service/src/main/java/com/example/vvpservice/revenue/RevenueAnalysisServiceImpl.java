package com.example.vvpservice.revenue;

import com.example.vvpdomain.dto.RAEnergyBaseDTO;
import com.example.vvpdomain.dto.RAPriceDTO;
import com.example.vvpservice.revenue.model.RAInfoDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.time.temporal.TemporalAdjusters;
@Slf4j
@Service
public class RevenueAnalysisServiceImpl  implements RevenueAnalysisService {

    /**
     * 充电效率
     */

    private static double eff_in = 0.91;

    /**
     * 放电效率
     */
    private static double eff_out = 0.92;

    public List<RAInfoDTO> revenueAnalysis(Map<String, Map<String, Double>> socMap,
                                           Map<String, Map<String, RAPriceDTO>> priceMap,
                                           RAEnergyBaseDTO energyBaseInfoMap,
                                           Map<String, Double> dateProfitMap,
                                           Map<Long, Double> loadMap,
                                           LocalDateTime eLocalDate
    ){
        List<RAInfoDTO> raInfoVOS = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (Map.Entry<String, Map<String, Double>> entry2 : socMap.entrySet()) {
            String category = entry2.getKey();
            Map<String, Double> innerInnerMap = entry2.getValue();
            Map<String, RAPriceDTO> categoryPriceMap;
            Double storageEnergyCapacity = energyBaseInfoMap.getStorageEnergyCapacity();
            Double maxChargePercent = energyBaseInfoMap.getMaxChargePercent() / 100.0;
            Double minDischargePercent = energyBaseInfoMap.getMinDischargePercent() / 100.0;
            Double maxEnergy = storageEnergyCapacity;
            BigDecimal minEnergy = BigDecimal.valueOf(storageEnergyCapacity * minDischargePercent);
//                log.info("product 当前的值:{}",product);
//                log.info("energyBaseInfo 当前的值:{}",JSON.toJSONString(energyBaseInfo));
//                log.info("minEnergy 大于零当前的值:{}",minEnergy);
//                log.info("maxEnergy 小于零当前的值:{}",maxEnergy);
            if (priceMap.get(category) == null) {
                if (priceMap.get(category.substring(0, category.lastIndexOf("-")) + "-01") != null ) {
                    categoryPriceMap = priceMap.get(category.substring(0, category.lastIndexOf("-")) + "-01");
                } else {
                    continue;
                }
            } else {
                categoryPriceMap = priceMap.get(category);
            }

            for (Map.Entry<String, Double> entry3 : innerInnerMap.entrySet()) {
                String time = entry3.getKey();
                Double soc = entry3.getValue();
                String deadlineStr = category +" "+ time;
                LocalDateTime deadline = eLocalDate;
                LocalDateTime firstDayOfMonth = LocalDateTime.parse(deadlineStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime endOfMonth = firstDayOfMonth.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59).withNano(999999999);
                LocalDateTime actualEndTime = (deadline.isBefore(endOfMonth) || deadline.isEqual(endOfMonth)) ? deadline : endOfMonth;
                long daysBetween = Duration.between(firstDayOfMonth, actualEndTime).toDays();
//                    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
                BigDecimal nowEnergy = BigDecimal.valueOf(soc * storageEnergyCapacity);

                for (int i = 0; i < daysBetween; i++) {
                    BigDecimal fixedEndSum = BigDecimal.valueOf(0.0);
                    RAInfoDTO raInfoVO = new RAInfoDTO();
                    LocalDateTime currentDay = firstDayOfMonth.plusDays(i);
                    category = currentDay.toLocalDate().toString();

                    for (int j = 0; j < 96; j++) {
                        LocalDateTime currentTime = currentDay.plusMinutes(j * 15);
                        time = currentTime.format(timeFormatter);
                        BigDecimal energy = BigDecimal.ZERO;
                        RAPriceDTO priceInfo = categoryPriceMap.get(time);
                        BigDecimal price = priceInfo.getPrice();
                        String tititt = category +" "+ time + ":00";
                        Long loadTime = LocalDateTime.parse(tititt, formatter).atZone(ZoneId.systemDefault()).toEpochSecond() * 1000;

                        Double loadValue = loadMap.get(loadTime);

                        if (loadValue == null) {
//                            log.info("tititt 当前的时间空了:{}",tititt);
//                            log.info("loadTime 当前的时间戳空了:{}",loadTime);
                            continue;
                        }
                        BigDecimal loadValueBigDecimal = BigDecimal.valueOf(loadValue);
                        BigDecimal priceStrategy = priceInfo.getStrategy();
//                        log.info("category 当前的时间:{}",category);
//                        log.info("time 当前的时间:{}",time);
//                        log.info("soc 当前的soc:{}",soc);
//
//                        log.info("tititt 当前的时间:{}",tititt);
//                        log.info("loadTime 当前的时间戳:{}",loadTime);
//                        log.info("loadValue 当前的时间值:{}",loadValue);
//                        log.info("priceStrategy 当前的时间值:{}",priceStrategy);
//                        log.info("nowEnergy 当前energy的值:{}",nowEnergy);
//                        log.info("price 当前price的值:{}",price);

                        if (priceStrategy.compareTo(BigDecimal.ZERO) > 0) {
                            priceStrategy = loadValueBigDecimal.min(priceStrategy).multiply(BigDecimal.valueOf(0.25)).abs();
                            if (loadValueBigDecimal.subtract(BigDecimal.valueOf(10)).compareTo(BigDecimal.ZERO) < 0) {
                                priceStrategy = BigDecimal.valueOf(0);
                            }
                            BigDecimal actual = nowEnergy;
//                            log.info("priceStrategy 大于零当前的值:{}",priceStrategy);
//                            log.info("actual 大于零当前的值:{}",actual);
                            if (actual.compareTo(minEnergy) <= 0) {
                                continue;
                            }
                            actual = actual.subtract(minEnergy);
                            if (actual.compareTo(priceStrategy) <= 0) {
                                energy = actual;
                            } else {
                                energy = priceStrategy;
                            }
                            fixedEndSum = fixedEndSum.add(price.multiply(energy).multiply(BigDecimal.valueOf(eff_out)));
//                            log.info("fixedEndSum 大于零当前的值:{}",fixedEndSum);

                        } else if(priceStrategy.compareTo(BigDecimal.ZERO) < 0) {
//                                    BigDecimal negatedLoadValue = loadValueBigDecimal.negate();
//                                    priceStrategy = priceStrategy.max(negatedLoadValue).multiply(BigDecimal.valueOf(0.25));
                            priceStrategy = priceStrategy.multiply(BigDecimal.valueOf(0.25));
                            BigDecimal actual = BigDecimal.valueOf(maxEnergy * maxChargePercent).subtract(nowEnergy);

//                            log.info("negatedLoadValue 小于零当前的值:{}",negatedLoadValue);
//                            log.info("priceStrategy 小于零当前的值:{}",priceStrategy);
//                            log.info("actual 小于零当前的值:{}",actual);
                            if (soc >= 0.98) {
                                continue;
                            }
                            if (actual.compareTo(priceStrategy.abs()) <= 0) {
                                energy = actual.negate();
                            } else {
                                energy = priceStrategy;
                            }
                            fixedEndSum = fixedEndSum.add(price.multiply(energy).multiply(BigDecimal.valueOf(eff_in)));
//                            log.info("fixedEndSum 小于零当前的值:{}",fixedEndSum);

                        }
//                        log.info("energy 当前energy的值:{}",energy);
                        nowEnergy = nowEnergy.subtract(energy);
                        soc = nowEnergy.divide(BigDecimal.valueOf(maxEnergy), 4, RoundingMode.HALF_UP).doubleValue();
                    }
                    if (dateProfitMap.get(category) != null) {
                        raInfoVO.setDynamic(BigDecimal.valueOf(dateProfitMap.get(category)).setScale(2, RoundingMode.DOWN));
                    }
                    raInfoVO.setFixed(fixedEndSum.setScale(2, RoundingMode.DOWN));
                    raInfoVO.setTime(category);
                    raInfoVOS.add(raInfoVO);
                }
            }

        }
        return raInfoVOS;

    }
}
