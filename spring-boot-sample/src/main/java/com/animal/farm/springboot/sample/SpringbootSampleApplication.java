package com.animal.farm.springboot.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringbootSampleApplication {


    public static void main(String[] args) throws Exception {
//        SpringApplication.run(SpringbootSampleApplication.class, args);

        SpringApplication springApplication = new SpringApplication(SpringbootSampleApplication.class);
        springApplication.run(args);
    }




}