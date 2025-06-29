package com.example.vvpweb.loadmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AiModelResponse implements Serializable {

    @ApiModelProperty("时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date timeStamp;
    @ApiModelProperty("实际")
    private double activePower;
    @ApiModelProperty("预测")
    private double aiTimePrice;
}
