package com.example.vvpscheduling.model;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;

/**
 * Auto-generated: 2022-06-07 12:47:57
 *
 * @author www.jsons.cn
 * @website http://www.jsons.cn/json2java/
 */
@Data
public class LoadForecastingBean implements Serializable {

    @JSONField(name = "timestamp")
    private long timestamp;
    @JSONField(name = "load")
    private float load;
}