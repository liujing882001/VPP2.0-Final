package com.example.vvpweb.systemmanagement.energymodel.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class StorageEnergyAiSchedulingModel implements Serializable {

    String nodeId;
    String systemId;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM")
    Date count_Date;

}
