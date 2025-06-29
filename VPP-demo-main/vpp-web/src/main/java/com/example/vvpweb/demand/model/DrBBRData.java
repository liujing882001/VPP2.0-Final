package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DrBBRData {
    @JsonProperty("rSTime")
    private String rSTime;
    @JsonProperty("rETime")
    private String rETime;
    @JsonProperty("rPower")
    private Double rPower;
    @JsonProperty("rPrice")
    private Double rPrice;
}
