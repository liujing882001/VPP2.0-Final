package com.example.vvpservice.globalapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class NodeDevCountTreeDTO {
    private String nodeId;
    private String parentId;
    private String nodeName;
    private BigInteger deviceSize;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<NodeDevCountTreeDTO> children;

    public NodeDevCountTreeDTO(String nodeId, String parentId, String nodeName, BigInteger deviceSize) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
        this.deviceSize = deviceSize;

    }
}
