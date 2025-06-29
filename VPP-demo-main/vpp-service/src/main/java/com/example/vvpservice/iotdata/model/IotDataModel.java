package com.example.vvpservice.iotdata.model;

import java.io.Serializable;

/**
 * @author zph
 * @description IOT上传模型
 * @date 2022-07-01
 */
@lombok.Data
public class IotDataModel implements Serializable {

    private Data data;
    private long datatime;
    private String ddccode;
    private String deviceId;
    private String system;
    private long tagcode;
    private String type;
}