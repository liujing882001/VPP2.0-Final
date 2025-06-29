package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrDDeclareData {
    @JsonProperty("resourceID")
    private String resourceID;
    @JsonProperty("dData")
    private List<DrDDData> dData;
}
