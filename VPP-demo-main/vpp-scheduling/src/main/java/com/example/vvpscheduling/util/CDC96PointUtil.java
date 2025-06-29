package com.example.vvpscheduling.util;

import com.example.vvpcommom.TimeUtil;
import com.example.vvpdomain.view.IotTsKvLoadLastWeekView;
import com.example.vvpscheduling.model.slicing.SliceResult;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class CDC96PointUtil {
    private static SimpleDateFormat fmt_ymdhms = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat fmt_hm = new SimpleDateFormat("HH:mm");
    public static List<SliceResult> processLoadData(List<IotTsKvLoadLastWeekView> powerDataList, Date singleTime) {

        fmt_ymdhms.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        fmt_hm.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        LocalDateTime startTime = LocalDateTime.ofInstant(TimeUtil.getStartOfDay(singleTime).toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = LocalDateTime.ofInstant(TimeUtil.getStartOfDay(TimeUtil.dateAddDay(singleTime, 1)).toInstant(), ZoneId.systemDefault());


        List<SliceResult> results = new ArrayList<>();
        Map<Integer, List<IotTsKvLoadLastWeekView>> slices = new TreeMap<>();

        long totalMinutes = ChronoUnit.MINUTES.between(startTime, endTime);
        long sliceMinutes = totalMinutes / 96;


        for (IotTsKvLoadLastWeekView data : powerDataList) {
            long minutesFromStart = ChronoUnit.MINUTES.between(startTime, LocalDateTime.ofInstant(data.getTs().toInstant(), ZoneId.systemDefault()));
            int sliceIndex = (int) (minutesFromStart / sliceMinutes);
            slices.computeIfAbsent(sliceIndex, k -> new ArrayList<>()).add(data);
        }

        for (int i = 0; i < 96; i++) {
            try {
                List<IotTsKvLoadLastWeekView> sliceData = slices.getOrDefault(i, new ArrayList<>());

                LocalDateTime time = startTime.plusMinutes(i * 15);
                Date dt =fmt_ymdhms.parse(time.format(formatter));
                if (sliceData.isEmpty()) {

                    SliceResult result = new SliceResult();
                    result.setSliceIndex(i);
                    result.setSliceIndex(i);
                    result.setMax(0d);
                    result.setMin(0d);
                    result.setAvg(0d);
                    result.setFirstValue(0d);
                    result.setLastValue(0d);
                    result.setSliceSupplementRecording(true);
                    result.setLastTimestamp(TimeUtil.dateAddSeconds(dt,15));
                    result.setCountDataTime(TimeUtil.dateAddMinutes(dt,15));

                    String timeScope=fmt_hm.format(dt)+"-"+fmt_hm.format(TimeUtil.dateAddMinutes(dt,15));
                    result.setTimeScope(timeScope);
                    results.add(result);


                } else {
                    OptionalDouble optionalMax = sliceData.stream()
                            .mapToDouble(d -> Double.parseDouble(d.getPointValue()))
                            .max();
                    Double max = optionalMax.isPresent() ? optionalMax.getAsDouble() : null;
                    OptionalDouble optionalMin = sliceData.stream()
                            .mapToDouble(d -> Double.parseDouble(d.getPointValue()))
                            .min();
                    Double min = optionalMin.isPresent() ? optionalMin.getAsDouble() : null;
                    OptionalDouble optionalAvg = sliceData.stream()
                            .mapToDouble(d -> Double.parseDouble(d.getPointValue()))
                            .average();
                    Double avg = optionalAvg.isPresent() ? optionalAvg.getAsDouble() : null;
                    double lastData = Double.parseDouble(sliceData.get(sliceData.size() - 1).getPointValue());
                    double firstData = Double.parseDouble(sliceData.get(0).getPointValue());
                    Date lastTimestamp = sliceData.get(sliceData.size() - 1).getTs();

                    SliceResult result = new SliceResult();
                    result.setSliceIndex(i);
                    result.setMax(max);
                    result.setMin(min);
                    result.setAvg(avg);
                    result.setFirstValue(lastData);
                    result.setLastValue(firstData);
                    result.setLastTimestamp(lastTimestamp);
                    result.setCountDataTime(TimeUtil.dateAddMinutes(dt,15));
                    result.setSliceSupplementRecording(false);
                    String timeScope=fmt_hm.format(dt)+"-"+fmt_hm.format(TimeUtil.dateAddMinutes(dt,15));
                    result.setTimeScope(timeScope);
                    results.add(result);
                }
            } catch (Exception ex) {
            }
        }
        return results;
    }

    public static List<SliceResult> processList(List<SliceResult> list) {
        int firstFalseIndex = -1;
        int lastFalseIndex = -1;

        // 找到第一个和最后一个 sliceSupplementRecording = false 的索引
        for (int i = 0; i < list.size(); i++) {
            if (!list.get(i).isSliceSupplementRecording()) {
                if (firstFalseIndex == -1) {
                    firstFalseIndex = i;
                }
                lastFalseIndex = i;
            }
        }

        // 如果没有找到 sliceSupplementRecording = false 的元素，返回原列表
        if (firstFalseIndex == -1 || lastFalseIndex == -1) {
            return list;
        }

        // 提取第一个和最后一个 sliceSupplementRecording = false 的索引之间的数据
        return list.subList(firstFalseIndex, lastFalseIndex + 1);

    }
}
