package com.example.vvpservice.exceloutput.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class LoadExport {
    @ExcelProperty
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date timeStamp;
    @ExcelProperty
    private String load;
    @ExcelProperty
    private String aiTimePrice;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getLoad() {
        return load;
    }

    public void setLoad(String load) {
        this.load = load;
    }

    public String getAiTimePrice() {
        return aiTimePrice;
    }

    public void setAiTimePrice(String aiTimePrice) {
        this.aiTimePrice = aiTimePrice;
    }
}
