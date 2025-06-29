package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrDCommand {
    @JsonProperty("root")
    private String root;
    @JsonProperty("version")
    private String version;
    @JsonProperty("respID")
    private String respID;
    @JsonProperty("requestID")
    private String requestID;
    @JsonProperty("createdDateTime")
    private String createdDateTime;
    @JsonProperty("declareData")
    private List<DrDDeclareData> declareData;
}
