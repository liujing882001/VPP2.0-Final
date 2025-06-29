package com.example.kafka.alarm;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class KafkaProducerExample {
    public static void main(String[] args) {
        System.out.println("服务生产者-开始kafka推送");

        // 创建一个 Properties 对象来存储生产者的配置
        Properties props = new Properties();
        // 设置 Kafka 集群的地址，用于建立连接
        props.put("bootstrap.servers", "8.153.13.210:9092");
        // 设置键的序列化器为字符串序列化器
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        // 设置值的序列化器为字符串序列化器
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        // 创建一个 Kafka 生产者实例，传入配置参数
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);

        // 设置消息的键
        String key = "9875c3ceb98687f62b75b9639a875b27";
        //String key = "00sfasgsdd1";
        // 设置消息的内容
        String message = "001服务生产者--测试的message信息-发送成功-Test message from sender.";

        // 创建一个 ProducerRecord 对象，指定主题、键和消息内容
        ProducerRecord<String, String> record = new ProducerRecord<>("test_topic", key, message);

        try {
            System.out.println("准备发送消息，主题：test_topic，键：" + key + "，消息内容：" + message);
            // 发送消息
            producer.send(record);
            System.out.println("服务生产者消息发送成功-让服务消费者接受-Message sent successfully.");
        } catch (Exception e) {
            System.out.println("发送消息时出现异常：");
            // 如果发送消息出现异常，打印异常堆栈信息
            e.printStackTrace();
        } finally {
            System.out.println("关闭生产者。");
            // 无论是否发生异常，最后都关闭生产者
            producer.close();
        }
    }
    }

