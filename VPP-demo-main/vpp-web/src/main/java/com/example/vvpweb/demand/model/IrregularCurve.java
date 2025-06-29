package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IrregularCurve implements Serializable {
    private static final long serialVersionUID = -6101076193503268348L;

    @ApiModelProperty("存储值的数组")
    private List<com.example.vvpweb.demand.model.Data> values;
}
