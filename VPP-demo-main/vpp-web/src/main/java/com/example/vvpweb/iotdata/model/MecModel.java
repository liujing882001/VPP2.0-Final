package com.example.vvpweb.iotdata.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class MecModel implements Serializable {

    private boolean active;
    private long lastConnectTime;
    private long lastActivityTime;
    private long lastDisconnectTime;
    private long lastInactivityAlarmTime;
    private long inactivityTimeout;
    private String deviceType;
    private String scope;
    private String ss_mecId;
    private String deviceName;
}
