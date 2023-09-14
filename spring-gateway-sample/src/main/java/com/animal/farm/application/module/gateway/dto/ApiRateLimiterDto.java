package com.animal.farm.application.module.gateway.dto;


import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author zhouzhiyuan
 * @date 2022/10/28 14:37
 */

@ApiModel("限流配置")
public class ApiRateLimiterDto {

  @ApiModelProperty("请求路径中的uuid值")
  @NotNull(message = "作业名称不能为空")
  private String apiPathId;
  @ApiModelProperty("限流速率，令牌个数")
  @NotNull(message = "限流速率不能为空")
  private Integer replenishRate;
  @ApiModelProperty("锁定时间(单位秒)")
  @NotNull(message = "锁定时间不能为空")
  private Integer lockTime;

  public ApiRateLimiterDto() {
  }

  public ApiRateLimiterDto(String apiPathId, Integer replenishRate, Integer lockTime) {
    this.apiPathId = apiPathId;
    this.replenishRate = replenishRate;
    this.lockTime = lockTime;
  }

  public String getApiPathId() {
    return apiPathId;
  }

  public void setApiPathId(String apiPathId) {
    this.apiPathId = apiPathId;
  }

  public Integer getReplenishRate() {
    return replenishRate;
  }

  public void setReplenishRate(Integer replenishRate) {
    this.replenishRate = replenishRate;
  }

  public Integer getLockTime() {
    return lockTime;
  }

  public void setLockTime(Integer lockTime) {
    this.lockTime = lockTime;
  }
}
