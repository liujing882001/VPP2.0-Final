package com.example.vvpservice.tunableload.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class RTLoadModel implements Serializable {


    private Double value;
    private String ts;

    public RTLoadModel() {
    }

    public RTLoadModel(Double value, String ts) {
        this.value = value;
        this.ts = ts;
    }
}
