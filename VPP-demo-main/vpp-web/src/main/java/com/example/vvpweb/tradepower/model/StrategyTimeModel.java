package com.example.vvpweb.tradepower.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class StrategyTimeModel {
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private Date stime;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "HH:mm")
    private Date etime;
    private Double power;
    private String type;
}
