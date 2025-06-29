package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class EndDeviceAsset implements Serializable {
    private static final long serialVersionUID = -6019782522534247162L;

    @ApiModelProperty("终端设备资产的类型")
    private String mrid;
}
