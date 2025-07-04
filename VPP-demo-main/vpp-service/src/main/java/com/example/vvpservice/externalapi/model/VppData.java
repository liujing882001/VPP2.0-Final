package com.example.vvpservice.externalapi.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class VppData {
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date time;
    private Object value;

    public void setTime(Date time) { this.time = time; }
    public void setValue(Object value) { this.value = value; }
}
