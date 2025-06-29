package com.example.vvpweb.sensetime.load;

import java.io.Serializable;

@lombok.Data
public class Load implements Serializable {
    private String time;
    private double value;
}