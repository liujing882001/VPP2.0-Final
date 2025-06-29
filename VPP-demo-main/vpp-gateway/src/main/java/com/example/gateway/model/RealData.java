package com.example.gateway.model;

import java.util.List;
import java.util.Map;

public class RealData {
    private List<String> timeSeries;
    private Map<String, List<List<Float>>> values;

    public List<String> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(List<String> timeSeries) {
        this.timeSeries = timeSeries;
    }

    public Map<String, List<List<Float>>> getValues() {
        return values;
    }

    public void setValues(Map<String, List<List<Float>>> values) {
        this.values = values;
    }

}
