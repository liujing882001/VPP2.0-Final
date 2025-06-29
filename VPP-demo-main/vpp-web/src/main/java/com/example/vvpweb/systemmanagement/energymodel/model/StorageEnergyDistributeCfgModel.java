package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageEnergyDistributeCfgModel implements Serializable {

    private String nodeId;
    private String systemId;

    //名称
    private String name;
    //地址
    private String requestUrl;
    //请求方式
    private RequestMethod method;



    public static enum RequestMethod{
        GET,
        POST;
    }
}
