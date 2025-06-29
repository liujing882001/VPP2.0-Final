package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EnergyDataObject {
    private String nodeId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
//    private Date countDataTime;
    private Double value;
}
