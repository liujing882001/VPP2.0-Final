package com.example.vvpweb.demand.model;

import lombok.Data;

import java.util.List;

@Data
public class DrIInviteData {
    private String marketID;
    private String respID;
    private String respName;
    private Integer respType;
    private List<String> resourceIds;
    private String mktDeclStart;
    private String mktDeclEnd;
    private String pwrModStart;
    private String pwrModEnd;
    private Double maxPrice;
}
