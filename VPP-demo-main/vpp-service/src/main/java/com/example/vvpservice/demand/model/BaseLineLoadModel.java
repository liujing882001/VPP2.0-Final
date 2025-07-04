package com.example.vvpservice.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
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

    // Manual getters and setters to ensure compilation
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getSystemId() { return systemId; }
    public void setSystemId(String systemId) { this.systemId = systemId; }
    public String getRealValue() { return realValue; }
    public void setRealValue(String realValue) { this.realValue = realValue; }
    public Date getCountDataTime() { return countDataTime; }
    public void setCountDataTime(Date countDataTime) { this.countDataTime = countDataTime; }
    public String getCountData() { return countData; }
    public void setCountData(String countData) { this.countData = countData; }
    public String getCountTime() { return countTime; }
    public void setCountTime(String countTime) { this.countTime = countTime; }
    public Integer getCountYear() { return countYear; }
    public void setCountYear(Integer countYear) { this.countYear = countYear; }
}