package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

@Data
public class NodeEPVO {
    private String property;
    private String price;
    private String dateType;
    private String priceUse;

    public NodeEPVO(String priceUse,String dateType,String property,String price) {
        this.priceUse= priceUse;
        this.dateType = dateType;
        this.property = property;
        this.price = price;
    }
}
