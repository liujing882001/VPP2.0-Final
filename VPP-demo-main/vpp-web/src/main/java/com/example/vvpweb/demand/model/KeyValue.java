package com.example.vvpweb.demand.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class KeyValue implements Serializable {
    private static final long serialVersionUID = 4037544934486365256L;

    @ApiModelProperty("键")
    private String key;

    @ApiModelProperty("值")
    private String value;
}
