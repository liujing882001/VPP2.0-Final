package com.example.vvpscheduling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Date;

/**
 * @description 可调负荷预测
 * @date 2022-07-01
 */
@Entity
@Getter
@Setter
public class BaseLineLoadModel {

    private String id;

    /**
     * 节点id
     */
    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 负载
     */
    private String realValue;

    /**
     * AI超短期预测值
     */
    private String ultraShortTermForecastValue;

    /**
     * AI目前预测值
     */
    private String currentForecastValue;

    /**
     * AI基线负荷值
     */
    private String baselineLoadValue;


    /**
     * yyy-mm-dd hh:mm:ss
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date countDataTime;

    /**
     * created_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createdTime;

    /**
     * update_time
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * AI基线负荷值（商汤外，自行预测值）
     */
    private String baselineLoadValueOther;

    /**
     * 统计日期
     */
    private String countData;

    /**
     * 统计时间
     */
    private String countTime;

    /**
     * 统计年份
     */
    private Integer countYear;
}