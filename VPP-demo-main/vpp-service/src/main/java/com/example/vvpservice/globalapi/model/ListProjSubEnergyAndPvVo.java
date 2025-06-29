package com.example.vvpservice.globalapi.model;

import com.example.vvpdomain.entity.StationNode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListProjSubEnergyAndPvVo {

    private String nodeId;

    private String stationName;

    private String stationTypeId;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ListProjSubEnergyAndPvVo> energy;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ListProjSubEnergyAndPvVo> photovoltaic;
    public ListProjSubEnergyAndPvVo() {
        this.energy = new ArrayList<>();
        this.photovoltaic = new ArrayList<>();
    }
    public ListProjSubEnergyAndPvVo(StationNode node) {
        this.nodeId = node.getStationId();
        this.stationName = node.getStationName();
        this.stationTypeId = node.getStationTypeId();
        this.energy = new ArrayList<>();
        this.photovoltaic = new ArrayList<>();
    }
}
