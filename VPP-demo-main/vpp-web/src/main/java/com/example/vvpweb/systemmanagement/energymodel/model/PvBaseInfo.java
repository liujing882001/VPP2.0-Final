package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PvBaseInfo implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 光伏装机容量 kwp
     */
    private double photovoltaicInstalledCapacity;

}
