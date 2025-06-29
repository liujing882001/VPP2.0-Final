package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ResourceCurveData implements Serializable {
    private static final long serialVersionUID = 8775655147927291626L;

    @ApiModelProperty("资源ID")
    private String resourceID;

    @ApiModelProperty("数据类型,0日前申报削峰,1日前申报填谷,2备用申报削峰,3备用申报填谷")
    private String dataType;

    @ApiModelProperty("规则曲线")
    private RegularCurve regular;
}
