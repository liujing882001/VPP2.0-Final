package com.example.vvpweb.systemmanagement.energymodel.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdatePvPowerUserModel implements Serializable {


    private String id;


    /**
     * 电力用户比例
     */
    private Double powerUserProp;
    /**
     * 资产方比例
     */
    private Double loadProp;

    /**
     * 运营方比例
     */
    private Double operatorProp;
}
