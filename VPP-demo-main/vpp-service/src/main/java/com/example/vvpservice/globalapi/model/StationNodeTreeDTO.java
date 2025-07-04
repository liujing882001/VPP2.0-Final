package com.example.vvpservice.globalapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.util.List;

@Data
public class StationNodeTreeDTO {
    private String nodeId;
    private String parentId;
    private String nodeName;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<StationNodeTreeDTO> children;

    public StationNodeTreeDTO(String nodeId, String parentId, String nodeName) {
        this.nodeId = nodeId;
        this.parentId = parentId;
        this.nodeName = nodeName;
    }

    public String getNodeId() { return nodeId; }
    public void setNodeId(String nodeId) { this.nodeId = nodeId; }
    public String getParentId() { return parentId; }
    public void setParentId(String parentId) { this.parentId = parentId; }
    public List<StationNodeTreeDTO> getChildren() { return children; }
    public void setChildren(List<StationNodeTreeDTO> children) { this.children = children; }
}
