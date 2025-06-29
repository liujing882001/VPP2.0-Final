package com.example.vvpweb.demand.model;

import lombok.Data;

import java.util.List;

@Data
public class DrsRFData {
    private String dtstart;
    private String period;
    private List<Double> array;
}
