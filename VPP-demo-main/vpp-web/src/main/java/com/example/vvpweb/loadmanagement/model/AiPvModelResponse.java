package com.example.vvpweb.loadmanagement.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AiPvModelResponse implements Serializable {

    @ExcelProperty
    @ApiModelProperty("时间")
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date timeStamp;

    @ExcelProperty
    @ApiModelProperty("实际值")
    private String realValue;
//
//    @ExcelProperty
//    @ApiModelProperty("超短期预测值")
//    private String ultraShortTermForecastValue;

    @ExcelProperty
    @ApiModelProperty("目前预测准确率值")
    private String currentForecastValue;
//
//    @ExcelProperty()
//    @ApiModelProperty("中期预测准确率值")
//    private String mediumTermForecastValue;
}
