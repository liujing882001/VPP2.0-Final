package com.example.kafka.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataIotType implements Serializable {
    private Object value;
    private String valuetype;
}