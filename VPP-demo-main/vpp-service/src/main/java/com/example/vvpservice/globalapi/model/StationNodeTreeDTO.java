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
}
