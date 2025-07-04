package com.example.vvpcommom.Enum;

public enum NodePostTypeEnum {

    load("load", "负荷"),
    pv("pv", "光伏"),
    storageEnergy("storageEnergy", "储能");

    private String nodePostType;
    private String nodePostName;

    NodePostTypeEnum(String nodePostType, String nodePostName) {
        this.nodePostType = nodePostType;
        this.nodePostName = nodePostName;
    }

    public String getNodePostType() {
        return nodePostType;
    }

    public String getNodePostName() {
        return nodePostName;
    }
}
