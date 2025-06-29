//package com.example.vvpscheduling;
//
//
//import com.alibaba.fastjson.JSON;
//import com.example.vvpdomain.DevicePointRepository;
//import com.example.vvpdomain.IotTsKvMeteringDevice96Repository;
//import com.example.vvpdomain.IotTsKvRepository;
//import com.example.vvpdomain.entity.*;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.Resource;
//import javax.persistence.criteria.Predicate;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZoneOffset;
//import java.util.*;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//@Component
//@EnableAsync
//@Slf4j
//public class IotDataSlicingJob {
//
//    @Resource
//    private IotTsKvRepository iotTsKvRepository;
//    @Resource
//    private DevicePointRepository devicePointRepository;
//    @Resource
//    private IotTsKvMeteringDevice96Repository iotTsKvMeteringDevice96Repository;
//
//    //每天0点1分开始，每三十分钟执行一次任务
//    @Scheduled(cron="0 1/30 * * * *")
////    @Async
//    public void dataSlicing() throws ParseException {
//        LocalDateTime updateTime = LocalDateTime.now(ZoneOffset.ofHours(8));
////        LocalDateTime updateTime = LocalDateTime.of(2024, 6, 25, 5, 1, 0);
//        log.info("开始切片：{}",updateTime);
//        LocalDateTime now = updateTime.withSecond(0).withNano(0);
//        LocalDateTime endLocalTime = now.minusMinutes(now.getMinute() % 15);
//        LocalDateTime startLocalTime = endLocalTime.minusMinutes(15);
//        LocalDateTime queryStartLocalTime = startLocalTime.minusMinutes(15);
//        LocalDateTime queryEndLocalTime = endLocalTime.plusMinutes(15);
//
//
//        Date updateDate = Date.from(updateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        Date startDate = Date.from(startLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        Date endDate = Date.from(endLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        Date queryStartDate = Date.from(queryStartLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//        Date queryEndDate = Date.from(queryEndLocalTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant());
//
//        SimpleDateFormat ymd_hmsS = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
//        ymd_hmsS.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//
//        SimpleDateFormat ymd_hms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        ymd_hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//
//        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");
//        ymd.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//
//        SimpleDateFormat hms = new SimpleDateFormat("HH:mm:ss");
//        hms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//
//        SimpleDateFormat hm = new SimpleDateFormat("HH:mm");
//        hm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
//        Date createdTime = ymd_hmsS.parse(ymd_hmsS.format(endDate));
//        Date count_date_time = ymd_hms.parse(ymd_hms.format(endDate));
//        Date count_date = ymd.parse(ymd.format(endDate));
//        Date count_time = hms.parse(hms.format(endDate));
//        String timeScope = hm.format(startDate) +"-"+ hm.format(endDate);
//
//        Specification<IotTsKv> spec = (root, criteriaQuery, cb) -> {
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(cb.greaterThan(root.get("ts"), queryStartDate));
//            predicates.add(cb.lessThanOrEqualTo(root.get("ts"), queryEndDate));
//            predicates.add(cb.or(
//                    cb.equal(root.get("pointDesc"), "load"),
//                    cb.equal(root.get("pointDesc"), "energy")
//            ));
//            predicates.add(cb.equal(root.get("msgType"), "MSG"));
//            criteriaQuery.where(cb.and(predicates.toArray(new Predicate[predicates.size()])));
////            criteriaQuery.orderBy(cb.asc(root.get("ts")));
//            return criteriaQuery.getRestriction();
//        };
//        List<IotTsKv> iotTsKvs = iotTsKvRepository.findAll(spec);
//        Map<String, List<IotTsKv>> groupByPD = iotTsKvs.stream().collect(Collectors.groupingBy(IotTsKv::getPointDesc));
//
//        Map<String, DevicePoint> dpGroup = devicePointRepository.findAll().stream()
//                .collect(Collectors.groupingBy(
//                        dp -> dp.getDeviceSn() + dp.getPointSn(),
//                        Collectors.collectingAndThen(
//                                Collectors.toList(),
//                                list -> list.get(0)
//                        )
//                ));
//
//        for (Map.Entry<String, List<IotTsKv>> entry : groupByPD.entrySet()) {
//            String pointDesc = entry.getKey();
//            Map<String,List<IotTsKv>> dataByITK = entry.getValue().stream().collect(Collectors.groupingBy(itk -> itk.getDeviceSn() + itk.getPointSn()));
//            for (Map.Entry<String, List<IotTsKv>> entryITK : dataByITK.entrySet()) {
//                String itkKey = entryITK.getKey();
//                DevicePoint devicePoint = dpGroup.get(itkKey);
//                Node node = devicePoint.getDevice().getNode();
//                SysDictType systemType = devicePoint.getDevice().getSystemType();
//                Device device = devicePoint.getDevice();
//
//                List<IotTsKv> itkListAll = entryITK.getValue();
//
//                List<IotTsKv> itkvListNoNull = itkListAll.stream()
//                        .sorted(Comparator.comparing(IotTsKv::getTs))
//                        .filter(kv -> {
//                            try {
//                                if (kv.getPointValue() == null || kv.getPointValue().isEmpty()) return false;
//                                Double.valueOf(kv.getPointValue());
//                                return true;
//                            } catch (NumberFormatException e) {
//                                return false;
//                            }
//                        })
//                        .collect(Collectors.toList());
//                List<IotTsKv> itkList;
//                IotTsKv itkListOutLast= null;
//                IotTsKv itkListOutBefore= null;
//
//
//                OptionalInt position = IntStream.range(0, itkvListNoNull.size())
//                        .filter(i -> itkvListNoNull.get(i).getTs().getTime() > endDate.getTime())
//                        .findFirst();
//                OptionalInt position1 = IntStream.range(0, itkvListNoNull.size())
//                        .filter(i -> itkvListNoNull.get(i).getTs().getTime() <= startDate.getTime())
//                        .findFirst();
//                if (position.isPresent()) {
//                    itkListOutLast = itkvListNoNull.get(position.getAsInt());
//                }
//                if (position1.isPresent()) {
//
//                    itkListOutBefore = itkvListNoNull.get(position1.getAsInt());
//                }
//                itkList = itkvListNoNull.stream()
//                        .filter(i -> i.getTs().getTime() > startDate.getTime() && i.getTs().getTime() <= endDate.getTime()).collect(Collectors.toList());
//
//                List<Double> itkListNoNull = itkList.stream()
//                        .map(mtd -> Double.parseDouble(mtd.getPointValue()))
//                        .collect(Collectors.toList());
//
//                DoubleSummaryStatistics stats = new DoubleSummaryStatistics();
//
//
//                Double colValue = null;
//                if (itkListOutLast != null) {
//                    if (!itkList.isEmpty()) {
//                        IotTsKv iotTsKvFirst = itkList.get(0);
//                        colValue = (Double.parseDouble(itkListOutLast.getPointValue()) - Double.parseDouble(iotTsKvFirst.getPointValue()))
//                                / (itkListOutLast.getTs().getTime() - iotTsKvFirst.getTs().getTime())
//                                * (endDate.getTime() - iotTsKvFirst.getTs().getTime()) + Double.parseDouble(iotTsKvFirst.getPointValue());
//                    }
//                } else if (itkListOutBefore != null) {
//                    IotTsKv iotTsKvFirst;
//                    if (!itkList.isEmpty()) {
//                        iotTsKvFirst = itkList.get(0);
//                    } else {
//                        iotTsKvFirst = itkListOutBefore;
//                    }
//                    colValue = (Double.parseDouble(itkListOutBefore.getPointValue()) - Double.parseDouble(iotTsKvFirst.getPointValue()))
//                            / (itkListOutBefore.getTs().getTime() - iotTsKvFirst.getTs().getTime())
//                            * (endDate.getTime() - iotTsKvFirst.getTs().getTime()) + Double.parseDouble(iotTsKvFirst.getPointValue());
//                }
//                Double h_total = null;
//                Double t_PowerEnergy = null;
//                if (pointDesc.equals("load")) {
//                    stats = itkListNoNull.stream()
//                            .mapToDouble(Double::doubleValue)
//                            .summaryStatistics();
//                    if (!itkListNoNull.isEmpty()) {
//                        h_total = itkListNoNull.get(0);
//                    }
//                } else if (pointDesc.equals("energy")) {
//                    stats = IntStream.range(1, itkListNoNull.size())
//                            .mapToDouble(i -> itkListNoNull.get(i) - itkListNoNull.get(i - 1))
//                            .boxed()
//                            .collect(Collectors.toList()).stream()
//                            .mapToDouble(Double::doubleValue)
//                            .summaryStatistics();
//                    if (stats.getCount() >= 2) {
//                        int endIndex = IntStream.range(1, itkListNoNull.size())
//                                .filter(i -> Math.signum(itkListNoNull.get(i) - itkListNoNull.get(i - 1))
//                                        != Math.signum(itkListNoNull.get(1) - itkListNoNull.get(0)))
//                                .findFirst()
//                                .orElse(itkListNoNull.size()) - 1;
//                        h_total = colValue == null ? itkListNoNull.get(0) - itkListNoNull.get(endIndex): colValue - itkListNoNull.get(endIndex);
//                    } else if (colValue != null){
//                        h_total = colValue - itkListNoNull.get(0);
//                    } else if (itkListOutBefore != null){
//                        h_total = itkListNoNull.get(0) - Double.parseDouble(itkListOutBefore.getPointValue());
//
//                    }
//                    t_PowerEnergy = itkListNoNull.get(0);
//
//                }
//                //均值、最值、首尾值
//                Double avg = stats.getCount() > 0 ? stats.getAverage() : null;
//                Double min = stats.getCount() > 0 ? stats.getMin() : null;
//                Double max = stats.getCount() > 0 ? stats.getMax() : null;
//                Double first =  stats.getCount() > 0 ? itkListNoNull.get(0) : null;
//                Double last = stats.getCount() > 0 ? itkListNoNull.get(itkListNoNull.size() - 1) : null;
//
//                IotTsKvMeteringDevice96 tsKvMeteringDevice96 = null;
//                String id = devicePoint.getDeviceSn() + "_"
//                        + devicePoint.getPointSn() + "_"
//                        + devicePoint.getPointDesc() + "_"
//                        + new SimpleDateFormat("yyyyMMddHHmm").format(endDate);
////                String id = "ceshide11111111111";
//                tsKvMeteringDevice96 = new IotTsKvMeteringDevice96();
//                tsKvMeteringDevice96.setId(id);
//                tsKvMeteringDevice96.setCountDate(count_date);
//                tsKvMeteringDevice96.setCountDataTime(count_date_time);
//
//                tsKvMeteringDevice96.setCountTime(count_time);
//                tsKvMeteringDevice96.setTimeScope(timeScope);
//                tsKvMeteringDevice96.setCreatedTime(createdTime);
//
//                tsKvMeteringDevice96.setHTotalUse(h_total);
//                tsKvMeteringDevice96.setColValue(colValue);
//                tsKvMeteringDevice96.setTotalPowerEnergy(t_PowerEnergy);
//
//                tsKvMeteringDevice96.setAvgValue(avg);
//                tsKvMeteringDevice96.setMinValue(min);
//                tsKvMeteringDevice96.setMaxValue(max);
//                tsKvMeteringDevice96.setFirstValue(first);
//                tsKvMeteringDevice96.setLastValue(last);
//
//                if (tsKvMeteringDevice96 != null) {
//                    tsKvMeteringDevice96.setProvinceRegionId(node.getProvinceRegionId());
//                    tsKvMeteringDevice96.setCityRegionId(node.getCityRegionId());
//                    tsKvMeteringDevice96.setCountyRegionId(node.getCountyRegionId());
//                    tsKvMeteringDevice96.setNodeId(node.getNodeId());
//                    tsKvMeteringDevice96.setNodePostType(node.getNodePostType());
//                    tsKvMeteringDevice96.setLatitude(node.getLatitude());
//                    tsKvMeteringDevice96.setLongitude(node.getLongitude());
//                    tsKvMeteringDevice96.setSystemId(systemType.getSystemId());
//                    tsKvMeteringDevice96.setDeviceSn(device.getDeviceSn());
//                    tsKvMeteringDevice96.setConfigKey(devicePoint.getDeviceConfigKey());
//                    tsKvMeteringDevice96.setPointSn(devicePoint.getPointSn());
//                    tsKvMeteringDevice96.setPointUnit(devicePoint.getPointUnit());
//                    tsKvMeteringDevice96.setPointDesc(pointDesc);
//                    iotTsKvMeteringDevice96Repository.save(tsKvMeteringDevice96);
//
//                }
//
//            }
//
//        }
//    }
//}
