package com.animal.farm.application.module.gateway.rateLimiter;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.HasRouteId;
import org.springframework.cloud.gateway.support.HttpStatusHolder;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;

import com.animal.farm.infrastructure.foundation.util.JsonUtil;
import com.animal.farm.infrastructure.foundation.web.MessageCode;
import com.animal.farm.infrastructure.foundation.web.Result;

import reactor.core.publisher.Mono;

/**
 * @author zhouzhiyuan
 * @date 2022/10/26 17:17
 */
@Component
public class LocalRateLimiterGetWayFilterFactory extends
    AbstractGatewayFilterFactory<LocalRateLimiterGetWayFilterFactory.Config> {

  /**
   * Key-Resolver key.
   */
  public static final String KEY_RESOLVER_KEY = "keyResolver";

  private static final String EMPTY_KEY = "____EMPTY_KEY__";

  private final RateLimiter defaultRateLimiter;

  private final KeyResolver defaultKeyResolver;

  /**
   * Switch to deny requests if the Key Resolver returns an empty key, defaults to true.
   */
  private boolean denyEmptyKey = true;

  /**
   * HttpStatus to return when denyEmptyKey is true, defaults to FORBIDDEN.
   */
  private String emptyKeyStatusCode = HttpStatus.FORBIDDEN.name();

  public LocalRateLimiterGetWayFilterFactory(RateLimiter defaultRateLimiter,
      KeyResolver defaultKeyResolver) {
    super(Config.class);
    this.defaultRateLimiter = defaultRateLimiter;
    this.defaultKeyResolver = defaultKeyResolver;
  }

  public KeyResolver getDefaultKeyResolver() {
    return defaultKeyResolver;
  }

  public RateLimiter getDefaultRateLimiter() {
    return defaultRateLimiter;
  }

  public boolean isDenyEmptyKey() {
    return denyEmptyKey;
  }

  public void setDenyEmptyKey(boolean denyEmptyKey) {
    this.denyEmptyKey = denyEmptyKey;
  }

  public String getEmptyKeyStatusCode() {
    return emptyKeyStatusCode;
  }

  public void setEmptyKeyStatusCode(String emptyKeyStatusCode) {
    this.emptyKeyStatusCode = emptyKeyStatusCode;
  }

  @SuppressWarnings("unchecked")
  @Override
  public GatewayFilter apply(
      Config config) {
    KeyResolver resolver = getOrDefault(config.keyResolver, defaultKeyResolver);
    RateLimiter<Object> limiter = getOrDefault(config.rateLimiter,
        defaultRateLimiter);
    boolean denyEmpty = getOrDefault(config.denyEmptyKey, this.denyEmptyKey);
    HttpStatusHolder emptyKeyStatus = HttpStatusHolder
        .parse(getOrDefault(config.emptyKeyStatus, this.emptyKeyStatusCode));

    return (exchange, chain) -> resolver.resolve(exchange).defaultIfEmpty(EMPTY_KEY)
        .flatMap(key -> {
          if (EMPTY_KEY.equals(key)) {
            //空key
            if (denyEmpty) {
              ServerWebExchangeUtils.setResponseStatus(exchange, emptyKeyStatus);
              return writeResponse(exchange, MessageCode.ERROR_403_403);
            }
            return chain.filter(exchange);
          }
          String routeId = config.getRouteId();
          if (routeId == null) {
            Route route = exchange
                .getAttribute(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            routeId = route.getId();
          }
          return limiter.isAllowed(routeId, key).flatMap(response -> {

            for (Map.Entry<String, String> header : response.getHeaders()
                .entrySet()) {
              exchange.getResponse().getHeaders().add(header.getKey(),
                  header.getValue());
            }

            if (response.isAllowed()) {
              return chain.filter(exchange);
            }
            //isAllowd=false时，返回429， too may reqests
            ServerWebExchangeUtils.setResponseStatus(exchange, config.getStatusCode());
//            return exchange.getResponse().setComplete();
            return writeResponse(exchange, MessageCode.ERROR_429);

          });
        });
  }

  private <T> T getOrDefault(T configValue, T defaultValue) {
    return (configValue != null) ? configValue : defaultValue;
  }

  /**
   * 往response里写json body
   * @param exchange
   * @return
   */
  public Mono<Void> writeResponse(ServerWebExchange exchange, MessageCode messageCode) {
    ServerHttpResponse response = exchange.getResponse();
    // 设置响应的内容类型为JSON
    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
    Result<Void> result = Result
        .error(messageCode.getCode(), messageCode.getMessage());
    // 写入JSON响应数据
    return response.writeWith(Mono
        .just(response
            .bufferFactory()
            .wrap(JsonUtil.writeValueAsString(result)
                .getBytes(StandardCharsets.UTF_8))));
  }

  @Override
  public String name() {
    return "LocalRateLimiter";
  }

  public static class Config implements HasRouteId {

    private KeyResolver keyResolver;

    private RateLimiter rateLimiter;

    private HttpStatus statusCode = HttpStatus.TOO_MANY_REQUESTS;

    private Boolean denyEmptyKey;

    private String emptyKeyStatus;

    private String routeId;

    public KeyResolver getKeyResolver() {
      return keyResolver;
    }

    public Config setKeyResolver(KeyResolver keyResolver) {
      this.keyResolver = keyResolver;
      return this;
    }

    public RateLimiter getRateLimiter() {
      return rateLimiter;
    }

    public Config setRateLimiter(RateLimiter rateLimiter) {
      this.rateLimiter = rateLimiter;
      return this;
    }

    public HttpStatus getStatusCode() {
      return statusCode;
    }

    public Config setStatusCode(HttpStatus statusCode) {
      this.statusCode = statusCode;
      return this;
    }

    public Boolean getDenyEmptyKey() {
      return denyEmptyKey;
    }

    public Config setDenyEmptyKey(Boolean denyEmptyKey) {
      this.denyEmptyKey = denyEmptyKey;
      return this;
    }

    public String getEmptyKeyStatus() {
      return emptyKeyStatus;
    }

    public Config setEmptyKeyStatus(String emptyKeyStatus) {
      this.emptyKeyStatus = emptyKeyStatus;
      return this;
    }

    @Override
    public void setRouteId(String routeId) {
      this.routeId = routeId;
    }

    @Override
    public String getRouteId() {
      return this.routeId;
    }
  }
}
