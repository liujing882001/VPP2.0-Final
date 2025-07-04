package com.example.vvpservice.globalapi.model;

import com.example.vvpdomain.entity.StationNode;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ListProjSubCategoryVo {
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
    private List<ListProjSubCategoryVo> energy;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ListProjSubCategoryVo> photovoltaic;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<ListProjSubCategoryVo> load;
    public ListProjSubCategoryVo() {

    }
    public ListProjSubCategoryVo(StationNode node) {
        this.id = node.getId();
        this.stationId = node.getStationId();
        this.stationName = node.getStationName();
        this.stationCategory = node.getStationCategory();
        this.stationType = node.getStationType();
        this.systemIds = node.getSystemIds();
        this.stationTypeId = node.getStationTypeId();
        this.systemNames = node.getSystemNames();
        this.stationState = node.getStationState();
        this.energy = new ArrayList<>();
        this.photovoltaic = new ArrayList<>();
        this.load = new ArrayList<>();
    }

    public List<ListProjSubCategoryVo> getChildren() { return load; }
    public void setChildren(List<ListProjSubCategoryVo> children) { this.load = children; }

    // 手动添加缺失的getter方法以确保编译通过
    public List<ListProjSubCategoryVo> getEnergy() {
        return energy;
    }

    public List<ListProjSubCategoryVo> getPhotovoltaic() {
        return photovoltaic;
    }

    public List<ListProjSubCategoryVo> getLoad() {
        return load;
    }
}
