package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.util.List;

@Data
public class StorageEnergyStrategyDistributionModels {

    private List<StrategyDistributionModel> storageEnergyStrategyDistributionModels;

    //是否修改策略再下发
    private boolean modify;
}