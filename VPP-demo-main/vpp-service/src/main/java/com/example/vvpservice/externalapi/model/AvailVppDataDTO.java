package com.example.vvpservice.externalapi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AvailVppDataDTO {
    private String nodeId;
    private String nodeName;
    private List<AvailVppDataDTO> children;
    private List<DataType> dataTypes;

    public AvailVppDataDTO(){}
    public AvailVppDataDTO(String nodeId,String nodeName,List<DataType> dataTypes){
        this.nodeId = nodeId;
        this.nodeName = nodeName;
        this.children = new ArrayList<>();
        this.dataTypes = dataTypes;

    }

}
