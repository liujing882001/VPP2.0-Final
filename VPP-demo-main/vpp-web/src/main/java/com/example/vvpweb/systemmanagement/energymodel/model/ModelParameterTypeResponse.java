package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Zhaoph
 */
@Data
public class ModelParameterTypeResponse implements Serializable {

    /**
     * 参数类型 id
     */
    private String config_key;
    /**
     * 参数类型
     */
    private String config_name;
}
