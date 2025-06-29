package com.example.vvpweb.flexibleresourcemanagement.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(value = "TodayElectricityModel", description = "今日发电及用能统计")
public class TodayElectricityModel implements Serializable {

    @ApiModelProperty(value = "今日发电及用能统计_发电（kWh）", name = "generateElectricity", required = true)
    private double generateElectricity = 0;

    @ApiModelProperty(value = "今日发电及用能统计_用电（kWh）", name = "energyConsumption", required = true)
    private double energyConsumption = 0;

    @ApiModelProperty(value = "今日发电及用能统计_碳排放（t）", name = "carbonEmission", required = true)
    private double carbonEmission = 0;
}
