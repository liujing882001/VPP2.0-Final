package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.util.List;

@Data
public class EnergyStorageSubView {
    private String date;

    private List<NodeChargeDischargeInfo> nodeChargeDischargeInfos;
}
