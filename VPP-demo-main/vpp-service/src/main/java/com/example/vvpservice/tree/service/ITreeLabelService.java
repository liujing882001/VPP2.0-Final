package com.example.vvpservice.tree.service;

import com.example.vvpservice.tree.model.StructTreeResponse;

import java.util.List;

public interface ITreeLabelService {

    List<StructTreeResponse> deviceShortView();

    List<StructTreeResponse> loadForestShortView();

    List<StructTreeResponse> runLoadForestShortView();

    List<StructTreeResponse> pvForestShortView();

    List<StructTreeResponse> runPvForestShortView();

    List<StructTreeResponse> loadShortView();

    List<StructTreeResponse> loadNoSystemId_NY_ShortView();

    //************************************
    //  节点添加 类型 或者 省市县
    //*************************************


    List<StructTreeResponse> runLoadNoSystemId_NY_ShortView();

    public List<StructTreeResponse> areaDeviceShortView();

    List<StructTreeResponse> areaLoadForestShortView();

    List<StructTreeResponse> runAreaLoadForestShortView();

    List<StructTreeResponse> areaPvForestShortView();
    List<StructTreeResponse> runAreaPvForestShortView();

    List<StructTreeResponse> areaLoadShortView();

    List<StructTreeResponse> areaLoadNoSystemId_NY_ShortView();
    List<StructTreeResponse> runAreaLoadNoSystemId_NY_ShortView();


    public List<StructTreeResponse> typeDeviceShortView();

    List<StructTreeResponse> typeLoadForestShortView();

    List<StructTreeResponse> typePvForestShortView();

    List<StructTreeResponse> typeLoadShortView();

    List<StructTreeResponse> typeLoadNoSystemId_NY_ShortView();


    public List<StructTreeResponse> areaDeviceView();

    public List<StructTreeResponse> areaDeviceViewMatch(String strategyId);


    List<StructTreeResponse> pvNodeTree();
    List<StructTreeResponse> runPvNodeTree();

    List<StructTreeResponse> loadNodeTree();

    List<StructTreeResponse> storageEnergyNodeTree();
    List<StructTreeResponse> runStorageEnergyNodeTree();


    List<StructTreeResponse> nodeTree();

    List<StructTreeResponse> runNodeTree();

    List<StructTreeResponse> chargingPileNodeTree();
    List<StructTreeResponse> runChargingPileNodeTree();


    List<StructTreeResponse> pvAndStorageEnergyNodeTree();

    List<StructTreeResponse> runPvAndStorageEnergyNodeTree();
}
