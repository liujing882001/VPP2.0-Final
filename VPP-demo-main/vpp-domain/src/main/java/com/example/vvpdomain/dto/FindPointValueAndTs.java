package com.example.vvpdomain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class FindPointValueAndTs {
    private String pointValue;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss.S")
    private Date ts;

    public FindPointValueAndTs(){}
    public FindPointValueAndTs(String pointValue, Date ts) {
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
