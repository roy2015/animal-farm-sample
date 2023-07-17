package com.animal.farm.nacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;

/**
 * @author guojun
 * @date 2023/7/17 16:21
 */
@SpringBootApplication
@NacosPropertySource(dataId = "example")
public class SpringbootSampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(SpringbootSampleApplication.class, args);
  }
}
