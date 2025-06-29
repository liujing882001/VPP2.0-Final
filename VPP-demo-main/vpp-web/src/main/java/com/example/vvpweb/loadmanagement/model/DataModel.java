package com.example.vvpweb.loadmanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DataModel implements Serializable {
    int stat_type;
    String value;
}
