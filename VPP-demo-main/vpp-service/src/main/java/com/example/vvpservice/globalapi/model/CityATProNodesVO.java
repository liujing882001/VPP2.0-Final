package com.example.vvpservice.globalapi.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CityATProNodesVO {
    private Set<String> nodeTypes;
    private List<StructTreeDTO> cityTree;

    public List<StructTreeDTO> getCityTree() { return cityTree; }
    public void setCityTree(List<StructTreeDTO> cityTree) { this.cityTree = cityTree; }
    public Set<String> getNodeTypes() { return nodeTypes; }
    public void setNodeTypes(Set<String> nodeTypes) { this.nodeTypes = nodeTypes; }
}
