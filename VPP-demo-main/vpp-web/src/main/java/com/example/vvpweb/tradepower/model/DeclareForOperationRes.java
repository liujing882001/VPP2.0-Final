package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeclareForOperationRes {
    @ApiModelProperty("参与节点数")
    Integer nodeNum;
    @ApiModelProperty("申报数据")
    List<DeclareForOperationModel> list;
    @ApiModelProperty("数据区间信息")
    DeclareForOperationInterval intervalInfo;
}
