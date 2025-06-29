package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PeakCapacityParamCfgModel implements Serializable {

    private String id;
    private String paramName;

    private double pvRatedPower;
    private double storageEnergyRatedPower;
    private double cdzRatedPower;
    private double ktRatedPower;

}
