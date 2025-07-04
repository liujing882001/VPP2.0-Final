package com.example.vvpservice.externalapi.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VppDataDTO {
    private String dataId;
    private List<VppData> dataList;

    public VppDataDTO(){}
    public VppDataDTO(String dataId){
        this.dataId = dataId;
        this.dataList = new ArrayList<>();
    }

    public void setDataId(String dataId) { this.dataId = dataId; }
    public void setDataList(List<VppData> dataList) { this.dataList = dataList; }
}
