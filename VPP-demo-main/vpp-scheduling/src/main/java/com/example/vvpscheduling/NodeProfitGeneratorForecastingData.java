package com.example.vvpscheduling;

import com.example.vvpcommom.Enum.NodePostTypeEnum;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpcommom.arima.DaMaoPrediction;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.*;
import com.example.vvpscheduling.model.Staff;
import org.apache.commons.collections4.ListUtils;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预测收益-调度任务
 */
@Component("nodeProfitGeneratorForecastingData")
@EnableAsync
public class NodeProfitGeneratorForecastingData {

    private static final Logger logger = LoggerFactory.getLogger(NodeProfitGeneratorForecastingData.class);
    @Resource
    private NodeProfitMonthForecastingRepository nodeProfitMonthForecastingRepository;

    @Resource
    private NodeProfitDayForecastingRepository nodeProfitDayForecastingRepository;

    @Resource
    private NodeProfitRepository nodeProfitRepository;

    @Resource
    private NodeRepository nodeRepository;

    /**
     * 预测收益- 通过节点配置预测配置预测日期
     */
    @Scheduled(initialDelay = 1000 * 5 * 60, fixedDelay = 60 * 60 * 1000 * 5)
    @Async
    public void nodeProfitForecastingMonth() {

        try {

            List<String> nodeIds = nodeProfitRepository.findAllGroupByNodeId();
            if (nodeIds != null && nodeIds.size() > 0) {
                for (String nodeId : nodeIds) {
                    try {
                        if (StringUtils.isEmpty(nodeId) == false) {

                            Pair<double[], List<Date>> predictData = predictData(nodeId);
                            List<Date> forecastDayDates = predictSteps(nodeId);


                            if (predictData == null
                                    || (predictData.getValue0() != null && predictData.getValue0().length == 0)
                                    || forecastDayDates == null
                                    || forecastDayDates.size() == 0) {
                                continue;
                            }
                            try {
                                int forecastDataNumber = forecastDayDates.size();
                                double[] predictDayValueList = DaMaoPrediction.predict(predictData.getValue1(), predictData.getValue0(), forecastDataNumber);

                                if (forecastDataNumber > 0
                                        && predictDayValueList != null
                                        && predictDayValueList.length > 0) {
                                    saveForecastDays(nodeId, forecastDayDates, predictDayValueList);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private Pair<double[], List<Date>> predictData(String nodeId) {

        try {
            if (nodeRepository.existsById(nodeId)) {

                SimpleDateFormat ymdFmt = new SimpleDateFormat("yyyy-MM-dd");
                ymdFmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

                Date nowDay = ymdFmt.parse(ymdFmt.format(new Date()));
                Date endDay = ymdFmt.parse(ymdFmt.format(TimeUtil.dateAddDay(new Date(), -1)));
                Date startDay = ymdFmt.parse(ymdFmt.format(TimeUtil.dateAddMonths(endDay, -3)));

                List<NodeProfit> nodeProfitList = nodeProfitRepository.findAllByNodeIdAndProfitDateBetweenOrderByProfitDateAsc(nodeId, startDay, endDay);
                if (nodeProfitList != null && nodeProfitList.size() > 0) {

                    Date startProfitDate = nodeProfitList.get(0).getProfitDate();

                    if (startProfitDate.compareTo(endDay) == 0) {
                        return null;
                    }

                    List<Date> dayList = TimeUtil.truncateToSplitDay(startProfitDate, nowDay);
                    if (dayList != null && dayList.size() > 0) {

                        Map<Date, Double> days = new TreeMap<>();

                        for (Date day : dayList) {

                            double decimal = nodeProfitList.stream()
                                    .filter(c -> ymdFmt.format(c.getProfitDate()).equals(ymdFmt.format(day)))
                                    .mapToDouble(p -> BigDecimal.valueOf(p.getProfitValue())
                                            .doubleValue()).sum();

                            days.put(day, decimal);
                        }

                        List<Map.Entry<Date, Double>> list = new ArrayList<>(days.entrySet());

                        Collections.sort(list, Comparator.comparing(Map.Entry::getKey));

                        return Pair.with(list.stream().mapToDouble(p -> p.getValue()).toArray(), dayList);

                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }

    @Resource
    private CfgPhotovoltaicBaseInfoRepository cfgPhotovoltaicBaseInfoRepository;
    @Resource
    private CfgStorageEnergyBaseInfoRepository cfgStorageEnergyBaseInfoRepository;

    private List<Date> predictSteps(String nodeId) {
        try {

            Node node = nodeRepository.findByNodeId(nodeId);
            if (node != null) {

                String nodePostType = node.getNodePostType();
                //预测截止时间
                Date forecastEndTime = null;

                if (NodePostTypeEnum.pv.getNodePostType().equals(nodePostType)) {
                    CfgPhotovoltaicBaseInfo baseInfo = cfgPhotovoltaicBaseInfoRepository.findAllByNodeIdAndSystemId(nodeId, "nengyuanzongbiao");
                    if (baseInfo != null) {
                        forecastEndTime = TimeUtil.dateAddYears(baseInfo.getTimeDivisionStartTime(), baseInfo.getTimeDivisionExpiryDate());
                    }
                }

                if (NodePostTypeEnum.storageEnergy.getNodePostType().equals(nodePostType)) {
                    CfgStorageEnergyBaseInfo baseInfo = cfgStorageEnergyBaseInfoRepository.findCfgStorageEnergyBaseInfoByNodeId(nodeId);
                    if (baseInfo != null) {
                        forecastEndTime = TimeUtil.dateAddYears(baseInfo.getStrategyStartTime(), baseInfo.getStrategyExpiryDate());
                    }
                }

                if (forecastEndTime != null && (new Date()).compareTo(forecastEndTime) <= 0) {

                    return TimeUtil.truncateToSplitDay(new Date(), forecastEndTime);
                }
            }
        } catch (Exception ex) {
        }
        return null;
    }


    private void saveForecastDays(String nodeId, List<Date> forecastDayDates, double[] forecastDayValues) {
        try {
            SimpleDateFormat ymFmt = new SimpleDateFormat("yyyy-MM");
            ymFmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            SimpleDateFormat ymdFmt = new SimpleDateFormat("yyyy-MM-dd");
            ymdFmt.setTimeZone(TimeZone.getTimeZone("GMT+8"));

            if (forecastDayDates != null
                    && forecastDayDates.size() > 0
                    && forecastDayValues != null
                    && forecastDayValues.length > 0
                    && forecastDayValues.length == forecastDayDates.size()) {

                List<Staff> staffList = new ArrayList<>();

                for (int i = 0; i < forecastDayDates.size(); i++) {

                    staffList.add(new Staff(forecastDayDates.get(i), BigDecimal.valueOf(forecastDayValues[i])));
                }

                //求每月对于生产总量
                Map<String, BigDecimal> yieldMonthMap = staffList.stream().collect(
                        Collectors.groupingBy(
                                o -> ymFmt.format(o.getDate()),
                                Collectors.mapping(Staff::getYield, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                        ));

                Map<String, List<Staff>> yieldMonthMapForecast = staffList.stream().collect(Collectors.groupingBy((px) -> ymFmt.format(px.getDate())));

                List<NodeProfitMonthForecasting> nodeProfitListMf = new ArrayList<>();

                List<NodeProfitDayForecasting> nodeProfitListDay = new ArrayList<>();


                for (Map.Entry<String, BigDecimal> entry : yieldMonthMap.entrySet()) {
                    try {

                        Date dt = ymdFmt.parse(entry.getKey() + "-01 00:00:00");

                        List<Staff> staff = yieldMonthMapForecast.get(entry.getKey());
                        //天
                        if (staff != null && staff.size() > 0) {
                            for (Staff e : staff) {
                                if ((new Date()).before(e.getDate())) {
                                    NodeProfitDayForecasting nfd = new NodeProfitDayForecasting();
                                    nfd.setNodeId(nodeId);
                                    nfd.setProfitId(nodeId + TimeUtil.toYmdStr(e.getDate()));
                                    nfd.setProfitDateDay(e.getDate());
                                    nfd.setProfitForecastValue(Double.parseDouble(String.format("%.2f", Math.max(0, e.getYield().doubleValue()))));
                                    nodeProfitListDay.add(nfd);
                                }
                            }
                        }

                        //月
                        if (entry.getKey().equals(ymFmt.format(new Date())) == false) {

                            NodeProfitMonthForecasting nfm = new NodeProfitMonthForecasting();
                            nfm.setNodeId(nodeId);
                            nfm.setProfitId(nodeId + TimeUtil.toYmStr(dt));
                            nfm.setProfitDateMonth(dt);
                            nfm.setProfitForecastValue(Double.parseDouble(String.format("%.2f", Math.max(0, entry.getValue().doubleValue()))));

                            nodeProfitListMf.add(nfm);
                        }
                    } catch (Exception ex) {
                    }
                }

                if (nodeProfitListMf != null && nodeProfitListMf.size() > 0) {

                    List<List<NodeProfitMonthForecasting>> monthSubs = ListUtils.partition(nodeProfitListMf, 100);
                    if (monthSubs != null && monthSubs.size() > 0) {
                        for (List<NodeProfitMonthForecasting> months : monthSubs) {
                            if (months != null && months.size() > 0) {
                                nodeProfitMonthForecastingRepository.saveAll(months);
                                Thread.sleep(30);
                            }
                        }
                    }


                }
                if (nodeProfitListDay != null && nodeProfitListDay.size() > 0) {
                    List<List<NodeProfitDayForecasting>> daySubs = ListUtils.partition(nodeProfitListDay, 100);
                    if (daySubs != null && daySubs.size() > 0) {
                        for (List<NodeProfitDayForecasting> days : daySubs) {
                            if (days != null && days.size() > 0) {
                                nodeProfitDayForecastingRepository.saveAll(days);
                                Thread.sleep(30);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
    }
}
