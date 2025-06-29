package com.example.vvpservice.chinasouthernpower.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeviceCommand implements Serializable {

    private String deviceSn;
    private float deviceSetTemperature;
}
