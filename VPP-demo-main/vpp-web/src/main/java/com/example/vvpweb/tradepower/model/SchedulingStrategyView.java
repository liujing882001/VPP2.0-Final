package com.example.vvpweb.tradepower.model;

import com.example.vvpweb.systemmanagement.energymodel.model.NodeChargeDischargeInfo;
import lombok.Data;

import java.util.List;

@Data
public class SchedulingStrategyView {
    private String date;

    private List<NodeChargeDischargeInfo> nodeChargeDischargeInfos;
}
