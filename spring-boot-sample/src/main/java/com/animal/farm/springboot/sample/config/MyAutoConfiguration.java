package com.animal.farm.springboot.sample.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animal.farm.springboot.sample.vo.MotorEngineFactoryBean;

/**
 * @author guojun
 * @date 2023/7/28 10:51
 */
@Configuration
public class MyAutoConfiguration {

  @Bean
  public MotorEngineFactoryBean motorEngine() {
    return new MotorEngineFactoryBean();
  }

  @Bean
  public Client client1() {
    return new Client1();
  }

  @Bean
  public Client client2() {
    return new Client2();
  }


  class Client1 implements Client {

  }

  class Client2 implements Client {

  }

  interface Client {

  }
}
