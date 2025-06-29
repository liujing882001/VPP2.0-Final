package com.example.vvpweb.iotdata.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TsKvModelRequest implements Serializable {

    private String nodeId;
    private String systemId;
    private String deviceId;

    /**
     * 每页大小
     */
    private int pageSize;
    /**
     * 当前页为第几页 默认 1开始
     */
    private int number;
}
