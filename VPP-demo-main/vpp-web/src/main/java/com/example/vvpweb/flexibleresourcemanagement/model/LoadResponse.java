package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadResponse implements Serializable {

    private Double totalLoad;

    private Double load;

    private Double jieRuLoad;

    private Double loadNumber;
}
