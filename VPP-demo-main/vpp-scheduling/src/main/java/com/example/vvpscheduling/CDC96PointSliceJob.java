package com.example.vvpscheduling;

import com.example.vvpcommom.Minutes15Model;
import com.example.vvpcommom.Slice96Util;
import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.*;
import com.example.vvpdomain.entity.DevicePoint;
import com.example.vvpdomain.entity.IotTsKv;
import com.example.vvpdomain.entity.IotTsKvMeteringDevice96;
import com.example.vvpdomain.entity.Node;
import com.example.vvpdomain.view.IotTsKvEnergyLastWeekView;
import com.example.vvpdomain.view.IotTsKvLoadLastWeekView;
import com.example.vvpscheduling.model.slicing.EnergyUnit;
import com.example.vvpscheduling.model.slicing.SliceResult;
import com.example.vvpscheduling.util.CDC96PointUtil;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 数据切片iot_ts_kv到iot_ts_kv_metering_device_96
 */
@Component("cdc96PointSliceJob")
@EnableAsync
public class CDC96PointSliceJob {
    @Resource
    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
    @Resource
    private IotTsKvRepository iotTsKvRepository;
    @Resource
    private NodeRepository nodeRepository;
    @Resource
    private IotTsKvLoadLastWeekRepository loadLastWeekRepository;
    @Resource
    private IotTsKvEnergyLastWeekRepository energyLastWeekRepository;

    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000 * 5)
    @Async
    public void load96Slicing() {
        SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
        fmt_ymdhm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_hms = new SimpleDateFormat("HH:mm:ss");
        fmt_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_ym = new SimpleDateFormat("yyyy-MM");
        fmt_ym.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        System.out.println("开始load96Slicing");
        List<IotTsKvLoadLastWeekView> items = loadLastWeekRepository.findAllLoadInterval3days();
        if (items != null && items.size() > 0) {

            Map<String, List<IotTsKvLoadLastWeekView>> countMap = items
                    .stream()
                    .collect(Collectors.groupingBy(o -> o.getDeviceSn() + o.getPointSn() + fmt_ymd.format(o.getTs())));
            if (countMap != null && countMap.size() > 0) {

                for (String p : countMap.keySet()) {
                    if (p != null) {
                        List<IotTsKvLoadLastWeekView> entities = countMap.get(p).stream()
                                .sorted(Comparator.comparing(IotTsKvLoadLastWeekView::getTs))
                                .collect(Collectors.toList());
                        if (entities != null && entities.size() > 0) {

                            String deviceSn = entities.get(0).getDeviceSn();
                            String pointSn = entities.get(0).getPointSn();
                            String pointdesc = entities.get(0).getPointDesc();
                            Date singleTime = entities.get(0).getTs();
                            Date now = new Date();
                            IotTsKv firstEntity = iotTsKvRepository.findFirstLoadDeivcePointInfo(deviceSn, pointSn);

                            if (firstEntity != null && firstEntity.getTs().after(now) == false) {

                                Date first_date_time = firstEntity.getTs();
                                List<SliceResult> results = CDC96PointUtil.processLoadData(entities, singleTime);
                                if (results != null && results.size() > 0) {
                                    List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96s = new ArrayList<>();
                                    for (SliceResult result : results) {
                                        try {
                                            Date count_date_time = result.getCountDataTime();

                                            if (first_date_time.after(count_date_time)) {

                                                continue;
                                            }
                                            if (now.before(count_date_time)) {

                                                continue;
                                            }

                                            Date count_date = fmt_ymd.parse(fmt_ymd.format(count_date_time));
                                            Date count_time = fmt_hms.parse(fmt_hms.format(count_date_time));

                                            String id = deviceSn + "_" + pointSn + "_" + pointdesc + "_" + fmt_ymdhm.format(count_date_time);

                                            IotTsKvMeteringDevice96 tsKvMeteringDevice96 = new IotTsKvMeteringDevice96();
                                            tsKvMeteringDevice96.setId(id);
                                            tsKvMeteringDevice96.setProvinceRegionId(entities.get(0).getProvinceRegionId());
                                            tsKvMeteringDevice96.setCityRegionId(entities.get(0).getCityRegionId());
                                            tsKvMeteringDevice96.setCountyRegionId(entities.get(0).getCountyRegionId());
                                            tsKvMeteringDevice96.setNodeId(entities.get(0).getNodeId());
                                            tsKvMeteringDevice96.setLongitude(entities.get(0).getLongitude());
                                            tsKvMeteringDevice96.setLatitude(entities.get(0).getLatitude());
                                            tsKvMeteringDevice96.setSystemId(entities.get(0).getSystemId());
                                            tsKvMeteringDevice96.setDeviceSn(entities.get(0).getDeviceSn());
                                            tsKvMeteringDevice96.setPointSn(entities.get(0).getPointSn());
                                            tsKvMeteringDevice96.setConfigKey(entities.get(0).getDeviceConfigKey());
                                            tsKvMeteringDevice96.setHTotalUse(result.getLastValue());
                                            tsKvMeteringDevice96.setTotalPowerEnergy((double) 0);
                                            //tsKvMeteringDevice96.setTotalPowerEnergyBase((double) 0);
                                            tsKvMeteringDevice96.setPointDesc(entities.get(0).getPointDesc());
                                            tsKvMeteringDevice96.setPointUnit(entities.get(0).getPointUnit());
                                            tsKvMeteringDevice96.setCountDate(count_date);
                                            tsKvMeteringDevice96.setCountTime(count_time);
                                            tsKvMeteringDevice96.setCountDataTime(count_date_time);
                                            tsKvMeteringDevice96.setTimeScope(result.getTimeScope());
                                            tsKvMeteringDevice96.setNodePostType(entities.get(0).getNodePostType());
                                            //tsKvMeteringDevice96.setCreatedTime(result.getLastTimestamp());
                                            //tsKvMeteringDevice96.setUpdateTime(new Date());
                                            tsKvMeteringDevice96.setAvgValue(result.getAvg());
                                            tsKvMeteringDevice96.setMinValue(result.getMin());
                                            tsKvMeteringDevice96.setMaxValue(result.getMax());
                                            tsKvMeteringDevice96.setFirstValue(result.getFirstValue());
                                            tsKvMeteringDevice96.setLastValue(result.getLastValue());
                                            tsKvMeteringDevice96.setColValue(result.getLastValue());
                                            tsKvMeteringDevice96.setTs(result.getLastTimestamp());
                                            iotTsKvMeteringDevice96s.add(tsKvMeteringDevice96);
                                        } catch (Exception ex) {
                                            System.out.println(ex.fillInStackTrace());
                                        }
                                    }
                                    if (iotTsKvMeteringDevice96s != null && !iotTsKvMeteringDevice96s.isEmpty()) {
                                        iotTsKvMeteringDevice96Repository.saveAll(iotTsKvMeteringDevice96s);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    @Scheduled(initialDelay = 1000 * 5, fixedDelay = 60 * 1000 * 5)
    @Async
    public void energy96Slicing() {
        SimpleDateFormat fmt_ymdhm = new SimpleDateFormat("yyyyMMddHHmm");
        fmt_ymdhm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_ymd = new SimpleDateFormat("yyyy-MM-dd");
        fmt_ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        SimpleDateFormat fmt_hms = new SimpleDateFormat("HH:mm:ss");
        fmt_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        System.out.println("开始energy96Slicing");

        List<IotTsKvEnergyLastWeekView> energyEntity = energyLastWeekRepository.findAllEnergyInterval3days();

        Map<String, List<IotTsKvEnergyLastWeekView>> countMap = energyEntity.stream().
                collect(Collectors.groupingBy((px) -> px.getPointSn()));

        for (String l : countMap.keySet()) {

            List<IotTsKvEnergyLastWeekView> entities = countMap.get(l).stream()
                    .sorted(Comparator.comparing(IotTsKvEnergyLastWeekView::getTs))
                    .collect(Collectors.toList());;

            if (entities != null && !entities.isEmpty() && entities.size() > 1) {

                IotTsKvEnergyLastWeekView first = entities.get(0);
                IotTsKvEnergyLastWeekView end = entities.get(entities.size() - 1);

                List<String> dateStr = Slice96Util.split15Minutes(first.getTs(), end.getTs());

                List<EnergyUnit> energyUnitList = new ArrayList<>();

                Iterator<String> timeIterator = dateStr.iterator();
                Iterator<IotTsKvEnergyLastWeekView> enIterator = entities.iterator();

                EnergyUnit lastUnit = null;
                while (timeIterator.hasNext()) {
                    String timeNext = timeIterator.next();
                    EnergyUnit energyUnit = new EnergyUnit();
                    energyUnit.setDataStr(timeNext);

                    if (lastUnit != null) {
                        if (lastUnit.getRight().getTs().compareTo(TimeUtil.stringToDate(timeNext)) < 0) {
                            energyUnit.setLeft(lastUnit.getRight());
                        } else {
                            energyUnit.setLeft(lastUnit.getLeft());
                            energyUnit.setRight(lastUnit.getRight());
                            energyUnitList.add(energyUnit);
                            continue;
                        }

                    }

                    while (enIterator.hasNext()) {
                        IotTsKvEnergyLastWeekView energyNext = enIterator.next();

                        if (energyNext.getTs().compareTo(TimeUtil.stringToDate(timeNext)) < 0) {
                            energyUnit.setLeft(energyNext);
                        } else {
                            energyUnit.setRight(energyNext);
                            lastUnit = energyUnit;
                            energyUnitList.add(energyUnit);
                            break;
                        }
                    }
                }

                List<IotTsKvMeteringDevice96> iotTsKvMeteringDevice96s = new ArrayList<>();
                AtomicReference<IotTsKvMeteringDevice96> cachedLastEnergyValue = new AtomicReference<>();

                for (EnergyUnit d : energyUnitList) {
                    try {
                        IotTsKvEnergyLastWeekView left = d.getLeft();
                        if (left != null) {

                            Date count_date_time = TimeUtil.stringToDate(d.getDataStr());
                            Date count_date = fmt_ymd.parse(fmt_ymd.format(count_date_time));
                            Date count_time = fmt_hms.parse(fmt_hms.format(count_date_time));

                            String id = left.getDeviceSn() + "_" + left.getPointSn() + "_" + left.getPointDesc() + "_" + fmt_ymdhm.format(count_date_time);

                            Minutes15Model minutes15Model = TimeUtil.convert15MinutesStr(count_date_time);


                            IotTsKvMeteringDevice96 tsKvMeteringDevice96 = new IotTsKvMeteringDevice96();
                            tsKvMeteringDevice96.setId(id);
                            tsKvMeteringDevice96.setTotalPowerEnergy(d.calculate());
                            tsKvMeteringDevice96.setHTotalUse(0d);

                            if (cachedLastEnergyValue.get() == null) {
                                cachedLastEnergyValue.set(tsKvMeteringDevice96);
                            } else {
                                IotTsKvMeteringDevice96 last = cachedLastEnergyValue.get();
                                double value = tsKvMeteringDevice96.getTotalPowerEnergy() - last.getTotalPowerEnergy();
                                last.setHTotalUse(value);
                                cachedLastEnergyValue.set(tsKvMeteringDevice96);
                            }
                            tsKvMeteringDevice96.setProvinceRegionId(left.getProvinceRegionId());
                            tsKvMeteringDevice96.setCityRegionId(left.getCityRegionId());
                            tsKvMeteringDevice96.setCountyRegionId(left.getCountyRegionId());
                            tsKvMeteringDevice96.setNodeId(left.getNodeId());
                            tsKvMeteringDevice96.setLongitude(left.getLongitude());
                            tsKvMeteringDevice96.setLatitude(left.getLatitude());
                            tsKvMeteringDevice96.setSystemId(left.getSystemId());
                            tsKvMeteringDevice96.setDeviceSn(left.getDeviceSn());
                            tsKvMeteringDevice96.setPointSn(left.getPointSn());
                            tsKvMeteringDevice96.setConfigKey(left.getDeviceConfigKey());
                            //h_total_use
                            //total_power_energy
                            //tsKvMeteringDevice96.setTotalPowerEnergyBase((double) 0);
                            tsKvMeteringDevice96.setPointDesc(left.getPointDesc());
                            tsKvMeteringDevice96.setPointUnit(left.getPointUnit());
                            tsKvMeteringDevice96.setCountDate(count_date);
                            tsKvMeteringDevice96.setCountTime(count_time);
                            tsKvMeteringDevice96.setCountDataTime(count_date_time);
                            tsKvMeteringDevice96.setTimeScope(minutes15Model.getTimeScope());
                            tsKvMeteringDevice96.setNodePostType(left.getNodePostType());
                            //tsKvMeteringDevice96.setCreatedTime(TimeUtil.stringToDate(d.getDataStr()));
                            //tsKvMeteringDevice96.setUpdateTime(new Date());
                            //tsKvMeteringDevice96.setAvgValue(0d);
                            //tsKvMeteringDevice96.setMinValue(0d);
                            //tsKvMeteringDevice96.setMaxValue(0d);
                            //tsKvMeteringDevice96.setFirstValue(0d);
                            //tsKvMeteringDevice96.setLastValue(0d);
                            //tsKvMeteringDevice96.setColValue(0d);
                            tsKvMeteringDevice96.setTs(TimeUtil.stringToDate(d.getDataStr()));
                            iotTsKvMeteringDevice96s.add(tsKvMeteringDevice96);
                        }
                    } catch (Exception ex) {
                        System.out.println(ex.fillInStackTrace());
                    }
                }

                if (iotTsKvMeteringDevice96s != null && !iotTsKvMeteringDevice96s.isEmpty()) {
                    iotTsKvMeteringDevice96Repository.saveAll(iotTsKvMeteringDevice96s);
                }
            }
        }
    }
}
