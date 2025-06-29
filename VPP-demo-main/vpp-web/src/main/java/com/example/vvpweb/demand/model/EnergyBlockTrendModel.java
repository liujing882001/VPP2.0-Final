package com.example.vvpweb.demand.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class EnergyBlockTrendModel {
    /**
     * 采集数值=(采集数累计值-基数)
     */
    private Double hTotalUse;

    /**
     * yyy-mm-dd hh:mm:ss
     */
    @JsonFormat(locale = "zh", timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date countDataTime;

    public EnergyBlockTrendModel() {
    }

    public EnergyBlockTrendModel(Double hTotalUse, Date countDataTime) {
        this.hTotalUse = hTotalUse;
        this.countDataTime = countDataTime;
    }
}
