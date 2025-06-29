package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrsRPointData {
    private String resourceID;
    private String timestamp;
    @JsonProperty("rData")
    private List<DrsRData> rData;
    @JsonProperty("dData")
    private List<DrsDData> dData;
}
