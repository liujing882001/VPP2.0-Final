package com.example.gateway.model;

import lombok.Data;

@Data
public class MeteorologicalDataVo {
    //实时-2m 气温
    private Double rtTt2;
    //实时-小时累计降水
    private Double rtRain;
    //"实时采集时间"
    private String ts;
    //预测-2m 气温
    private Double predTt2;
    //预测-小时累计降水
    private Double predRain;

    private Double rtSh;


    private Double rtSsr;

    private Double rtWs10;

    private Double rtUu;


    private Double rtVv;

    private Double rtPs;

    private Double rtDt;

    private Double predSsrd;

    private Double predTsdsr;

    private Double predSTsr;

    private Double predWs10;

    private Double predWd10;

    private Double predWs100;

    private Double predWd100;

    private Double predRh;

    private Double predPs;

    private Double predTcc;
}
