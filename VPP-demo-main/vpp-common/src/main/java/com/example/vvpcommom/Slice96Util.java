package com.example.vvpcommom;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 *
 * */
public class Slice96Util {

    public static List<String> get96Times(String ymd) {

        LocalDate date = LocalDate.parse(ymd);
        LocalDateTime startTime = date.atStartOfDay();
        // 创建一个时间戳列表
        List<String> timestamps = new ArrayList<>();

        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 生成96个时间点的时间戳，每15分钟一个时间点
        for (int i = 0; i < 96; i++) {
            LocalDateTime time = startTime.plusMinutes(i * 15);
            String formattedTime = time.format(formatter);
            timestamps.add(formattedTime);
        }
        return timestamps;
    }


    public static Map<Integer,String> getMap96Times(String ymd) {

        LocalDate date = LocalDate.parse(ymd);
        LocalDateTime startTime = date.atStartOfDay();
        // 创建一个时间戳列表
        Map<Integer,String> timestamps = new ConcurrentHashMap<>();

        // 时间格式化器
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 生成96个时间点的时间戳，每15分钟一个时间点
        for (int i = 0; i < 96; i++) {
            LocalDateTime time = startTime.plusMinutes(i * 15);
            String formattedTime = time.format(formatter);
            timestamps.put(i,formattedTime);
        }
        return timestamps;
    }

    public static List<String> split15Minutes(Date start, Date end) {
        List<String> result  = new ArrayList<>();
        LocalDateTime startLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(start.getTime()), ZoneOffset.of("+8"));
        LocalDateTime endLt = LocalDateTime.ofInstant(Instant.ofEpochMilli(end.getTime()), ZoneOffset.of("+8"));

        LocalDateTime lastQuarter = startLt.truncatedTo(ChronoUnit.HOURS)
                    .plusMinutes(15 * (startLt.getMinute() / 15));

        while (lastQuarter.isBefore(endLt)){
            result.add(TimeUtil.dateFormat(new Date(lastQuarter.toInstant(ZoneOffset.ofHoursMinutes(+8, 0)).toEpochMilli())));
            lastQuarter = lastQuarter.plusMinutes(15);
        }

        return result;

    }
}
