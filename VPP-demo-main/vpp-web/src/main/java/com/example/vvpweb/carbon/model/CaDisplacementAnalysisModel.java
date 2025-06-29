package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class CaDisplacementAnalysisModel implements Serializable {


    @ApiModelProperty("日期")
    private String dateTime;

    @ApiModelProperty("交易类型")
    private String scopetType;

    @ApiModelProperty("值")
    private String dischargeValue;

}
