package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrsDData {
    private String resourceID;
    @JsonProperty("rData")
    private List<DrsRData> rData;
}
