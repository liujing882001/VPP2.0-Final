package com.example.vvpweb.carbon.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CaSubFootmarkModel {

    @ApiModelProperty("燃烧排放类型（1-固定燃烧源的燃烧排放 2-移动燃烧源的燃烧排放 3-逸散型排放源的排放）")
    private Integer dischargeType;

    @ApiModelProperty("燃烧排放实体（11-天然气 12-煤气 13-柴油 21-公务车 31-冷机 32-分体空调 33-灭火器）")
    private Integer dischargeEntity;

    @ApiModelProperty("冷机参数（1-冷机制冷剂类型 2-每台冷机制冷剂数量 3-冷机数量）")
    private Integer refrigerator;

    @ApiModelProperty("范围二购买内容（1-外购电力（kWh）2-外购热力（KJ））")
    private Integer scopeTwo;

    @ApiModelProperty("范围三类型（1-差旅-飞机(km) 2-差旅-火车（km) 3-差旅-私家车(辆) 4-自来水(t) 5-纸张消耗(张)）")
    private Integer scopeThree;

    @ApiModelProperty("值")
    private Double dischargeValue;
}
