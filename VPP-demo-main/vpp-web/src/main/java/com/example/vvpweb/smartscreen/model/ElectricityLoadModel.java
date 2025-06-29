package com.example.vvpweb.smartscreen.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用电负荷曲线
 */
@Data
public class ElectricityLoadModel implements Serializable {
    private BigDecimal predictedValue;
    private BigDecimal actualValue;
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date countDataTime;

    public ElectricityLoadModel() {
    }

    public ElectricityLoadModel(BigDecimal predictedValue, BigDecimal actualValue, Date countDataTime) {
        this.predictedValue = predictedValue;
        this.actualValue = actualValue;
        this.countDataTime = countDataTime;
    }
}
