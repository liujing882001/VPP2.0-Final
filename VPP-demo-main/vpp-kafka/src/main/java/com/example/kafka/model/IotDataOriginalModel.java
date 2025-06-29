package com.example.kafka.model;


import lombok.Data;

import java.io.Serializable;

@Data
public class IotDataOriginalModel implements Serializable {

    private long tagcode;

    private String system;

    private String ddccode;

    private long datatime;

    private DataIotType data;

    private String deviceId;

    private String type;
}