package com.example.gateway;

import com.example.gateway.model.MeteorologicalDataResponse;

import java.util.Date;

public interface MeteorologicalDataService {

    MeteorologicalDataResponse getMeteorologicalData(double longitude, double latitude
            , Date startDate
            , Date endDate);
}
