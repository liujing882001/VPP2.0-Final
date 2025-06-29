package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import java.util.List;

@Data
public class AllNodeEPVO {
    private String nodeId;
    private String nodeName;
    private List<NodeEPVO> epVoList;
    private List<NodeEPVO> sepVoList;

    public AllNodeEPVO(String nodeId,String nodeName,List<NodeEPVO> epVoList,List<NodeEPVO> eppVoList) {
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.epVoList = epVoList;
        this.sepVoList = eppVoList;
    }
}
