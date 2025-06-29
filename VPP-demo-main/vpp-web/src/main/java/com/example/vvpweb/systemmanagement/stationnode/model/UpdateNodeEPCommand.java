package com.example.vvpweb.systemmanagement.stationnode.model;

import lombok.Data;

import java.util.List;

@Data
public class UpdateNodeEPCommand {
    private String nodeId;
    private String dateType;
    List<UpdateProperty> propertyList;
}
