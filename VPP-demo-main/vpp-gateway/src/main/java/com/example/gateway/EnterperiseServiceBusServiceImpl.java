package com.example.gateway;

import com.example.gateway.forest.ApplicationError;
import com.example.gateway.forest.KafkaRequest;
import com.example.gateway.forest.KafkaService;
import com.example.gateway.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("enterperiseServiceBusService")
public class EnterperiseServiceBusServiceImpl implements EnterpriseServiceBusService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnterperiseServiceBusServiceImpl.class);

    @Autowired
    private KafkaService kafkaService;

    @Override
    public boolean eventBusToESB(String key, String topic, String msg) {
        KafkaRequest request = new KafkaRequest();
        request.setKey(key);
        request.setTopic(topic);
        request.setMsg(msg);
        Object retObject = kafkaService.kafkaInfo(request, ApplicationError.create());
        int code = (Integer) JsonUtils.getValueByKey(retObject, "code");
        if (code == 200) {
            LOGGER.info("kafka数据发送成功,请求" + JsonUtils.jsonOutputObj(request));
            return true;
        } else {
            LOGGER.error("kafka数据发送失败,请求" + JsonUtils.jsonOutputObj(request) + "响应" + JsonUtils.jsonOutputObj(retObject));
            return false;
        }
    }
}
