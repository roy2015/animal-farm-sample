package com.animal.farm.springboot.sample.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

import com.animal.farm.springboot.sample.config.MyAutoConfiguration.Client;

import lombok.extern.slf4j.Slf4j;

/**
 * @author guojun
 * @date 2023/8/15 20:13
 */
@Slf4j
@Component
public class CustomerBFBB implements BeanFactoryPostProcessor {
  @Override
  public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    BeanDefinition beanDefinition = beanFactory.getBeanDefinition("client1");
    beanDefinition.setPrimary(true);
    log.info("123");
  }
}
