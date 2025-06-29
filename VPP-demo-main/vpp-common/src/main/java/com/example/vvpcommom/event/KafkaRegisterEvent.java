package com.example.vvpcommom.event;

import org.springframework.context.ApplicationEvent;

/**
 * 注册事件
 */

public class KafkaRegisterEvent extends ApplicationEvent {


    private String topic;
    private String message;

    public KafkaRegisterEvent(Object source) {
        super(source);
    }

    public KafkaRegisterEvent(Object source, String topic, String message) {
        super(source);
        this.topic = topic;
        this.message = message;
    }

    public String getTopic() {
        return topic;
    }

    public String getMessage() {
        return message;
    }
}
