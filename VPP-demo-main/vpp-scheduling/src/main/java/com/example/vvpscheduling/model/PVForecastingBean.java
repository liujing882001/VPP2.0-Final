package com.example.vvpscheduling.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

@Data
public class PVForecastingBean implements Serializable {

    @JSONField(name = "timestamp")
    private long timestamp;
    @JSONField(name = "active_power")
    private float active_power;

}
