package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class ControlsMqttModel implements Serializable {

    @ApiModelProperty("控制时间点")
    private String controlTime;

    @ApiModelProperty("时间点下调量 调控量kW")
    private String controlValue;

    @ApiModelProperty("设备调控集合")
    private Object devices;
}
