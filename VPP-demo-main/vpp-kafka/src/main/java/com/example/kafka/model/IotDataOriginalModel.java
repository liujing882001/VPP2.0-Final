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

    public void setSystem(String system) { this.system = system; }
    public void setType(String type) { this.type = type; }
    public void setDatatime(long datatime) { this.datatime = datatime; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public void setDdccode(String ddccode) { this.ddccode = ddccode; }
    public void setData(DataIotType data) { this.data = data; }
    public void setTagcode(long tagcode) { this.tagcode = tagcode; }
    public String getDeviceId() { return deviceId; }
    public String getDdccode() { return ddccode; }
}