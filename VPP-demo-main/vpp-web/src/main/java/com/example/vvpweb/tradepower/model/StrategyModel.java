package com.example.vvpweb.tradepower.model;

import lombok.Data;

import java.util.List;

@Data
public class StrategyModel {
    private String nodeId;
    private String nodeName;
    private List<StrategyTimeModel> list;

}
