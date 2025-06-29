package com.example.vvpweb.runschedule.runmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class YearMonthModel implements Serializable {

    String nodeId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    private Date yearMonth;
}
