package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class DemandResponseModel implements Serializable {

    private String id;
    private String paramName;
    private String address;
}
