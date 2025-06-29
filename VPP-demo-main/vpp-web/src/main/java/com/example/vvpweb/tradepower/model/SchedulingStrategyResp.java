package com.example.vvpweb.tradepower.model;

import com.example.vvpweb.systemmanagement.energymodel.model.EnergyStorageSubView;
import lombok.Data;

import java.util.List;

@Data
public class SchedulingStrategyResp {
    private String nodeId;
    private List<EnergyStorageSubView> strategy;
}
