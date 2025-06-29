package com.example.vvpservice.tunableload.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RTLoadMonthModel implements Serializable {

    private Double value;
    private String ts;


    public RTLoadMonthModel() {
    }

    public RTLoadMonthModel(Double value, String ts) {
        this.value = value;
        this.ts = ts;
    }
}
