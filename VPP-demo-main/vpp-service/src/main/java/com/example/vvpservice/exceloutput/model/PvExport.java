package com.example.vvpservice.exceloutput.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class PvExport {
    @ExcelProperty
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm")
    private Date timeStamp;
    @ExcelProperty
    private String activePower;
    @ExcelProperty
    private String aiTimePrice;

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getActivePower() {
        return activePower;
    }

    public void setActivePower(String activePower) {
        this.activePower = activePower;
    }

    public String getAiTimePrice() {
        return aiTimePrice;
    }

    public void setAiTimePrice(String aiTimePrice) {
        this.aiTimePrice = aiTimePrice;
    }
}
