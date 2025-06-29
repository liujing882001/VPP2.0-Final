package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.util.List;

@Data
public class EnergyStorageOverviewResp {

    private List<EnergyStorageProperty> energyStoragePropertyList;

    private List<EnergyStorageSubView> energyStorageSubViews;

    private PropertyTotal propertyTotal;
}
