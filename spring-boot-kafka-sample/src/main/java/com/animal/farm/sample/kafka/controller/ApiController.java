package com.animal.farm.sample.kafka.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.animal.farm.sample.kafka.service.KafkaConsumerService;
import com.animal.farm.sample.kafka.service.KafkaProducerService;

//import com.animal.farm.sample.service.ServiceA;

/**
 * ApiController
 */
@RestController
@RequestMapping("/api")
public class ApiController {
    @Autowired
    private KafkaProducerService producerService;

    @Autowired
    private KafkaConsumerService consumerService;

    @RequestMapping(value = "sayHello")
    public String helloWorld() {
        return "123";
    }

//    @PostMapping(value = "sendMsg")
    @GetMapping(value = "sendMsg")
    public String sendMsg(@RequestParam(name = "topic") String topic,
        @RequestParam(name = "partition") Integer partition,
        @RequestParam(name = "key") String key,
        @RequestParam(name = "message") String message ) {
        producerService.sendMessage(topic, partition, key, message);
        return "success send";
    }

}
