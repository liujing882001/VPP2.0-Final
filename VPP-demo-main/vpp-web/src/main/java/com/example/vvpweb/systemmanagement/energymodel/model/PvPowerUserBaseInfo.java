package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PvPowerUserBaseInfo implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 电力用户购电折扣比例 开始时间 yyyy
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date powerUserStartTime;
    /**
     * 电力用户购电折扣比例 年限
     */
    private int powerUserExpiryDate;
}
