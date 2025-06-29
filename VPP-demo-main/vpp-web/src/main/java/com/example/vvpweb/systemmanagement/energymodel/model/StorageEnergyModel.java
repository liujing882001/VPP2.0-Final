package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergyModel implements Serializable {
    String deviceSn;
    String deviceName;
}
