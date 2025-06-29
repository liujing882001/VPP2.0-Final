package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceAdjustmentModel implements Serializable {

    private String deviceKey;
    private String deviceKeyDesc;
    private Double deviceValue;
}
