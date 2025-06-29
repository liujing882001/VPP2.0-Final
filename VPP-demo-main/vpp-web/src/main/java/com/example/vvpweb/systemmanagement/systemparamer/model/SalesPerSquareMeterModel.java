package com.example.vvpweb.systemmanagement.systemparamer.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SalesPerSquareMeterModel implements Serializable {

    private String id;

    private String paramName;

    private double CommercialComplex;

    private double GovernmentOfficeGreaterThan20000;

    private double GovernmentOfficeLessThan20000;
}
