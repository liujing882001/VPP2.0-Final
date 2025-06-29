package com.example.vvpweb.flexibleresourcemanagement.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoadNatureResponse implements Serializable {
    String name;
    double value;
}
