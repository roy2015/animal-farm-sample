package com.animal.farm.sample.origin.service;

import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: BG244210
 * Date: 06/11/2017
 * Time: 17:15
 * Description:
 */

@Component
public class HelloWorldService {
    public HelloWorldService() {
        int k = 0;
    }

    public String sayHello() {
        return "{\"sessionId\":\"123bacd1\", \"hubId\":1, \"userName\":\"abc\"}";
    }
}
