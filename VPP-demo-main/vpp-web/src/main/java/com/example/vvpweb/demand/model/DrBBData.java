package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrBBData {
    @JsonProperty("resourceID")
    private String resourceID;
    @JsonProperty("bRData")
    private List<DrBBRData> bRData;
}
