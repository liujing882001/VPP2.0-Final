package com.example.vvpscheduling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class PointModel implements Serializable {


    private String nodeId;
    private String systemId;
    private Double pointValue;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp ts;


    public PointModel() {
    }

    public PointModel(String nodeId, String systemId, Double pointValue, Timestamp ts) {
        this.nodeId = nodeId;
        this.systemId = systemId;
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
