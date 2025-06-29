package com.example.vvpweb.Intelligentcabinet.model;

import com.alibaba.fastjson.JSONObject;
import com.example.vvpcommom.TimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class LatestStatusResponse implements Serializable {

    @ApiModelProperty("最新状态，时间格式为yyyy-MM-dd HH:mm:ss")
    private String timestamp;

    @ApiModelProperty("Ups状态")
    private boolean status;


}
