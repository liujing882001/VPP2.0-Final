package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class ModelParameterResponse implements Serializable {

    /**
     * 参数主键
     */
    private String configId;

    /**
     * 参数名称
     */
    private String configName;

    /**
     * 参数键名
     */
    private String configKey;

    /**
     * 参数类型（1 设备 2 点位）
     */
    private String configKeyType;

    /**
     * 系统内置（y是 n否)
     */
    private String configType;

    /**
     * 备注
     */
    private String configMark;

}
