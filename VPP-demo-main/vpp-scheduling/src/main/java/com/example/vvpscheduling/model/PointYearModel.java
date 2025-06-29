package com.example.vvpscheduling.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PointYearModel implements Serializable {
    private String nodeId;
    private String systemId;
    private Double pointValue;
    private String ts;


    public PointYearModel() {
    }

    public PointYearModel(String nodeId, String systemId, Double pointValue, String ts) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
