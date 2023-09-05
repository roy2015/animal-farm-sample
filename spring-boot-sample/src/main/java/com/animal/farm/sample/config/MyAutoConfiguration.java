package com.animal.farm.sample.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.animal.farm.sample.vo.MotorEngineFactoryBean;

/**
 * @author guojun
 * @date 2023/7/28 10:51
 *
 *
 * 测试ConditionalOnClass 用hutool的CronPatternUtil测试，引入了hutool包就不报错
 */
@Configuration
@ConditionalOnClass(name= "cn.hutool.cron.pattern.CronPatternUtil")
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

  public interface Client {

  }
}
