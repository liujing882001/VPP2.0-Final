package com.example.vvpweb.systemmanagement.energymodel.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class AIStorageEnergystrategyRequest {
    @ApiModelProperty("code")
    private Integer code;

    @ApiModelProperty("msg")
    private String msg;
}
