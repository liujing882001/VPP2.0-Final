package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class MeterAsset implements Serializable {
    private static final long serialVersionUID = -4396473756094312305L;

    @ApiModelProperty("表计资产的类型")
    private String mrid;
}
