package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author maoyating
 */
@Data
public class CaFootmarkReq {

    @ApiModelProperty("节点id")
    private String nodeId;

    @ApiModelProperty("范围(1-范围一 2-范围二 3-范围三)")
    private Integer scopeType;

    @ApiModelProperty("年份 yyyy")
    private Integer year;

    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

}
