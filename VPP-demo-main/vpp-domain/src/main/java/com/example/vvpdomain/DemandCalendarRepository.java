package com.example.vvpdomain;

import com.example.vvpdomain.entity.DemandCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author maoyating
 * @description 日历表-基线负荷
 * @date 2022-08-09
 */
@Repository
public interface DemandCalendarRepository extends JpaRepository<DemandCalendar, String>,
        JpaSpecificationExecutor<DemandCalendar> {

    @Query(value = "UPDATE demand_calendar " +
            "SET date_type = :dateType " +
            "WHERE date in (:dateList) ", nativeQuery = true)
    @Modifying(clearAutomatically = true)
    @Transactional
    void updateDateType(@Param("dateList") Collection<Date> dateList,@Param("dateType")Integer dateType);

    @Query(value = " select * from demand_calendar " +
            " where date = :queryDate ", nativeQuery = true)
    DemandCalendar findByDate(@Param("queryDate")Date queryDate);

    @Query(value = " select date from demand_calendar  where date_type=:dateType " +
            " and date < :date order by date desc limit :pageSize ", nativeQuery = true)
    List<Date> findByDateTypePageSize(@Param("dateType")Integer dateType,
                                      @Param("pageSize")Integer pageSize,
                                      @Param("date")Date date);

    @Query(value = " select date from demand_calendar  where date_type=:dateType " +
            " and year < :year order by date desc", nativeQuery = true)
    List<Date> findByDateTypeYear(@Param("dateType")Integer dateType,@Param("year")Integer year);

    @Query(value = "SELECT * FROM demand_calendar " +
            "WHERE date BETWEEN :sDate AND :eDate", nativeQuery = true)
    List<DemandCalendar> findByDateBetween(@Param("sDate") Date sDate, @Param("eDate") Date eDate);


}