package com.animal.farm.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class SpringbootSampleApplication {


    public static void main(String[] args)  {
        SpringApplication springApplication = new SpringApplication(SpringbootSampleApplication.class);
        springApplication.run(args);
    }

}