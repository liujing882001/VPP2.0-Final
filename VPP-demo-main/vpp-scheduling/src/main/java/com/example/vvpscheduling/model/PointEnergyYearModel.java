package com.example.vvpscheduling.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointEnergyYearModel implements Serializable {
    private String nodeId;
    private String systemId;
    private String pointValue;
    private String ts;


    public PointEnergyYearModel() {
    }

    public PointEnergyYearModel(String nodeId, String systemId, String pointValue, String ts) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
