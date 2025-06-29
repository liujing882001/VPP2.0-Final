package com.example.vvpweb.smartscreen.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 储能电站信息
 */
@Data
public class EnergyStorageStationModel implements Serializable {

    private double outCapacity;
    private String soc;
    private String soh;
}
