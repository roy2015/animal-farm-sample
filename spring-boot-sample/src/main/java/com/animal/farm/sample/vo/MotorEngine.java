package com.animal.farm.sample.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author guojun
 * @date 2023/7/28 10:53
 */
@Data
@Accessors(chain = true)
public class MotorEngine {
  private String pid;
  private String model;

  @Override
  public String toString() {
    return "MotorEngine{" +
        "pid='" + pid + '\'' +
        ", model='" + model + '\'' +
        '}';
  }
}
