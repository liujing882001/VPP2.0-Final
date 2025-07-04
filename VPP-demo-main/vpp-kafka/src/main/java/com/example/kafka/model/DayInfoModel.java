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

    public String getMeterAccountNumber() { return meterAccountNumber; }
    public void setMeterAccountNumber(String meterAccountNumber) { this.meterAccountNumber = meterAccountNumber; }
    public String getAvailableValue() { return availableValue; }
    public void setAvailableValue(String availableValue) { this.availableValue = availableValue; }
    public String getAllValue() { return allValue; }
    public void setAllValue(String allValue) { this.allValue = allValue; }
    public long getTime() { return time; }
    public void setTime(long time) { this.time = time; }

}
