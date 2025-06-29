package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PvTimeDivisionModel implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 生效时间 yyyy-mm
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date effectiveDate;
    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    private int number;
}
