package com.example.vvpweb.flexibleresourcemanagement.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class LoadMonthModelResponse implements Serializable {
    @ExcelProperty
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd")
    private Date ts;
    @ExcelProperty
    private Double value;
}
