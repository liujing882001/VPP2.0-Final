package com.example.vvpdomain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class RAPower96DTO {

    private String nodeId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-mm-dd")
    private Date effectiveDate;
    private String sTime;
    private String strategy;
    public RAPower96DTO(){}
    public RAPower96DTO(String nodeId, Date effectiveDate, String sTime,String strategy) {
        this.nodeId = nodeId;
        this.effectiveDate = effectiveDate;
        this.sTime = sTime;
        this.strategy = strategy;
    }
}
