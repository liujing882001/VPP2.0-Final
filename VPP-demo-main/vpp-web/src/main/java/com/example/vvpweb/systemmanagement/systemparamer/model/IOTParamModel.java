package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class IOTParamModel implements Serializable {

    private String id;
    private String paramName;
    private String iotAddress;
    private String iotUserName;
    private String iotUserPwd;
}
