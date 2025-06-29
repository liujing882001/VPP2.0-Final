package com.example.vvpscheduling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class PointEnergyModel implements Serializable {


    private String nodeId;
    private String systemId;
    private String pointValue;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp ts;


    public PointEnergyModel() {
    }

    public PointEnergyModel(String nodeId, String systemId, String pointValue, Timestamp ts) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
