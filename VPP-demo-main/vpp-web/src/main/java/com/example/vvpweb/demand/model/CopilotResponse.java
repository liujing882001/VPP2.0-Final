package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class CopilotResponse {
    @ApiModelProperty("日期")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date date;

    @ApiModelProperty("对应值")
    private Double value;

    public CopilotResponse() {

    }
    public CopilotResponse(Date date,Double value) {
        this.date = date;
        this.value = value;
    }
    public CopilotResponse(CopilotResponse response) {
        this.date = response.getDate();
        this.value = response.getValue();
    }
}
