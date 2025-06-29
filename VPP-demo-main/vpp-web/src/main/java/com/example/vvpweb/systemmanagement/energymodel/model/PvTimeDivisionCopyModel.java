package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PvTimeDivisionCopyModel implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 电价来源
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date fromEffectiveDate;


    private String toNodeId;
    /**
     * 系统id
     */
    private String toSystemId;


    /**
     * 电价目标
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date toEffectiveDate;

}
