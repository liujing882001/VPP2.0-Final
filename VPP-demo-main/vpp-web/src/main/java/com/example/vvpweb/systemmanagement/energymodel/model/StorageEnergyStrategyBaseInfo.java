package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StorageEnergyStrategyBaseInfo implements Serializable {


    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;


    /**
     * 充放电策略开始时间 yyyy-MM
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date strategyStartTime;
    /**
     * 充放电策略年限
     */
    private int strategyExpiryDate;

}
