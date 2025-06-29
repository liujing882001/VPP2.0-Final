package com.example.vvpscheduling.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class DistributionModel implements Serializable {

    private String nodeId;
    /**
     * 经度
     */
    private double longitude;

    /**
     * 纬度
     */
    private double latitude;

    private double pointValue;

    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ts;


    public DistributionModel() {
    }

    public DistributionModel(String nodeId, double longitude, double latitude, double pointValue, Date ts) {
        this.nodeId = nodeId;
        this.longitude = longitude;
        this.latitude = latitude;
        this.pointValue = pointValue;
        this.ts = ts;
    }
}
