package com.example.gateway.forest;

import com.dtflys.forest.annotation.Body;
import com.dtflys.forest.annotation.Request;
import com.dtflys.forest.callback.OnError;
import org.springframework.stereotype.Component;

/**
 * 天气接口
 */
@Component
public interface KafkaService {

    @Request(
            url = "${serviceIp}/datalake/kafka/kafkaInfo",
            type = "POST",
            contentType = "application/json"
    )
    Object  kafkaInfo(@Body KafkaRequest request, OnError error);


}