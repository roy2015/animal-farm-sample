package com.animal.farm.sample.controller;


import com.animal.farm.sample.service.ServiceA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * helloController
 */
@RestController
@RequestMapping("/api")
public class HelloController {
    @Autowired
    private ServiceA serviceA;


    @RequestMapping(value = "sayHello")
    public String helloWorld() {
        return serviceA.sayHello();
    }

}
