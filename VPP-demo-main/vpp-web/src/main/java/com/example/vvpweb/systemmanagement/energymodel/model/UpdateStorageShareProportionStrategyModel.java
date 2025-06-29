package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateStorageShareProportionStrategyModel implements Serializable {

    private String id;

    /**
     * 负荷集成商比例
     */
    private Double loadProp;

    /**
     * 电力用户比例
     */
    private Double powerUserProp;
}
