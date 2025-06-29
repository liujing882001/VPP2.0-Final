package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class ResourceOverviewNodeTypeOrderModel implements Serializable {

    private String id;
    private String paramName;
    private List<String> nodeTypeIds = new ArrayList<>();
}
