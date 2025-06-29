package com.example.vvpweb.tradepower.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseModel {
    @ApiModelProperty("名称")
    String name;
    @ApiModelProperty("对应值")
    String value;
    public BaseModel() {
    }
    public BaseModel(String name,String value) {
        this.name = name;
        this.value = value;
    }
}
