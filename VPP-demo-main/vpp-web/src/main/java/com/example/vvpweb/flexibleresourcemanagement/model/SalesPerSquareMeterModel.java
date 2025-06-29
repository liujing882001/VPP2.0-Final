package com.example.vvpweb.flexibleresourcemanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@ApiModel(value = "SalesPerSquareMeterModel", description = "逐日坪效")
public class SalesPerSquareMeterModel implements Serializable {
    @ApiModelProperty(value = "年月日", name = "ts", required = true)
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date ts;

    @ApiModelProperty(value = "标准坪效", name = "standardSalesPerSquareMeter", required = true)
    private double standardSalesPerSquareMeter;

    @ApiModelProperty(value = "节点实时坪效", name = "realTimeSalesPerSquareMeter", required = true)
    private double realTimeSalesPerSquareMeter;

    @ApiModelProperty(value = "节点年累计平均坪效", name = "annualAccumulationSalesPerSquareMeter", required = true)
    private double annualAccumulationSalesPerSquareMeter;
}
