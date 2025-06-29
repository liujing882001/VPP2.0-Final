package com.example.vvpweb.Intelligentcabinet.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class PueValueResponse implements Serializable {

    @ApiModelProperty("最新状态，时间格式为yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    @ApiModelProperty("PUE")
    private double value;


}
