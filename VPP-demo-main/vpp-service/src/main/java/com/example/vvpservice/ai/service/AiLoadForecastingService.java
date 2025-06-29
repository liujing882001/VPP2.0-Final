package com.example.vvpservice.ai.service;


import com.example.vvpdomain.entity.AiLoadForecasting;

import java.util.List;

public interface AiLoadForecastingService {

    void batchInsertOrUpdate(List<AiLoadForecasting> list);

}
