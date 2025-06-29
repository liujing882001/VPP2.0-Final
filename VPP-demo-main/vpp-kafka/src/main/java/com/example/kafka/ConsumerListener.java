package com.example.kafka;

import com.example.vvpcommom.HttpUtil;
import com.example.vvpcommom.StringUtils;
import com.example.vvpcommom.event.KafkaRegisterEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 国网数科联调协议
 */
@Component
public class ConsumerListener {


    @Resource
    private ApplicationEventPublisher applicationEventPublisher;
    @Value("${server.port}")
    private int port;

    /**
     * 交互序号-2 申报响应负荷（Topic）名称
     * demand_response_invitation_response
     * <p>
     * 1、能源平台收到虚拟电厂平台的日前邀约信息后，根据自身平台计算出响应，推送至约定kafka对应topic，
     * 虚拟电厂平台消费该topic
     * 2、每条信息为数组形式，最多2000
     */
    @KafkaListener(topics = "demand_response_invitation_response", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${spring.kafka.auto-startup}")
    public void demandResponseInvitationResponse(@Payload String data,
                                                 @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                                 @Headers MessageHeaders messageHeaders,
                                                 @Header(KafkaHeaders.OFFSET) String offset) {
//        LOGGER.info("[接收信息] topic:{},  offset:{},  data:{}", topic, offset, data);

        String url = "http://localhost:" + port + "/demandKafka/demandResponseInvitationResponse";

        if (!StringUtils.isEmpty(data)) {
            //发送数据到API接口
            HttpUtil.okHttpPost(url, data);
        }
    }


    /**
     * 交互序号-6、基站负荷上报接口（Topic）名称
     * demand_response_day_info
     * <p>
     * 1、能源平台每15分钟上报能参与响应的基站负荷信息，，将信息推送至约定kafka对应topic，
     * 虚拟电厂平台消费该topic
     * 2、每次上报的都是能参与响应的，不能参与的不上报
     */
    @KafkaListener(topics = "demand_response_day_info", groupId = "${spring.kafka.consumer.group-id}", autoStartup = "${spring.kafka.auto-startup}")
    public void demandResponseDayInfo(@Payload String data,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Headers MessageHeaders messageHeaders,
                                      @Header(KafkaHeaders.OFFSET) String offset) {
//        LOGGER.info("[接收信息] topic:{},  offset:{},  data:{}", topic, offset, data);

        //采集节点下实时可调负荷
        applicationEventPublisher.publishEvent(new KafkaRegisterEvent(this, topic, data));
    }

}