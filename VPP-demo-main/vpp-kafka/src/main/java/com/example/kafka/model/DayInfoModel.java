package com.example.kafka.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DayInfoModel implements Serializable {

    private String meterAccountNumber;
    //可响应负荷
    private String availableValue;
    private String availableDuration;
    //基站当前总负荷
    private String allValue;

    private long time;

}
