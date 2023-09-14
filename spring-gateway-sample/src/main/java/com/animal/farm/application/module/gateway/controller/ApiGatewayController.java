package com.animal.farm.application.module.gateway.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.animal.farm.application.module.gateway.dto.ApiRateLimiterDto;
import com.animal.farm.application.module.gateway.service.ApiRateLimiterService;
import com.animal.farm.infrastructure.foundation.web.MessageCode;
import com.animal.farm.infrastructure.foundation.web.Result;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author zhouzhiyuan
 * @date 2022/10/27 12:02
 */
@Api(tags = "限流配置")
@RestController
@RequestMapping(value = "/gateway/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class ApiGatewayController {

  private static final Logger LOGGER = LoggerFactory.getLogger(ArrayIndexOutOfBoundsException.class);

  private ApplicationEventPublisher publisher;

  private ApiRateLimiterService apiRateLimiterService;

  public ApiGatewayController(ApplicationEventPublisher publisher, ApiRateLimiterService apiRateLimiterService) {
    this.publisher = publisher;
    this.apiRateLimiterService = apiRateLimiterService;
  }


  /**
   * 动态刷新路由规则，暂时没有用到
   * @return
   */
  @GetMapping("refresh/route")
  public Result refreshRoute() {
    try {
      publisher.publishEvent(new RefreshRoutesEvent(this));
      return new Result<>();
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Result.error(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
    }
  }

  @ApiOperation("创建api限流")
  @PostMapping("rate/limiter")
  public Result<Boolean> createApiRateLimiter(@Valid @RequestBody ApiRateLimiterDto apiRateLimiterDto) {
    try {
      apiRateLimiterService.saveApiRateLimiter(apiRateLimiterDto.getApiPathId(), apiRateLimiterDto.getReplenishRate(),
          apiRateLimiterDto.getLockTime());
      return new Result<>(true);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Result.error(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
    }
  }

  @ApiOperation("删除api限流")
  @GetMapping("rate/limiter/{id}")
  public Result<Boolean> deleteApiRateLimiter(@ApiParam(name = "id",required = true,example = "id = apiPath") @PathVariable(name = "id" ) String id) {
    try {
      apiRateLimiterService.deleteApiRateLimiter(id);
      return new Result<>(true);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Result.error(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
    }
  }


  @ApiOperation(value = "删除锁定用户", nickname = "unLockedUser", notes = "查询锁定用户")
  @GetMapping(value = "locked/{id}")
  public Result<Boolean> unLockedUser(@ApiParam(name = "id",required = true,example= "id=apiPath+userId") @PathVariable(name = "id") String id) {

    try {
      apiRateLimiterService.unLockUser(id);
      return new Result<>(true);
    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      return Result.error(MessageCode.ERROR_500.getCode(), MessageCode.ERROR_500.getMessage());
    }
  }


}
