package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SystemThirdPartyEnergyPlat implements Serializable {
    private String id;
    private List<ThirdPartyEnergyPlatParam> param;
    private String status;
}
