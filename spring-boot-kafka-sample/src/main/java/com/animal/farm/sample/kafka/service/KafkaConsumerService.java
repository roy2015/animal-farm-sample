package com.animal.farm.sample.kafka.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.stereotype.Service;

/**
 *
 * 定义三个consume，刚好每个对应一个partition
 * 可以用KafkaListeners，里面套KafkaListener，就可以只用一个而不用重复定义三个方法
 *
 */
@Service
public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @KafkaListeners({
        @KafkaListener(id= "1", clientIdPrefix = "c1",topics = "topic2023", groupId="my-consumer-group"),
        @KafkaListener(id= "2", clientIdPrefix = "c2", topics = "topic2023", groupId="my-consumer-group"),
        @KafkaListener(id= "3", clientIdPrefix = "c3", topics = "topic2023", groupId="my-consumer-group")
    })
    public void consumer(String message) {
        // 处理接收到的消息
        logger.info("consumer Received message: " + message);
    }

//    @KafkaListener(id= "1", topics = "topic2023", groupId="my-consumer-group")
//    public void consumer1(String message) {
//        // 处理接收到的消息
//        logger.info("consumer1 Received message: " + message);
//    }
//
//    @KafkaListener(id= "2", topics = "topic2023", groupId="my-consumer-group")
//    public void consumer2(String message) {
//        // 处理接收到的消息
//        logger.info("consumer2 Received message: " + message);
//    }
//
//    @KafkaListener(id= "3", topics = "topic2023", groupId="my-consumer-group")
//    public void consumer3(String message) {
//        // 处理接收到的消息
//        logger.info("consumer2 Received message: " + message);
//    }
}
