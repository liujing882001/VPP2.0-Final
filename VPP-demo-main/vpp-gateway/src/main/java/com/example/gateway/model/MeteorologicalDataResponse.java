package com.example.gateway.model;

import java.util.ArrayList;
import java.util.List;

public class MeteorologicalDataResponse {

    private List<MeteorologicalData> data = new ArrayList<>();

    public List<MeteorologicalData> getData() {
        return data;
    }

    public void setData(List<MeteorologicalData> data) {
        this.data = data;
    }
}
