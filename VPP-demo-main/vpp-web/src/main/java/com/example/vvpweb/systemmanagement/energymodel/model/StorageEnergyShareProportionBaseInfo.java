package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StorageEnergyShareProportionBaseInfo implements Serializable {


    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 分层比例开始时间 yyyy
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date shareProportionStartTime;
    /**
     * 分层比例年限
     */
    private int shareProportionExpiryDate;
}
