package com.example.vvpweb.globalapi.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ListProjSubCommand {

    private String query;


    private Map<String,Object> keyword;
    public List<String> getStationTypeIds() {
        Object stationTypeIdObj = keyword.get("stationTypeId");
        if (stationTypeIdObj instanceof List) {
            return (List<String>) stationTypeIdObj;
        }
        return new ArrayList<>();
    }

    public List<String> getStationState() {
        Object stationStateObj = keyword.get("stationState");
        if (stationStateObj instanceof List) {
            return (List<String>) stationStateObj;
        }
        return new ArrayList<>();
    }

}
