package com.animal.farm.sample.vo;

import org.springframework.beans.factory.FactoryBean;

/**
 * @author guojun
 * @date 2023/7/28 10:55
 */

public class MotorEngineFactoryBean implements FactoryBean<MotorEngine> {

  @Override
  public MotorEngine getObject() throws Exception {
    MotorEngine motorEngine = new MotorEngine();
    motorEngine.setPid("001").setModel("V8");
    return motorEngine;
  }

  @Override
  public Class<?> getObjectType() {
    return MotorEngine.class;
  }
}
