package com.example.vvpweb.loadmanagement.model;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class AiStatisticalPrecisionModelResponse implements Serializable {

    @ExcelProperty
    @ApiModelProperty("时间")
    private String timeStamp;

    @ExcelProperty
    @ApiModelProperty("超短期预测准确率")
    private String ultraShortTermForecast;

    @ExcelProperty
    @ApiModelProperty("目前预测准确率")
    private String currentForecast;

    @ExcelProperty
    @ApiModelProperty("中期预测准确率")
    private String mediumTermForecast;
}
