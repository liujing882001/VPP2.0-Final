package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DrDDData {
    @JsonProperty("timestamp")
    private String timestamp;
    @JsonProperty("declarePower")
    private Double declarePower;
}
