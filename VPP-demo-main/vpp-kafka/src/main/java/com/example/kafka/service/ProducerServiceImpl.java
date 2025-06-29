package com.example.kafka.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Service("producerServiceImpl")
public class ProducerServiceImpl implements ProducerService {

    private static final Logger logger = LoggerFactory.getLogger(ProducerServiceImpl.class);

    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public void sendSyncMessage(String topic, String data) throws ExecutionException, InterruptedException {
        SendResult<String, String> sendResult = kafkaTemplate.send(topic, data).get();
        RecordMetadata recordMetadata = sendResult.getRecordMetadata();
        logger.info("发送同步消息成功!发送的主题为:{}", recordMetadata.topic());
    }

    @Override
    public void sendMessage(String topic, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, data);
        future.addCallback(success -> logger.info("发送消息成功!"), failure -> logger.error("发送消息失败!失败原因是:{}", failure.getMessage()));
    }

    @Override
    public void sendMessage(ProducerRecord<String, String> record) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(record);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    @Override
    public void sendMessage(Message message) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send((ProducerRecord<String, String>) message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    @Override
    public void sendMessage(String topic, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, data);
        future.addCallback(success -> logger.info("发送消息成功!"), failure -> logger.error("发送消息失败!失败原因是:{}", failure.getMessage()));
    }

    @Override
    public void sendMessage(String topic, Integer partition, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }

    @Override
    public void sendMessage(String topic, Integer partition, Long timestamp, String key, String data) {
        ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, partition, timestamp, key, data);
        future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
            @Override
            public void onFailure(Throwable throwable) {
                logger.error("发送消息失败!失败原因是:{}", throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, String> sendResult) {
                RecordMetadata metadata = sendResult.getRecordMetadata();
                logger.info("发送消息成功!消息主题是:{},消息分区是:{}", metadata.topic(), metadata.partition());
            }
        });
    }
}
