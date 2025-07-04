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

    public List<ListProjSubEnergyAndPvVo> getEnergy() { return energy; }
    public void setEnergy(List<ListProjSubEnergyAndPvVo> energy) { this.energy = energy; }
    public List<ListProjSubEnergyAndPvVo> getPhotovoltaic() { return photovoltaic; }
    public void setPhotovoltaic(List<ListProjSubEnergyAndPvVo> photovoltaic) { this.photovoltaic = photovoltaic; }
    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }
    public String getStationTypeId() { return stationTypeId; }
    public void setStationTypeId(String stationTypeId) { this.stationTypeId = stationTypeId; }
    public List<ListProjSubEnergyAndPvVo> getChildren() { return energy; }
    public void setChildren(List<ListProjSubEnergyAndPvVo> children) { this.energy = children; }
}
