package com.example.vvpweb.loadmanagement.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PvModelResponse implements Serializable {

    @ExcelProperty
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date timeStamp;
    @ExcelProperty
    private double activePower;
}
