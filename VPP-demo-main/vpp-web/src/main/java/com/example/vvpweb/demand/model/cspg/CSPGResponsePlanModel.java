package com.example.vvpweb.demand.model.cspg;

import lombok.Data;

import java.util.Map;
@Data
public class CSPGResponsePlanModel {

    private String resourceId;
    private Map<String, Double> declare;
}
