package com.example.vvpcommom.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodePostTypeEnum {

    load("load", "负荷"),
    pv("pv", "光伏"),
    storageEnergy("storageEnergy", "储能");

    private String nodePostType;
    private String nodePostName;
}
