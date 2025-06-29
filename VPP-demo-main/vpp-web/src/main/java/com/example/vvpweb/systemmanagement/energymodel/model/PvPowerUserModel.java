package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PvPowerUserModel implements Serializable {

    private String nodeId;
    /**
     * 系统id
     */
    private String systemId;

    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    private int number;
}
