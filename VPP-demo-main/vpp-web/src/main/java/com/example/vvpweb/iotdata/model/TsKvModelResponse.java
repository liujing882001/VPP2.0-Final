package com.example.vvpweb.iotdata.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class TsKvModelResponse implements Serializable {
    String device_sn;
    String point_sn;
    String point_name;
    String point_value;
    String point_desc;

    String ts;
    String point_value_type;
}
