package com.animal.farm.springboot.sample.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.animal.farm.springboot.sample.config.MyAutoConfiguration.Client;

/**
 * @author guojun
 * @date 2023/8/15 20:24
 */
@Component
public class ServiceA {

  @Autowired
  private Client client;



}
