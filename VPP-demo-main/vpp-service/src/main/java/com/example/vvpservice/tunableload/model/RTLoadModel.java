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

    // 手动添加getter和setter方法以确保编译通过
    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
