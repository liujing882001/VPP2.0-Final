package com.example.gateway.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceCommand implements Serializable {

    private String deviceSn;
    private String pointSn;
    private float deviceSetTemperature;
}
