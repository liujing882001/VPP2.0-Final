package com.example.vvpweb.demand.model;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author maoyating
 * @description 需求响应任务
 * @date 2022-08-09
 */
@Data
public class DemandBoardCancelModel implements Serializable {

    /**
     * 任务Id
     */
    private String respId;

    /**
     * 任务编码
     */
    private Long taskCode;

    /**
     * 响应时段(开始)
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private Date rsTime;

    /**
     * 响应时段(结束)
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private Date reTime;

    /**
     * 响应日期
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date rsDate;

    /**
     * 响应负荷，单位（kW）
     */
    private Double respLoad;

    /**
     * 响应补贴（元/kWh）
     */
    private Double respSubsidy;

    /**
     * 响应类型(1-削峰响应 2-填谷响应)
     */
    private Integer respType;

    /**
     * 响应级别(1-日前 2-小时 3-分钟 4-秒级)
     */
    private Integer respLevel;

    /**
     * 申报负荷
     */
    private Double declareLoad;

    /**
     * 预估收益
     */
    private Double profit;

    /**
     * 国网返回的收益
     */
    private Double volumeProfit;

}