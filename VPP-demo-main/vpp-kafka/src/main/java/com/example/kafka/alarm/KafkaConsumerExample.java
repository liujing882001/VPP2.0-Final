package com.example.kafka.alarm;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerExample {
    public static void main(String[] args) {
        System.out.println("服务消费者开始消费--kafka消费者");
        Properties props = new Properties();
        props.put("bootstrap.servers", "8.153.13.210:9092");
        props.put("group.id", "test_group");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList("test_topic"));

        try {
            System.out.println("消费者已订阅主题 'test_topic'，开始轮询消息。");
            while (true) {
                System.out.println("正在进行轮询...");
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                System.out.println("本次轮询获取到的消息数量：" + records.count());
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("Received message: Key = " + record.key() + ", Value = " + record.value());
                }
            }
        } finally {
            System.out.println("关闭消费者。");
            consumer.close();
        }
    }
}
