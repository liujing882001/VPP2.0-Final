package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrBCommand {
    @JsonProperty("root")
    private String root;
    @JsonProperty("version")
    private String version;
    @JsonProperty("requestID")
    private String requestID;
    @JsonProperty("bidData")
    private List<DrBBidData> bidData;

}
