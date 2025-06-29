package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SystemParamModel implements Serializable {

    private String id;
    private int key;
    private String paramName;
    private String paramContent;
}
