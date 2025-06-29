package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeclareForOperationInterval {
    @ApiModelProperty("最大值")
    private Double maxPower;
    @ApiModelProperty("最小值")
    private Double minPower;
    @ApiModelProperty("数据区间")
    List<BaseModel> interval;
}
