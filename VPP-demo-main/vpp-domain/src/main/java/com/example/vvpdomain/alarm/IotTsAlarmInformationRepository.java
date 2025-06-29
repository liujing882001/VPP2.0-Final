package com.example.vvpdomain.alarm;

import com.example.vvpdomain.entity.IotTsAlarmInformation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public interface IotTsAlarmInformationRepository extends JpaRepository<IotTsAlarmInformation,String> {

    @Query("SELECT a FROM IotTsAlarmInformation a WHERE (:alarmLevel IS NULL OR a.alarmLevel = :alarmLevel) AND (:alarmType IS NULL OR a.alarmType = :alarmType) AND (:alarmStatus IS NULL OR a.alarmStatus = :alarmStatus) AND a.stationName = :stationName AND a.startTs >= :startTime AND a.startTs <= :endTime ORDER BY a.startTs desc ")
    Page<IotTsAlarmInformation> findAlarmLogByNotNullWin(@Param("stationName") String stationName, @Param("alarmStatus") String alarmStatus, @Param("alarmLevel") String alarmLevel, @Param("alarmType") String alarmType, @Param("startTime") Timestamp startTime, @Param("endTime") Timestamp endTime, Pageable pageable);


}
