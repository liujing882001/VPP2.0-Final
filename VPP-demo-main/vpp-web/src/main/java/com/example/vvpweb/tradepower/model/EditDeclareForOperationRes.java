package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class EditDeclareForOperationRes {
    @ApiModelProperty("数据区间信息")
    DeclareForOperationInterval intervalInfo;
    @ApiModelProperty("策略")
    List<SchedulingStrategyModel> list;
}
