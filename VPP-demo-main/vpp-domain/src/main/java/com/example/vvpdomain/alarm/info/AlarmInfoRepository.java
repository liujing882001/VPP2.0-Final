package com.example.vvpdomain.alarm.info;

import com.example.vvpdomain.alarm.rule.AlarmRule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;


@Repository
public interface AlarmInfoRepository extends JpaRepository<AlarmInfo, String>, JpaSpecificationExecutor<AlarmInfo> {

	AlarmInfo findByAlarmRuleAndStationIdAndAlarmStatusNot(AlarmRule rule, String nodeId, int alarmStatus);

	List<AlarmInfo> findAllByAlarmStatusNot(int alarmStatus);

	AlarmInfo findByNodeName(String nodeName);

	@Query("SELECT a FROM AlarmInfo a WHERE (:alarmLevel IS NULL OR a.alarmLevel = :alarmLevel) AND (:alarmStatus IS NULL OR a.alarmStatus = :alarmStatus) AND a.stationId = :stationId AND a.startTime >= :startTime AND a.startTime <= :endTime ORDER BY a.startTime desc ")
	Page<AlarmInfo> findAlarmInfoWin(@Param("stationId") String stationId,
									 @Param("alarmStatus") Integer alarmStatus,
									 @Param("alarmLevel") Integer alarmLevel,
									 @Param("startTime") Timestamp startTime,
									 @Param("endTime") Timestamp  endTime, Pageable pageable);

	@Query("SELECT a FROM AlarmInfo a WHERE (:alarmLevel IS NULL OR a.alarmLevel = :alarmLevel) AND (:alarmStatus IS NULL OR a.alarmStatus = " +
			":alarmStatus) AND a.stationId in (:stationId) AND a.startTime >= :startTime AND a.startTime <= :endTime ORDER BY a.startTime desc ")
	Page<AlarmInfo> findAlarmInfoWin(@Param("stationId") List<String> stationId,
	                                 @Param("alarmStatus") Integer alarmStatus,
	                                 @Param("alarmLevel") Integer alarmLevel,
	                                 @Param("startTime") Timestamp startTime,
	                                 @Param("endTime") Timestamp  endTime, Pageable pageable);


}
