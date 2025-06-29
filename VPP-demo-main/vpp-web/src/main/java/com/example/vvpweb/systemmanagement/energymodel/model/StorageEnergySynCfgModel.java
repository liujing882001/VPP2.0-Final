package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergySynCfgModel implements Serializable {

    private String nodeId;
    private String systemId;

    //名称
    private String name;
    //地址
    private String requestUrl;
    //请求方式
    private RequestMethod method;
    //参数
    private String args;


    public static enum RequestMethod{
        GET,
        POST;
    }
}
