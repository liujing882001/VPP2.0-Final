package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RegionalTreeQueryModel {
    private String regionId;
    private String regionName;
    private String regionShortName;
    private String regionCode;
    private String regionParentId;
    private String regionLevel;
    private List<RegionalTreeQueryModel> children;
    public RegionalTreeQueryModel(String regionId, String regionName, String regionShortName,String regionParentId, String regionCode, String regionLevel) {
        this.regionId = regionId;
        this.regionName = regionName;
        this.regionShortName = regionShortName;
        this.regionCode = regionCode;
        this.regionParentId = regionParentId;
        this.regionLevel = regionLevel;
        this.children = new ArrayList<>();
    }
    public RegionalTreeQueryModel() {
    }

}
