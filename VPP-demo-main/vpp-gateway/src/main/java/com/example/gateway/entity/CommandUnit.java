package com.example.gateway.entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public  class CommandUnit implements Serializable {

    private String deviceSn;

    private String protocol;

    private Map<String, Object> propValues = new ConcurrentHashMap<>();

    public void setDeviceSn(String deviceSn) { this.deviceSn = deviceSn; }
    public void setPropValues(Map<String, Object> propValues) { this.propValues = propValues; }
    public void setProtocol(String protocol) { this.protocol = protocol; }

}
