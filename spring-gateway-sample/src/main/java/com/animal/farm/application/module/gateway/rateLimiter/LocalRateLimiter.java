package com.animal.farm.application.module.gateway.rateLimiter;/*
 * Copyright 2017-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import static com.animal.farm.application.module.gateway.constant.GatewayConstant.SEPARATOR;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.validation.constraints.Min;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.gateway.filter.ratelimit.AbstractRateLimiter;
import org.springframework.cloud.gateway.support.ConfigurationService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Primary;
import org.springframework.core.style.ToStringCreator;
import org.springframework.validation.annotation.Validated;

import com.animal.farm.application.module.gateway.dto.LockedUserInformationDto;
import com.animal.farm.application.module.gateway.rateLimiter.LocalRateLimiter.Config;
import com.animal.farm.application.module.gateway.service.ApiRateLimiterServiceImpl;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterConfig.Builder;
import io.github.resilience4j.ratelimiter.internal.InMemoryRateLimiterRegistry;
import reactor.core.publisher.Mono;

/**
 * @author zhouzhiyuan 当前api限速类 为定制类，不具备通用性。
 */
@Primary
public class LocalRateLimiter extends AbstractRateLimiter<Config>
    implements ApplicationContextAware {

  private static final Logger LOG = LoggerFactory.getLogger(LocalRateLimiter.class);

  /**
   * Local Rate Limiter property name.
   */
  public static final String CONFIGURATION_PROPERTY_NAME = "local-rate-limiter";

  /**
   * Remaining Rate Limit header name.
   */
  public static final String REMAINING_HEADER = "X-RateLimit-Remaining";

  /**
   * Replenish Rate Limit header name.
   */
  public static final String REPLENISH_RATE_HEADER = "X-RateLimit-Replenish-Rate";

  /**
   * Refresh period header name.
   */
  public static final String REFRESH_PERIOD_HEADER = "X-RateLimit-Refresh-Period";

  /**
   * Requested Tokens header name.
   */
  public static final String REQUESTED_TOKENS_HEADER = "X-RateLimit-Requested-Tokens";

  private final AtomicBoolean initialized = new AtomicBoolean(false);

  /**
   * Whether or not to include headers containing rate limiter information, defaults to false.
   */
  private boolean includeHeaders = false;

  private Config defaultConfig;

  /**
   * 必须持有InMemoryRateLimiterRegistry ，限流才能生效
   */
  private Map<String, InMemoryRateLimiterRegistry> cacheRateLimiter = new ConcurrentHashMap<>();


  private final Map<String, ApiRateLimiterServiceImpl.Config> apiAccessRate;

  private final Map<String, Long> lockedUser;

  private final BlockingQueue<LockedUserInformationDto> rateLimiterBlockingQueue;

  /**
   * This creates an instance with default static configuration, useful in Java DSL.
   *
   * @param defaultReplenishRate how many tokens per second in token-bucket algorithm. algorithm.
   */
  public LocalRateLimiter(int defaultReplenishRate, ApiRateLimiterServiceImpl apiRateLimiterService,
      BlockingQueue<LockedUserInformationDto> rateLimiterBlockingQueue) {
    super(Config.class, CONFIGURATION_PROPERTY_NAME, (ConfigurationService) null);
    this.defaultConfig = new Config().setReplenishRate(defaultReplenishRate);
    this.apiAccessRate = apiRateLimiterService.getApiAccessRate();//get map
    this.lockedUser = apiRateLimiterService.getLockedUser();//get map
    this.cacheRateLimiter = apiRateLimiterService.getCacheRateLimiter();
    this.rateLimiterBlockingQueue = rateLimiterBlockingQueue;
  }


  public boolean isIncludeHeaders() {
    return includeHeaders;
  }


  /**
   * Used when setting default configuration in constructor.
   *
   * @param context the ApplicationContext object to be used by this object
   * @throws BeansException if thrown by application context methods
   */
  @Override
  @SuppressWarnings("unchecked")
  public void setApplicationContext(ApplicationContext context) throws BeansException {
    if (initialized.compareAndSet(false, true)) {
      if (context.getBeanNamesForType(ConfigurationService.class).length > 0) {
        setConfigurationService(context.getBean(ConfigurationService.class));
      }
    }
  }

  private RateLimiterConfig createRateLimiterConfig(int refreshPeriod,
      int replenishRate) {
    return RateLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(0))
        .limitRefreshPeriod(Duration.ofSeconds(refreshPeriod))
        .limitForPeriod(replenishRate).build();
  }


  /**
   *
   * 限流的核心逻辑
   * 用户被锁定 和 请求超过限制都会返回 429
   *
   * @param routeId
   * @param key
   * @return
   */
  @Override
  @SuppressWarnings("unchecked")
  public Mono<Response> isAllowed(String routeId, String key) {


    // 错误的调用，由下游提示为啥错误
    String[] split = key.split(SEPARATOR);
    if (split.length != 2) {
      return Mono.just(new Response(true, Collections.EMPTY_MAP));
    }

    String apiPath = split[0];
    String userId = split[1];
    
    if (lockedUser.containsKey(key)) {
      Long unlockTime = lockedUser.get(key);
      if (null != unlockTime) {
        if (unlockTime > System.currentTimeMillis()) {
          LOG.info("当前用户被锁定: " + userId);
          return Mono.just(new Response(false, Collections.EMPTY_MAP));
        } else {
          //删除过期key
          lockedUser.remove(key);
        }
      }
    }

    if (!apiAccessRate.containsKey(apiPath)) {
      return Mono.just(new Response(true, Collections.EMPTY_MAP));
    }


    ApiRateLimiterServiceImpl.Config config = apiAccessRate.get(apiPath);
    //并发访问时可能被删除
    if (config == null) {
      return Mono.just(new Response(true, Collections.EMPTY_MAP));
    }

    int replenishRate = config.getReplenishRate();
    InMemoryRateLimiterRegistry inMemoryRateLimiterRegistry = cacheRateLimiter.computeIfAbsent(apiPath,
        s -> new InMemoryRateLimiterRegistry(new Builder().build()));

    final io.github.resilience4j.ratelimiter.RateLimiter rateLimiter = inMemoryRateLimiterRegistry
        .rateLimiter(apiPath, createRateLimiterConfig(1, replenishRate));

    final boolean allowed = rateLimiter.acquirePermission(1);

    if (!allowed) {
      long startLockTime = System.currentTimeMillis();
      long unlockedUserTime = startLockTime + config.getLockTime();
      lockedUser.put(key, unlockedUserTime);
      if (!rateLimiterBlockingQueue.offer(
          new LockedUserInformationDto(apiPath, userId, unlockedUserTime, startLockTime, config.getLockTime()))) {
        LOG.error("存储锁定用户队列满了，可能原因：数据库链接异常");
      }
      LOG.info("锁定用户数据写入队列："+key);
    }

    return Mono.just(new Response(allowed, Collections.EMPTY_MAP));
  }


  @Validated
  public static class Config {

    @Min(1)
    private int replenishRate;

    @Min(1)
    private int refreshPeriod = 1;

    @Min(1)
    private int requestedTokens = 1;

    public int getReplenishRate() {
      return replenishRate;
    }

    public Config setReplenishRate(int replenishRate) {
      this.replenishRate = replenishRate;
      return this;
    }

    public int getRefreshPeriod() {
      return refreshPeriod;
    }

    public Config setRefreshPeriod(int refreshPeriod) {
      this.refreshPeriod = refreshPeriod;
      return this;
    }

    public int getRequestedTokens() {
      return requestedTokens;
    }

    public Config setRequestedTokens(int requestedTokens) {
      this.requestedTokens = requestedTokens;
      return this;
    }

    @Override
    public String toString() {
      return new ToStringCreator(this).append("replenishRate", replenishRate)
          .append("refreshPeriod", refreshPeriod)
          .append("requestedTokens", requestedTokens).toString();
    }
  }
}