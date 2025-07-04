package com.example.vvpdomain.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 日历表-基线负荷
 * @date 2022-08-09
 */
@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
@Table(name = "demand_calendar")
public class DemandCalendar implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     * 表字段： demand_calendar.date
     */
    @Id
    @Column(name = "date")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;

    /**
     * 年
     * 表字段： demand_calendar.year
     */
    @Column(name = "year")
    private Integer year;

    /**
     * 月
     * 表字段： demand_calendar.year
     */
    @Column(name = "month")
    private Integer month;

    /**
     * 日
     * 表字段： demand_calendar.day
     */
    @Column(name = "day")
    private Integer day;



    /**
     * 周几（1-周一 2-周二 3-周三 4-周四 5-周五 6-周六 7-周日）
     * 表字段： demand_calendar.day_of_week
     */
    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    /**
     * 日期类型（1-工作日 2-非工作日 3-删除计算日 4-元旦 5-春节 6-清明节 7-劳动节 8-端午节 9-中秋节 10-国庆节）
     * 表字段： demand_calendar.date_type
     */
    @Column(name = "date_type")
    private Integer dateType;


    /**
     * 表字段： demand_calendar.update_by
     */
    @Column(name = "update_by")
    private String updateBy;

    /**
     * 表字段： demand_calendar.update_time
     */
    @LastModifiedDate
    @Column(name = "update_time")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public DemandCalendar() {

    }

    // Manual getter to ensure compilation
    public Integer getDateType() {
        return dateType;
    }

}