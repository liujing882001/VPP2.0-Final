package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DrBBidData {
    @JsonProperty("respID")
    private String respID;
    @JsonProperty("respName")
    private String respName;
    @JsonProperty("respBid")
    private String respBid;
    @JsonProperty("respType")
    private String respType;
    @JsonProperty("bData")
    private List<DrBBData> bData;
}
