package com.example.vvpservice.globalapi.model;

import com.example.vvpdomain.entity.StationNode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListProjSubVo {
    private String id;

    private String stationId;

    private String stationName;

    private String stationCategory;

    private String stationType;

    private String systemIds;

    private String stationTypeId;

    private String systemNames;

    private String stationState;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ListProjSubVo> children;

    public ListProjSubVo() {

    }
    public ListProjSubVo(StationNode node) {
        this.id = node.getId();
        this.stationId = node.getStationId();
        this.stationName = node.getStationName();
        this.stationCategory = node.getStationCategory();
        this.stationType = node.getStationType();
        this.systemIds = node.getSystemIds();
        this.stationTypeId = node.getStationTypeId();
        this.systemNames = node.getSystemNames();
        this.stationState = node.getStationState();
        this.children = new ArrayList<>();
    }
}
