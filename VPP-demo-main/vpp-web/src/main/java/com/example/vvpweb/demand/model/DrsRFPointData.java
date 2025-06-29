package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrsRFPointData {
    private String resourceID;
    @JsonProperty("rFData")
    private DrsRFData rFData;
}
