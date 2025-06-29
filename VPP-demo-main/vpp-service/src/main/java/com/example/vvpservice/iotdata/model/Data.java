package com.example.vvpservice.iotdata.model;

import java.io.Serializable;

public class Data implements Serializable {
    private Object value;
    private String valuetype;

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getValuetype() {
        return valuetype;
    }

    public void setValuetype(String valuetype) {
        this.valuetype = valuetype;
    }
}