package com.animal.farm.sample.kafka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author guojun
 * @date 2023/10/1 20:09
 */
@SpringBootApplication
public class SpringbootKafkaSampleApplication {
  public static void main(String[] args) throws Exception {
    SpringApplication springApplication = new SpringApplication(SpringbootKafkaSampleApplication.class);
    springApplication.run(args);
  }
}
