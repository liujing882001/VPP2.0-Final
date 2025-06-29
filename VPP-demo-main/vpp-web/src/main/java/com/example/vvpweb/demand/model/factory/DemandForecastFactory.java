package com.example.vvpweb.demand.model.factory;

import com.alibaba.fastjson.JSON;
import com.example.vvpdomain.entity.AiLoadForecasting;
import com.example.vvpdomain.entity.DemandStrategy;
import com.example.vvpweb.demand.model.DemandForecastResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Slf4j
public class DemandForecastFactory {


    public List<DemandForecastResponse> toVOList(
            List<AiLoadForecasting> loadForecastingList,
            List<DemandStrategy> demandStrategyList,
            Date sTime,
            Date eTime,
            Date rsTime,
            Date reTime) {
        List<DemandForecastResponse> list = new ArrayList<>();
        Long forecastAdjustLoad = Math.round(demandStrategyList.stream().mapToDouble(vend1 -> Double.parseDouble(vend1.getForecastAdjustLoad())).average().orElse(0));
        long firstTime = sTime.getTime();
        AtomicLong startTime = new AtomicLong(firstTime -100 );
        AtomicLong endTime = new AtomicLong(firstTime + (900 * 1000) +100);
        long rsLongTime = rsTime.getTime();
        long reLongTime = reTime.getTime();
        //临时增加逻辑nowDate，当前时间在创建时间之前才会显示实际负荷
        long nowDate = new Date().getTime();
        List<Double> baselineLoadValue = new ArrayList<>();
        List<Double> forecastLoad = new ArrayList<>();
        List<Double> realValue = new ArrayList<>();
        AtomicReference<Integer> count = new AtomicReference<>(0);
        AtomicLong chartId = new AtomicLong(0L);
        int listSize= loadForecastingList.size() - 1;
        loadForecastingList.forEach(v ->
                {
                    DemandForecastResponse response = new DemandForecastResponse();
                    long time = v.getCreatedTime().getTime() + 50 ;

                    if (v.getId() == loadForecastingList.get(0).getId()) {
                        time = v.getCreatedTime().getTime();
                    }
                    if(time >= startTime.get() && time <= endTime.get()) {
                        if (v.getBaselineLoadValue() != null && !("-").equals(v.getBaselineLoadValue())) {
                            baselineLoadValue.add(Double.valueOf(v.getBaselineLoadValue()));
                        }
                        if (v.getCurrentForecastValue() != null && !("-").equals(v.getCurrentForecastValue())) {
                            forecastLoad.add(Double.valueOf(v.getCurrentForecastValue()));

                        }
                        if (v.getRealValue() != null && !("-").equals(v.getRealValue())) {
                            if (nowDate >= time - 50) {
                                realValue.add(Double.valueOf(v.getRealValue()));
                            }
                        }
                    } else {
//                        long countTime;
//                        if (demandStrategyList.size() >= count.get() + 1) {
//                            countTime = demandStrategyList.get(count.get()).getForecastTime().getTime();
//                        } else {
//                            countTime = 0;
//                        }
//                        Long end1 = 0L;
                        Long end2 = 0L;
                        end2 = Math.round(forecastLoad.stream().mapToDouble(Double::doubleValue).average().orElse(0));
//                        if (countTime >= startTime.get() && countTime <= endTime.get()) {
//                            count.getAndSet(count.get() + 1);
//                            if (demandStrategyList.get(count.get() - 1).getForecastAdjustedLoad() != null) {
//                                end1 = (long) Double.parseDouble(demandStrategyList.get(count.get() - 1).getForecastAdjustedLoad());
//                            }
//
//                            response.setForecastLoadAfterRegulation(Arrays.asList(
//                                    chartId.get(),
//                                    0L,
//                                    end1
//                            ));
//                            response.setForecastRegulationLoad(Arrays.asList(
//                                    chartId.get(),
//                                    0L,
//                                    (end2 - end1)));
//                        }
//                        end1 = Math.round(demandStrategyList.stream().mapToDouble(vend1 -> Double.parseDouble(vend1.getForecastAdjustedLoad())).average().orElse(0));



//                        response.setTimeStamp(new Date(startTime.get()));
                        if (list.size() == 0) {
                            response.setTimeStamp(new Date(startTime.get() +100 ));
                        } else {
                            response.setTimeStamp(new Date(list.get(list.size()-1).getTimeStamp().getTime() + (900 * 1000) +100));

                        }

                        if (startTime.get() + 100 >= rsLongTime && startTime.get() + 100 <= reLongTime) {
                            response.setAdjust(true);
                            //临时让全部任务响应时间内的调节值为同一个：
                            response.setForecastLoadAfterRegulation(Arrays.asList(
                                    chartId.get(),
                                    0L,
                                    (end2 - forecastAdjustLoad)
                            ));
                            response.setForecastRegulationLoad(Arrays.asList(
                                    chartId.get(),
                                    0L,
                                    forecastAdjustLoad));
                        } else {
                            response.setAdjust(false);
                        }

                        for (int i = 0; i < 8; i++) {
                            startTime.getAndSet(endTime.get());
                            endTime.getAndSet(endTime.get() + (900 * 1000));
                            if (time >= startTime.get() && time <= endTime.get()) {
                                break;
                            }
                        }
                        response.setBaselineLoadValue(Arrays.asList(
                                chartId.get(),
                                0L,
                                baselineLoadValue.isEmpty()
                                        ? 0L
                                        : Math.round(baselineLoadValue.stream().mapToDouble(Double::doubleValue).average().orElse(0))
                        ));
                        baselineLoadValue.clear();
                        response.setForecastLoad(Arrays.asList(
                                chartId.get(),
                                0L,
                                forecastLoad.isEmpty()
                                        ? 0L
                                        : end2
                        ));
                        forecastLoad.clear();

                        response.setRealValue(Arrays.asList(
                                chartId.get(),
                                0L,
                                realValue.isEmpty()
                                        ? 0L
                                        : Math.round(realValue.stream().mapToDouble(Double::doubleValue).average().orElse(0))

                        ));
                        realValue.clear();

                        if (v.getBaselineLoadValue() != null && !("-").equals(v.getBaselineLoadValue())) {
                            baselineLoadValue.add(Double.valueOf(v.getBaselineLoadValue()));
                        }
                        if (v.getCurrentForecastValue() != null && !("-").equals(v.getCurrentForecastValue())) {
                            forecastLoad.add(Double.valueOf(v.getCurrentForecastValue()));
                        }
                        if (v.getRealValue() != null && !("-").equals(v.getRealValue())) {
                            if (nowDate >= time - 50) {
                                realValue.add(Double.valueOf(v.getRealValue()));
                            }
                        }
                        chartId.getAndSet(chartId.get() + 1L);

                        list.add(response);
                    }
                    if (loadForecastingList.indexOf(v) == listSize) {
                        DemandForecastResponse responseEnd = new DemandForecastResponse();
                        if (v.getBaselineLoadValue() != null && !("-").equals(v.getBaselineLoadValue())) {
                            baselineLoadValue.add(Double.valueOf(v.getBaselineLoadValue()));
                        }
                        if (v.getCurrentForecastValue() != null && !("-").equals(v.getCurrentForecastValue())) {
                            forecastLoad.add(Double.valueOf(v.getCurrentForecastValue()));
                        }
                        if (v.getRealValue() != null && !("-").equals(v.getRealValue())) {
                            if (nowDate >= time - 50) {
                                realValue.add(Double.valueOf(v.getRealValue()));
                            }
                        }
                        Long end2 = 0L;
                        end2 = Math.round(forecastLoad.stream().mapToDouble(Double::doubleValue).average().orElse(0));


                        if (startTime.get() + 100 >= rsLongTime && startTime.get() + 100 <= reLongTime) {
                            responseEnd.setAdjust(true);
                            //临时让全部任务响应时间内的调节值为同一个：
                            responseEnd.setForecastLoadAfterRegulation(Arrays.asList(
                                    chartId.get(),
                                    0L,
                                    (end2 - forecastAdjustLoad)
                            ));
                            responseEnd.setForecastRegulationLoad(Arrays.asList(
                                    chartId.get(),
                                    0L,
                                    forecastAdjustLoad));
                        } else {
                            responseEnd.setAdjust(false);
                        }

//                        response.setTimeStamp(new Date(startTime.get()));
                        if (list == null) {
                            responseEnd.setTimeStamp(new Date(startTime.get()));
                        } else {
                            responseEnd.setTimeStamp(new Date(list.get(list.size()-1).getTimeStamp().getTime() + (900 * 1000) +100));

                        }

                        responseEnd.setBaselineLoadValue(Arrays.asList(
                                chartId.get(),
                                0L,
                                baselineLoadValue.isEmpty()
                                        ? 0L
                                        : Math.round(baselineLoadValue.stream().mapToDouble(Double::doubleValue).average().orElse(0))
                        ));
                        baselineLoadValue.clear();
                        responseEnd.setForecastLoad(Arrays.asList(
                                chartId.get(),
                                0L,
                                forecastLoad.isEmpty()
                                        ? 0L
                                        : end2
                        ));
                        forecastLoad.clear();

                        responseEnd.setRealValue(Arrays.asList(
                                chartId.get(),
                                0L,
                                realValue.isEmpty()
                                        ? 0L
                                        : Math.round(realValue.stream().mapToDouble(Double::doubleValue).average().orElse(0))

                        ));
                        realValue.clear();
                        list.add(responseEnd);

                    }
                }
                );
        return list.stream().filter(v -> v.getTimeStamp() != null).collect(Collectors.toList());
    }

}
