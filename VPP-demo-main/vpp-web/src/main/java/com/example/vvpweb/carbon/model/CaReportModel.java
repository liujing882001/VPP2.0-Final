package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author maoyating
 */
@Data
public class CaReportModel implements Serializable {

    @ApiModelProperty("名称")
    private String name;

    @ApiModelProperty("值")
    private String value;

}
