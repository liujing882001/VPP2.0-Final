package com.example.vvpweb.tradepower.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class StrategyFirstModel {
    private String nodeId;
    private List<Double> list;

}
