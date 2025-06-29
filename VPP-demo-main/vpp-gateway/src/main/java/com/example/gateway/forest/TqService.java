package com.example.gateway.forest;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 天气接口
 */
@Component
public interface TqService {

    @Request(
            url = "${serviceIp}/datalake/meteorological/meteorologicalData",
            type = "POST",
            contentType = "application/json"
    )
    Object  tqInfo(@Body TqRequest request, OnError error);


}