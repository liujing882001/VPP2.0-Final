package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergyResponse implements Serializable {

    private int storageEnergyNum;

    private Double storageEnergyCapacity;

    private Double storageEnergyLoad;

    private Double storageEnergySOH;

    private Double storageEnergySOC;
}
