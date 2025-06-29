package com.example.vvpweb.runschedule.runstrategy.model;

import lombok.Data;

@Data
public class StrategyCal {
    private int nodeNumber;
    private int deviceNumber;
    private double ratedPower;
    private double realPower;
}
