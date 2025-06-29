package com.example.vvpweb.tradepower.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SchedulingStrategyModel {
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;
    private List<StrategyModel> strategy;

}
