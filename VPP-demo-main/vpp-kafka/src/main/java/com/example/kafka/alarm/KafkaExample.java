package com.example.kafka.alarm;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaExample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put("bootstrap.servers", "8.153.13.210:9092");
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        String key = "9875c3ceb98687f62b75b9639a875b27";
        String message = "This is a test message.";

        ProducerRecord<String, String> record = new ProducerRecord<>("virtual_power_performance_alarm_topic", key, message);

        try {
            producer.send(record);
            System.out.println("Message sent successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
