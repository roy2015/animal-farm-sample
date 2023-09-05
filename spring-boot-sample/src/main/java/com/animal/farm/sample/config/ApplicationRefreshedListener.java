package com.animal.farm.sample.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import com.animal.farm.sample.config.MyAutoConfiguration.Client;
import com.animal.farm.sample.vo.MotorEngine;

/**
 * @author guojun
 * @date 2023/7/28 11:01
 */

@Component
public class ApplicationRefreshedListener implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRefreshedListener.class);


  @Autowired
  private Client client;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    ApplicationContext applicationContext = event.getApplicationContext();
    AutowireCapableBeanFactory beanFactory = applicationContext.getAutowireCapableBeanFactory();
    MotorEngine motorEngine = (MotorEngine)beanFactory.getBean("motorEngine");
    LOGGER.warn("{}", motorEngine);

    LOGGER.warn("{}", client);
  }
}
