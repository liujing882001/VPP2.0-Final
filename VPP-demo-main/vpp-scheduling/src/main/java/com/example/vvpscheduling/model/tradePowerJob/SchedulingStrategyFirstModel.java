package com.example.vvpscheduling.model.tradePowerJob;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class SchedulingStrategyFirstModel {
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date date;
    private List<StrategyFirstModel> strategy;

}
