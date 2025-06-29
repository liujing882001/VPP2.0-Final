package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class PVResponse implements Serializable {

    private int pvNum;

    private Double pvCapacity;

    private Double pvLoad;
}
