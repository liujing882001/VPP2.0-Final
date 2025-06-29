package com.example.vvpweb.chinasouthernpower.model;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AgentMerchantsResponse  implements Serializable {

    @ExcelProperty(value = "负荷聚合商和其代理用户唯一标识")
    @ApiModelProperty(value = "负荷聚合商和其代理用户 唯一标识", required = true)
    private String creditCode;

    @ExcelProperty(value = "负荷聚合商和其代理用户中文名称")
    @ApiModelProperty(value = "负荷聚合商和用户中文名称", required = true)
    private String userName;

    @ExcelProperty(value = "负荷聚合商和用户联系人姓名")
    @ApiModelProperty(value = "负荷聚合商和用户联系人姓名", required = true)
    private String operator;

    @ExcelProperty(value = "负荷聚合商和用户联系电话1")
    @ApiModelProperty(value = "负荷聚合商和用户联系电话 1", required = true)
    private String operatorTel1;

    @ExcelProperty(value = "负荷聚合商和用户联系电话2")
    @ApiModelProperty(value = "负荷聚合商和用户联系电话 2", required = false)
    private String operatorTel2;

    @ExcelProperty(value = "负荷聚合商和用户注册地址")
    @ApiModelProperty(value = "负荷聚合商和用户注册地址", required = true)
    private String operatorAddress;
}
