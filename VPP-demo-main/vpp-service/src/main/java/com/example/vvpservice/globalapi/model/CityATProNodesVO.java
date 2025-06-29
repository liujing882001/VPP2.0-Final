package com.example.vvpservice.globalapi.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class CityATProNodesVO {
    private Set<String> nodeTypes;
    private List<StructTreeDTO> cityTree;
}
