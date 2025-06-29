package com.example.vvpweb.presalesmodule.model;

import lombok.Data;

import java.util.List;

@Data
public class PredictEnergyCommand {
    private String projectId;
    private List<PredictEnergyModel> capacityList;
}
