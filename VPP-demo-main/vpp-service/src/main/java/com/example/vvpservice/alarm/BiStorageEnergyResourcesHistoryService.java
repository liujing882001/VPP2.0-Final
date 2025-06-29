package com.example.vvpservice.alarm;


import com.example.vvpcommom.ResponseResult;
import com.example.vvpdomain.BiStorageEnergyResourcesHistoryRepository;
import com.example.vvpdomain.StationNodeRepository;
import com.example.vvpdomain.alarm.info.AlarmInfo;
import com.example.vvpdomain.alarm.info.AlarmInfoRepository;
import com.example.vvpdomain.entity.BiStorageEnergyResourcesHistory;
import com.example.vvpdomain.entity.StationNode;
import com.example.vvpservice.alarm.message.AliyunSmsThrService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.ZoneId;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class BiStorageEnergyResourcesHistoryService {
    @Autowired
    private BiStorageEnergyResourcesHistoryRepository biStorageEnergyResourcesHistoryRepository;

    @Autowired
    private AlarmInfoRepository alarmInfoRepository;
    @Autowired
    private StationNodeRepository stationNodeRepository;

    @Autowired
    private AliyunSmsThrService aliyunSmsThrService;
    /**
     * 第一部分：获取所有数据
     *
     * @return
     */
    public List<BiStorageEnergyResourcesHistory> findAll() {
        return biStorageEnergyResourcesHistoryRepository.findAll();
    }


    /**
     * 第二部分计算持续时长-这个方法是成功的
     *
     * @return
     */
    public static String calculateDuration(Date alarmStartTime, Date alarmEndTime) {
        if (alarmStartTime != null && alarmEndTime == null) {
            Instant startInstant  = alarmStartTime.toInstant().atZone(ZoneId.systemDefault()).toInstant().atZone(java.time.ZoneId.systemDefault()).toInstant();
            Instant nowInstant  = Instant.now();
            Duration duration = Duration.between(startInstant, nowInstant);
            long minutes = duration.toMinutes();
            if (minutes < 60) {
                return minutes + " 分钟";
            } else {
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;
                if (hours < 24) {
                    return hours + " 小时 " + remainingMinutes + " 分钟";
                } else {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    remainingMinutes = duration.toMinutes() % 60;
                    return days + " 天 " + remainingHours + " 小时 " + remainingMinutes + " 分钟";
                }
            }
        } else if (alarmStartTime != null && alarmEndTime != null) {
            Instant startInstant = alarmStartTime.toInstant().atZone(ZoneId.systemDefault()).toInstant().atZone(java.time.ZoneId.systemDefault()).toInstant();
            Instant endInstant = alarmEndTime.toInstant().atZone(ZoneId.systemDefault()).toInstant().atZone(java.time.ZoneId.systemDefault()).toInstant();
            Duration duration = Duration.between(startInstant, endInstant);
            long minutes = duration.toMinutes();
            if (minutes < 60) {
                return minutes + " 分钟";
            } else {
                long hours = minutes / 60;
                long remainingMinutes = minutes % 60;
                if (hours < 24) {
                    return hours + " 小时 " + remainingMinutes + " 分钟";
                } else {
                    long days = hours / 24;
                    long remainingHours = hours % 24;
                    remainingMinutes = duration.toMinutes() % 60;
                    return days + " 天 " + remainingHours + " 小时 " + remainingMinutes + " 分钟";
                }
            }
        } else {
            return null;
        }
    }

}
